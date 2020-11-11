/*
 * Copyright (c) 2020, Shashank Verma (shank03) <shashank.verma2002@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.yoshino.networkswitcher

import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.telephony.CellSignalStrength
import android.telephony.SubscriptionManager
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.internal.telephony.RILConstants
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Service to handle Network mode
 *
 * - On shutdown/reboot, get the preference if network was 3G
 * - If no, then toggle to 3G and the system process continues else do nothing
 * - On boot, if preference was 3G, do nothing, else toggle to LTE
 *
 * @author shank03
 */
class NetworkSwitcher : Service() {

    companion object {
        private const val TAG = "NetworkSwitcher"

        private val DEFAULT_MODEMS = arrayOf("amss_fsg_lilac_tar.mbn",
                "amss_fsg_poplar_tar.mbn", "amss_fsg_poplar_dsds_tar.mbn",
                "amss_fsg_maple_tar.mbn", "amss_fsg_maple_dsds_tar.mbn")

        private const val MODEM_SWITCHER_STATUS = "/cache/modem/modem_switcher_status"
    }

    override fun onBind(intent: Intent): IBinder? = null

    private val sm by lazy { getSystemService(SubscriptionManager::class.java) }
    private val pm by lazy { getSystemService(PowerManager::class.java) }

    private val helper by lazy { NotificationHelper(applicationContext) }

    private val airplaneModeObserver by lazy { AirplaneModeObserver(applicationContext) }
    private val simServiceObserver by lazy { SimServiceObserver(applicationContext) }

    // Global variable to be accessed on shutdown
    private var mSubID = SubscriptionManager.INVALID_SUBSCRIPTION_ID

    /**
     * The [subscriptionsChangedListener] is called every time something changes
     * in SIM connectivity.
     *
     * This boolean will make sure that the network toggle takes place only once.
     */
    private var changedOnBoot = false

    /**
     * The [wasModemCSWorkCompleted] checks on boot if the modem task was completed.
     * If it wasn't completed, flag this boolean false.
     *
     * Now this flagged boolean will make sure that it doesn't toggle network on
     * shutdown/reboot because modem work was incomplete at boot.
     */
    private var wasModemDone = true

    /**
     * The [subscriptionsChangedListener] triggers very quickly (in special cases).
     *
     * This boolean make sure that [task] isn't interrupted when in process.
     */
    private var delayedTaskCompleted = true

    /**
     * Broadcast receiver to perform network toggle on shutdown/reboot
     */
    private val shutDownReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            action ?: return

