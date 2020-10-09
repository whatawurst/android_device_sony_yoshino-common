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

import android.app.Service
import android.content.Context
import android.database.ContentObserver
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Handler
import android.os.UserHandle
import android.provider.Settings
import android.telephony.CellSignalStrength
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import java.io.File
import java.util.*

class NetworkModeObserver(private val context: Context) : ContentObserver(Handler(context.mainLooper)) {

    private var registered = false

    private var onChange: () -> Unit = {}
    private var subID = SubscriptionManager.INVALID_SUBSCRIPTION_ID

    fun register(subID: Int, onChange: () -> Unit) {
        if (!registered && subID != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            this.subID = subID
            this.onChange = onChange

            context.contentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.PREFERRED_NETWORK_MODE + subID),
                    false, this, UserHandle.USER_CURRENT)
            registered = true
            d("NetworkModeObserver: Registered")
        }
    }

    fun unregister() {
        if (registered) {
            context.contentResolver.unregisterContentObserver(this)
            registered = false
            d("NetworkModeObserver: Unregistered")
        }
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (!selfChange) {
            if (uri != null && uri == Settings.Global.getUriFor(Settings.Global.PREFERRED_NETWORK_MODE + subID)) {
                onChange()
            }
        }
    }

    private fun d(msg: String) {
        Log.d("NetworkSwitcher", msg)
        log(msg, context)
    }
}

/**
 * An observer class to observe the status of airplane mode
 *
 * @author shank03
 */
class AirplaneModeObserver(private val context: Context) : ContentObserver(Handler(context.mainLooper)) {

    private var onChange: (uri: Uri?) -> Unit = {}
    private var registered = false

    fun register(onChange: (uri: Uri?) -> Unit) {
        if (!registered) {
            this.onChange = onChange
            context.contentResolver.registerContentObserver(Settings.System.getUriFor(Settings.Global.AIRPLANE_MODE_ON),
                    false, this, UserHandle.USER_CURRENT)
            registered = true
            d("AirplaneModeObserver: Registered")
        }
    }

    fun unregister() {
        if (registered) {
            context.contentResolver.unregisterContentObserver(this)
            registered = false
            d("AirplaneModeObserver: Unregistered")
        }
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (!selfChange) {
            onChange(uri)
        }
    }

    private fun d(msg: String) {
        Log.d("NetworkSwitcher", msg)
        log(msg, context)
    }
}

/**
 * A class to observe the SIM signal strength to determine
 * whether it's in service
 *
 * @author shank03
 */
class SimServiceObserver(private val context: Context) {

    private var registered = false

    private val handler by lazy { Handler(context.mainLooper) }
    private val runnable = object : Runnable {
        override fun run() {
            try {
                synchronized(Object()) {
                    if (subID != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                        val tm: TelephonyManager = context.getSystemService(TelephonyManager::class.java).createForSubscriptionId(subID)
                        if (tm.signalStrength != null && tm.signalStrength?.level != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                            d("SimServiceObserver: SIM in service, event sent.")
                            onConnected()
                            unregister()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                d("SimServiceObserver: ERROR ${e.message}\n${e.printStackTrace()}")
            } finally {
                handler.postDelayed(this, 2000)
            }
        }
    }

    private var onConnected: () -> Unit = {}
    private var subID = SubscriptionManager.INVALID_SUBSCRIPTION_ID

    fun register(subID: Int, onConnected: () -> Unit) {
        if (!registered) {
            this.subID = subID
            this.onConnected = onConnected

            handler.post(runnable)

            registered = true
            d("SimServiceObserver: Registered")
        }
    }

    fun unregister() {
        if (registered) {
            onConnected = {}
            subID = SubscriptionManager.INVALID_SUBSCRIPTION_ID

            handler.removeCallbacks(runnable)

            registered = false
            d("SimServiceObserver: Unregistered")
        }
    }

    private fun d(msg: String) {
        Log.d("NetworkSwitcher", msg)
        log(msg, context)
    }
}

/**
 * A class to observe the IMS registration status
 *
 * @author shank03
 */
class ImsRegistrationObserver(private val context: Context) {

    private var registered = false

    private val handler by lazy { Handler(context.mainLooper) }
    private val runnable = object : Runnable {
        override fun run() {
            try {
                synchronized(Object()) {
                    if (subID != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                        val tm = context.getSystemService(TelephonyManager::class.java).createForSubscriptionId(subID)
                        if (tm.isImsRegistered(subID)) {
                            d("ImsRegistrationObserver: IMS registered, event sent.")
                            onRegistered()
                            unregister()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                d("ImsRegistrationObserver: ERROR ${e.message}\n${e.printStackTrace()}")
            } finally {
                handler.postDelayed(this, 2000)
            }
        }
    }

    private var onRegistered: () -> Unit = {}
    private var subID = SubscriptionManager.INVALID_SUBSCRIPTION_ID

    fun register(subID: Int, onRegistered: () -> Unit) {
        if (!registered) {
            this.subID = subID
            this.onRegistered = onRegistered

            handler.post(runnable)

            registered = true
            d("ImsRegistrationObserver: Registered")
        }
    }

    fun unregister() {
        if (registered) {
            onRegistered = {}
            subID = SubscriptionManager.INVALID_SUBSCRIPTION_ID

            handler.removeCallbacks(runnable)

            registered = false
            d("ImsRegistrationObserver: Unregistered")
        }
    }

    private fun d(msg: String) {
        Log.d("NetworkSwitcher", msg)
        log(msg, context)
    }
}

/**
 * Write logs
 *
 * Dir: /data/data/com.yoshino.networkswitcher/files/ns.log
 */
fun log(msg: String, context: Context) {
    val logFile = File(context.filesDir, "ns.log")
    try {
        val log = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault()).format(System.currentTimeMillis()) + ": $msg\r\n"
        context.openFileOutput("ns.log", if (logFile.exists()) Service.MODE_APPEND else Service.MODE_PRIVATE).use { it.write(log.toByteArray()) }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