            if (action == Intent.ACTION_SHUTDOWN || action == Intent.ACTION_REBOOT) {
                d("onReceive: Action received: $action")
                if (mSubID == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    d("onReceive: Could not perform network switch; mSubID = $mSubID")
                    return
                }
                task(mSubID, false)
            }
        }
    }

    private val subscriptionsChangedListener: OnSubscriptionsChangedListener = object : OnSubscriptionsChangedListener() {
        override fun onSubscriptionsChanged() {
            super.onSubscriptionsChanged()
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                d("${Manifest.permission.READ_PHONE_STATE} was denied.")
                return
            }

            synchronized(Object()) {
                if (!changedOnBoot && delayedTaskCompleted) {
                    val list = sm.activeSubscriptionInfoList
                    d("onSubscriptionsChanged: list size ${list.size}")
                    if (list.size >= 1) {
                        // TODO: dual sim
                        mSubID = list[0].subscriptionId
                        if (mSubID == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                            d("onSubscriptionsChanged: Cannot continue. Subscription ID is invalid; $mSubID")
                            return
                        }

                        if (Preference.getWasNetwork3G(applicationContext, false)) {
                            changedOnBoot = true
                            d("onSubscriptionsChanged: User pref was 3G; Registering network observer")

                            val imsObserver = ImsRegistrationObserver(applicationContext)
                            val networkObserver = NetworkModeObserver(applicationContext)

                            networkObserver.register(mSubID) {
                                if (isLTE(getPreferredNetwork(mSubID))) {
                                    helper.notifyToggleNotification("Modem setup in progress ...")

                                    if (isModemDefault()) {
                                        networkObserver.unregister()
                                        postCompletionNotification(mSubID, "Not available (default modem)")
                                    } else {
                                        postCompletionNotification(mSubID, "Registration in progress ...")
                                        imsObserver.register(mSubID) {
                                            networkObserver.unregister()
                                            postCompletionNotification(mSubID, "Registered (Available)")
                                        }
                                    }
                                } else {
                                    helper.cancel()
                                    imsObserver.unregister()
                                }
                            }
                            return
                        }

                        helper.notifyToggleNotification("Modem setup in progress ...")

                        if (isAirplaneModeOn()) {
                            changedOnBoot = true
                            d("onSubscriptionsChanged: Airplane mode was ON. Waiting ...")
                            helper.notifyToggleNotification("Airplane mode is ON. Waiting ...")

                            airplaneModeObserver.register {
                                if (it != null && it == Settings.System.getUriFor(Settings.Global.AIRPLANE_MODE_ON)) {
                                    if (isAirplaneModeOn()) {
                                        helper.notifyToggleNotification("Airplane mode is ON. Waiting ...")
                                        simServiceObserver.unregister()
                                    } else {
                                        d("onSubscriptionsChanged: SIM not in service. Waiting ...")
                                        helper.notifyToggleNotification("SIM not in service. Waiting ...")

                                        simServiceObserver.register(mSubID) {
                                            task(mSubID, true)
                                            airplaneModeObserver.unregister()
                                        }
                                    }
                                }
                            }
                        } else {
                            // Delay 2 sec, not to immediately react
                            delayedTaskCompleted = false
                            delay(2000) { task(mSubID, true) }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        d("-------------------------------------")
        d("Service started")
        Settings.System.putString(contentResolver, "ns_status", "App didn't run")

        // Start process once user unlocks the device
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                unregisterReceiver(this)
                sm.addOnSubscriptionsChangedListener(subscriptionsChangedListener)
            }
        }, IntentFilter(Intent.ACTION_USER_UNLOCKED))

        // Register shutdown/reboot receiver
        registerReceiver(shutDownReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SHUTDOWN)
            addAction(Intent.ACTION_REBOOT)
        })

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    /**
     * This method prepares and performs some important checks before [toggle]
     */
    private fun task(subID: Int, isBoot: Boolean) {
        try {
            if (!wasModemCSWorkCompleted()) {
                d("task: Modem Work was not completed. Skipping Toggle task")
                wasModemDone = false
                delayedTaskCompleted = true
                helper.cancel()
                return
            }

            val tm = getSystemService(TelephonyManager::class.java).createForSubscriptionId(subID)

            val currentNetwork = getPreferredNetwork(subID)
            d("task: Current network = " + logPrefNetwork(currentNetwork) + "; $currentNetwork")

            // Continue the toggle task
            if (isBoot) {
                d("task: |[Boot task]|")

                if (!Preference.getPreferenceStored(applicationContext) && !isModemDefault()) {
                    d("task: App preferences missing AND modem flashed is NOT default. Prompting reboot")
                    val dialog = AlertDialog.Builder(applicationContext, R.style.AppTheme)
                            .setTitle("IMS Setup")
                            .setMessage("Your device requires a reboot for completion of IMS setup.")
                            .setPositiveButton("Reboot") { dialog, _ ->
                                changedOnBoot = true
                                postCompletionNotification(subID, "Reboot required for completion")

                                // One at a time boys... one at a time
                                synchronized(Object()) {
                                    task(subID, false)
                                    dialog.dismiss()
                                    d("task: Rebooting")
                                    pm.reboot("IMS Implementation")
                                }
                            }.create()

                    if (dialog.window != null) {
                        dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                        dialog.setCancelable(false)
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.show()
                    } else {
                        d("task: Error: dialog window was NULL")
                    }
                    return
                }

                if (tm.signalStrength != null && tm.signalStrength?.level != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                    if (isLTE(currentNetwork)) {
                        d("task: Network is LTE. Not toggling")
                    } else {
                        d("task: User pref was LTE; Toggling ...")
                        toggle(tm, subID, currentNetwork)
                    }
                    changedOnBoot = true
                    if (isModemDefault()) {
                        postCompletionNotification(subID, "Not available (default modem)")
                    } else {
                        postCompletionNotification(subID, "Registration in progress ...")
                        ImsRegistrationObserver(applicationContext).register(subID) {
                            postCompletionNotification(subID, "Registered (Available)")
                        }
                    }
                } else {
                    d("task: SIM not in service. Waiting ...")
                    helper.notifyToggleNotification("SIM not in service. Waiting ...")

                    SimServiceObserver(applicationContext).register(subID) { task(subID, isBoot) }
                    changedOnBoot = true
                    delayedTaskCompleted = true
                }
            } else {
                d("task: |[Shutdown/reboot task]|")

                if (wasModemDone) {

                    val lte = isLTE(currentNetwork)
                    if (lte) {
                        d("task: Current network is LTE; Toggling ...")
                        toggle(tm, subID, currentNetwork)
                    } else {
                        d("task: Current network was NOT LTE; Not toggling")
                    }
                    Preference.putWasNetwork3G(!lte, applicationContext)
                } else {
                    d("task: Modem task was incomplete when checked on boot, skipping ...")
                }
                d("-------------------------------------")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            d("task: ERROR ${e.message}\n${e.printStackTrace()}")
        }
        delayedTaskCompleted = true
    }

    /**
     * The method to toggle the network
     *
     * @param tm             [TelephonyManager] specific to subID
     * @param subID          the subscription ID from [subscriptionsChangedListener]
     * @param currentNetwork current preferred network mode from [task]
     */
    private fun toggle(tm: TelephonyManager, subID: Int, currentNetwork: Int) {
        val networkToChange = getToggledNetwork(currentNetwork)
        d("toggle: To be changed to = " + logPrefNetwork(networkToChange))

        if (networkToChange == -99) {
            d("toggle: Couldn't get proper network to change")
            return
        }

        if (tm.setPreferredNetworkType(subID, networkToChange)) {
            Settings.Global.putInt(applicationContext.contentResolver, Settings.Global.PREFERRED_NETWORK_MODE + subID, networkToChange)
            d("toggle: Successfully changed to " + logPrefNetwork(networkToChange))
        }
    }

    /**
     * Get the current in-use network mode preference
     *
     * @return default 3G [RILConstants.NETWORK_MODE_WCDMA_PREF] if no pref stored
     */
    private fun getPreferredNetwork(subID: Int): Int = Settings.Global.getInt(applicationContext.contentResolver,
            Settings.Global.PREFERRED_NETWORK_MODE + subID, RILConstants.NETWORK_MODE_WCDMA_PREF)

    /**
     * Returns whether
     *
     * @param network is LTE or not
     */
    private fun isLTE(network: Int): Boolean {
        return network == RILConstants.NETWORK_MODE_GLOBAL
               || network == RILConstants.NETWORK_MODE_LTE_CDMA_EVDO
               || network == RILConstants.NETWORK_MODE_LTE_GSM_WCDMA
               || network == RILConstants.NETWORK_MODE_LTE_ONLY
               || network == RILConstants.NETWORK_MODE_LTE_WCDMA
               || network == RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA
               || network == RILConstants.NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA
               || network == RILConstants.NETWORK_MODE_LTE_TDSCDMA_WCDMA
               || network == RILConstants.NETWORK_MODE_LTE_TDSCDMA_GSM
               || network == RILConstants.NETWORK_MODE_LTE_TDSCDMA
               || network == RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA
    }

    /**
     * This method returns the toggled network between 3G and LTE
     */
    private fun getToggledNetwork(currentNetwork: Int): Int = when (currentNetwork) {
        // 3G to LTE
        RILConstants.NETWORK_MODE_WCDMA_PREF, RILConstants.NETWORK_MODE_WCDMA_ONLY,
        RILConstants.NETWORK_MODE_GSM_UMTS, RILConstants.NETWORK_MODE_GSM_ONLY -> RILConstants.NETWORK_MODE_LTE_GSM_WCDMA

        // LTE to 3G
        RILConstants.NETWORK_MODE_LTE_GSM_WCDMA, RILConstants.NETWORK_MODE_LTE_ONLY,
        RILConstants.NETWORK_MODE_LTE_WCDMA, RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA -> RILConstants.NETWORK_MODE_WCDMA_PREF

        // Global to GSM ?, vice-versa
        RILConstants.NETWORK_MODE_GLOBAL -> RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA
        RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA -> RILConstants.NETWORK_MODE_WCDMA_PREF
        else -> -99
    }

    /**
     * Get the string version of the variables.
     *
     * Too lazy to refer the [RILConstants]
     */
    private fun logPrefNetwork(network: Int): String = when (network) {
        RILConstants.NETWORK_MODE_WCDMA_PREF -> "NETWORK_MODE_WCDMA_PREF"
        RILConstants.NETWORK_MODE_WCDMA_ONLY -> "NETWORK_MODE_WCDMA_ONLY"
        RILConstants.NETWORK_MODE_GSM_UMTS -> "NETWORK_MODE_GSM_UMTS"
        RILConstants.NETWORK_MODE_GSM_ONLY -> "NETWORK_MODE_GSM_ONLY"
        RILConstants.NETWORK_MODE_LTE_GSM_WCDMA -> "NETWORK_MODE_LTE_GSM_WCDMA"
        RILConstants.NETWORK_MODE_LTE_ONLY -> "NETWORK_MODE_LTE_ONLY"
        RILConstants.NETWORK_MODE_LTE_WCDMA -> "NETWORK_MODE_LTE_WCDMA"
        RILConstants.NETWORK_MODE_GLOBAL -> "NETWORK_MODE_GLOBAL"
        RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA -> "NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA"
        RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA -> "NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA --> NETWORK_MODE_WCDMA_PREF"
        else -> "N/A"
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @return true if enabled.
     */
    private fun isAirplaneModeOn(): Boolean = Settings.System.getInt(applicationContext.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0

    /**
     * This method is to check if work of modem and CS was completed by
     * reading the cache file.
     */
    private fun wasModemCSWorkCompleted(): Boolean = File(MODEM_SWITCHER_STATUS).exists()

    /**
     * Post a notification with modem info on completion of [task]
     *
     * @param subID is the subscription ID either from [subscriptionsChangedListener] or [task]
     * @param registration is registration message if needed to be added to notification
     */
    private fun postCompletionNotification(subID: Int, registration: String) {
        val modemConfig = readModemFile()
        if (modemConfig != null) helper.notifyModemNotification(subID, modemConfig[1], modemConfig[0], registration)
    }

    /**
     * @return if modem flashed is one of the [DEFAULT_MODEMS]
     * or whether modem name contains one of `ims`, `volte`, `vilte` or `vowifi`
     */
    private fun isModemDefault(): Boolean {
        val modemConfig = readModemFile()
        return if (modemConfig != null) {
            for (m in DEFAULT_MODEMS) {
                if (modemConfig[1] == m) return true
            }
            !modemConfig[1].contains("ims") && !modemConfig[1].contains("volte")
                    && !modemConfig[1].contains("vilte") && !modemConfig[1].contains("vowifi")
        } else true
    }

    /**
     * Reads [MODEM_SWITCHER_STATUS] file
     *
     * @return String array of [0] being status and [1] being modem config flashed
     */
    private fun readModemFile(): Array<String>? {
        return try {
            val file = File(MODEM_SWITCHER_STATUS)
            if (file.exists()) {
                var line = ""
                BufferedReader(FileReader(file)).use { br -> br.forEachLine { line += it } }
                line = line.replace("\n", "").replace("\r", "")
                        .replace("{", "").replace("}", "").replace("\"", "").trim()

                if (line == "") null else arrayOf(line.split(",".toRegex())[0], line.split(",".toRegex())[1])
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Write logs to [log]
     */
    private fun d(msg: String) {
        Log.d(TAG, msg)
        log(msg, applicationContext)
    }

    fun delay(milli: Long, block: () -> Unit) {
        Handler(mainLooper).postDelayed({ block() }, milli)
    }
}
