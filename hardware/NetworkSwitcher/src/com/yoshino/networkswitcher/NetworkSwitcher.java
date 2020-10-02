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

package com.yoshino.networkswitcher;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.CellSignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.ims.ImsManager;
import com.android.internal.telephony.RILConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;
import java.util.Locale;

/**
 * Service to handle Network mode
 * <p>
 * - On shutdown/reboot, get the preference if network was 3G
 * - If no, then toggle to 3G and the system process continues else do nothing
 * <p>
 * - On boot, if preference was 3G, do nothing, else toggle to LTE
 *
 * @author shank03
 */
public class NetworkSwitcher extends Service {

    private static final String TAG = "NetworkSwitcher";

    private static final String[] DEFAULT_MODEMS = {"amss_fsg_lilac_tar.mbn",
            "amss_fsg_poplar_tar.mbn", "amss_fsg_poplar_dsds_tar.mbn",
            "amss_fsg_maple_tar.mbn", "amss_fsg_maple_dsds_tar.mbn"};

    private static final String MODEM_SWITCHER_STATUS = "/cache/modem/modem_switcher_status";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    SubscriptionManager sm;
    PowerManager pm;

    // Global variable to be accessed on shutdown
    int mSubID = -99;

    /**
     * The {@link #subscriptionsChangedListener} is called every time something changes
     * in SIM connectivity.
     * <p>
     * This boolean will make sure that the network toggle takes place only once.
     */
    boolean changedOnBoot = false;

    /**
     * The {@link #wasModemCSWorkCompleted()} checks on boot if the modem task was completed.
     * If it wasn't completed, flag this boolean false.
     * <p>
     * Now this flagged boolean will make sure that it doesn't toggle network on
     * shutdown/reboot because modem work was incomplete at boot.
     */
    boolean wasModemDone = true;

    /**
     * The {@link #subscriptionsChangedListener} triggers very quickly (in special cases).
     * <p>
     * This boolean make sure that {@link #task(int, boolean)} isn't interrupted when in process.
     */
    boolean delayedTaskCompleted = true;

    /**
     * Broadcast receiver to perform network toggle on shutdown/reboot
     */
    private BroadcastReceiver shutDownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_SHUTDOWN) || action.equals(Intent.ACTION_REBOOT)) {
                    d("onReceive: Action received: " + action);
                    if (mSubID == -99) {
                        d("onReceive: Could not perform network switch; mSubID = " + mSubID);
                        return;
                    }
                    task(mSubID, false);
                    d("-------------------------------------");
                }
            }
        }
    };

    private SubscriptionManager.OnSubscriptionsChangedListener subscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        @Override
        public void onSubscriptionsChanged() {
            super.onSubscriptionsChanged();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                d(Manifest.permission.READ_PHONE_STATE + " was denied.");
                return;
            }

            if (!changedOnBoot && delayedTaskCompleted) {
                if (isAirplaneModeOn()) {
                    d("onSubscriptionsChanged: Airplane mode was ON. Waiting ...");
                    return;
                }

                List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
                d("onSubscriptionsChanged: list size " + list.size());

                if (list.size() >= 1) {
                    // TODO: dual sim
                    mSubID = list.get(0).getSubscriptionId();

                    // Delay 2 sec, not to immediately react
                    delayedTaskCompleted = false;
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            task(mSubID, true);
                        }
                    }, 2000);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        d("-------------------------------------");
        d("Service started");
        sm = getSystemService(SubscriptionManager.class);
        sm.addOnSubscriptionsChangedListener(subscriptionsChangedListener);

        pm = getSystemService(PowerManager.class);

        // Register shutdown/reboot receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_REBOOT);
        registerReceiver(shutDownReceiver, filter);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * This method prepares and performs some important checks before {@link #toggle(TelephonyManager, int, int)}
     */
    private void task(final int subID, final boolean isBoot) {
        if (subID == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            d("task: Cannot continue. Subscription ID is invalid; " + subID);
            delayedTaskCompleted = true;
            return;
        }
        if (!wasModemCSWorkCompleted()) {
            d("task: Modem Work was not completed. Skipping Toggle task");
            wasModemDone = false;
            delayedTaskCompleted = true;
            return;
        }

        TelephonyManager tm = getSystemService(TelephonyManager.class).createForSubscriptionId(subID);
        PersistableBundle carrierConfig = getSystemService(CarrierConfigManager.class).getConfigForSubId(subID);
        ImsManager imsManager = ImsManager.getInstance(getApplicationContext(), SubscriptionManager.getPhoneId(subID));

        int currentNetwork = getPreferredNetwork(subID);
        d("task: Current network = " + logPrefNetwork(currentNetwork) + "; " + currentNetwork);

        // Continue the toggle task
        if (isBoot) {
            d("task: Boot task");

            if (!Preference.getPreferenceStored(getApplicationContext()) && !isModemDefault()) {
                d("task: App preferences missing AND modem flashed is NOT default. Prompting reboot");
                AlertDialog dialog = new AlertDialog.Builder(getApplicationContext(), R.style.AppTheme)
                        .setTitle("IMS Setup")
                        .setMessage("Your device requires a reboot for completion of IMS setup.")
                        .setPositiveButton("Reboot", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changedOnBoot = true;

                                // One at a time boys... one at a time
                                synchronized (this) {
                                    task(subID, false);
                                    dialog.dismiss();
                                    d("task: Rebooting");
                                    pm.reboot("IMS Implementation");
                                }
                            }
                        }).create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                } else {
                    d("task: Error: dialog window was NULL");
                }
                return;
            }

            if (tm.getSignalStrength() == null ||
                    tm.getSignalStrength().getLevel() == CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                d("task: SIM not in service. Waiting ...");
                delayedTaskCompleted = true;
                return;
            }

            if (Preference.getWasNetwork3G(getApplicationContext(), !isLTE(currentNetwork))) {
                d("task: User pref was 3G; Not toggling");
            } else {
                d("task: User pref was LTE; Toggling ...");
                toggle(tm, subID, currentNetwork);
                handle4GVoLteToggle(tm, imsManager, carrierConfig, subID, true);
            }
            changedOnBoot = true;
        } else {
            d("task: Shutdown/reboot task");

            if (wasModemDone) {
                handle4GVoLteToggle(tm, imsManager, carrierConfig, subID, false);

                boolean lte = isLTE(currentNetwork);
                if (lte) {
                    d("task: Current network is LTE; Toggling ...");
                    toggle(tm, subID, currentNetwork);
                } else {
                    d("task: Current network was NOT LTE; Not toggling");
                }
                Preference.putWasNetwork3G(!lte, getApplicationContext());
            } else {
                d("task: Modem task was incomplete when checked on boot, skipping ...");
            }
        }
        delayedTaskCompleted = true;
    }

    /**
     * The method to toggle the network
     *
     * @param tm             {@link TelephonyManager} specific to subID
     * @param subID          the subscription ID from {@link #subscriptionsChangedListener}
     * @param currentNetwork current preferred network mode from {@link #task(int, boolean)}
     */
    private void toggle(TelephonyManager tm, int subID, int currentNetwork) {
        int networkToChange = getToggledNetwork(currentNetwork);
        d("toggle: To be changed to = " + logPrefNetwork(networkToChange));

        if (networkToChange == -99) {
            d("toggle: Couldn't get proper network to change");
            return;
        }

        if (tm.setPreferredNetworkType(subID, networkToChange)) {
            Settings.Global.putInt(getApplicationContext().getContentResolver(),
                    Settings.Global.PREFERRED_NETWORK_MODE + subID, networkToChange);
            d("toggle: Successfully changed to " + logPrefNetwork(networkToChange));
        }
    }

    /**
     * The method to toggle 4G Calling or VoLTE preference
     *
     * @param tm            {@link TelephonyManager} specific to subID
     * @param imsManager    {@link ImsManager} specific to subID
     * @param carrierConfig {@link PersistableBundle} info about the carrier.
     *                      Doesn't have proper use, but is required for
     *                      null check.
     * @param subID         the subscription ID from {@link #subscriptionsChangedListener}
     * @param isBoot        whether is boot task
     */
    private void handle4GVoLteToggle(TelephonyManager tm, final ImsManager imsManager, PersistableBundle carrierConfig, int subID, boolean isBoot) {
        if (imsManager == null) {
            d("4gLteToggle: ims manager is null :/");
            return;
        }
        if (carrierConfig == null) {
            d("4gLteToggle: carrier config is null :/");
            return;
        }
        if (!isEnhanced4GPrefEnabled(tm, imsManager, carrierConfig, subID)) {
            d("4gLteToggle: Enhanced 4G pref is NOT available");
            return;
        }

        if (isBoot) {
            d("4gLteToggle: Boot task");

            if (getPreferredEnhanced4GPref(imsManager)) {
                d("4gLteToggle: Enhanced 4G was ALREADY enabled, toggling Off and On ...");
                // OFF ...
                imsManager.setEnhanced4gLteModeSetting(false);
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Turn ON after 2 sec ...
                        imsManager.setEnhanced4gLteModeSetting(true);
                    }
                }, 2000);
            } else {
                d("4gLteToggle: Enhanced 4G was expectedly OFF.");
                if (Preference.getEnhanced4GEnabled(getApplicationContext(), getPreferredEnhanced4GPref(imsManager))) {
                    d("4gLteToggle: Enhanced 4G was enabled. Enabling ...");
                    imsManager.setEnhanced4gLteModeSetting(true);
                } else {
                    d("4gLteToggle: Enhanced 4G was disabled. Not enabling.");
                }
            }
        } else {
            d("4gLteToggle: Shutdown/reboot task");

            boolean isEnabled = getPreferredEnhanced4GPref(imsManager);
            if (isEnabled) {
                d("4gLteToggle: Enhanced 4G is enabled. Disabling ...");
                imsManager.setEnhanced4gLteModeSetting(false);
            } else {
                d("4gLteToggle: Enhanced 4G was disabled. Not toggling ...");
            }
            Preference.putEnhanced4GEnabled(isEnabled, getApplicationContext());
        }
    }

    /**
     * Get the current in-use network mode preference
     *
     * @return default 3G {@link RILConstants#NETWORK_MODE_WCDMA_PREF} if no pref stored
     */
    private int getPreferredNetwork(int subID) {
        return Settings.Global.getInt(getApplicationContext().getContentResolver(),
                Settings.Global.PREFERRED_NETWORK_MODE + subID, RILConstants.NETWORK_MODE_WCDMA_PREF);
    }

    /**
     * @return the current preference if 4G Calling or VoLTE is enabled
     */
    private boolean getPreferredEnhanced4GPref(ImsManager imsManager) {
        return imsManager.isEnhanced4gLteModeSettingEnabledByUser()
                && imsManager.isNonTtyOrTtyOnVolteEnabled();
    }

    /**
     * @return if 4G Calling or VoLTE preference is accessible
     */
    private boolean isEnhanced4GPrefEnabled(TelephonyManager tm, @Nullable ImsManager imsManager, PersistableBundle carrierConfig, int subID) {
        return subID != SubscriptionManager.INVALID_SUBSCRIPTION_ID
                && tm.getCallState(subID) == TelephonyManager.CALL_STATE_IDLE
                && imsManager != null
                && imsManager.isNonTtyOrTtyOnVolteEnabled()
                && carrierConfig.getBoolean(CarrierConfigManager.KEY_EDITABLE_ENHANCED_4G_LTE_BOOL);
    }

    /**
     * Returns whether
     *
     * @param network is LTE or not
     */
    private boolean isLTE(int network) {
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
                || network == RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA;
    }

    /**
     * This method returns the toggled network between 3G and LTE
     */
    private int getToggledNetwork(int currentNetwork) {
        int network = -99;

        switch (currentNetwork) {
            // GSM Devices
            // When currentNetwork is 3G
            case RILConstants.NETWORK_MODE_WCDMA_PREF:
            case RILConstants.NETWORK_MODE_WCDMA_ONLY:
            case RILConstants.NETWORK_MODE_GSM_UMTS:
            case RILConstants.NETWORK_MODE_GSM_ONLY:
                // return LTE
                network = RILConstants.NETWORK_MODE_LTE_GSM_WCDMA;
                break;
            // When currentNetwork is LTE
            case RILConstants.NETWORK_MODE_LTE_GSM_WCDMA:
            case RILConstants.NETWORK_MODE_LTE_ONLY:
            case RILConstants.NETWORK_MODE_LTE_WCDMA:
                // return 3G
                network = RILConstants.NETWORK_MODE_WCDMA_PREF;
                break;
            // GSM and CDMA devices
            case RILConstants.NETWORK_MODE_GLOBAL:
                // Wtf to do here?
                network = RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA;
                break;
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                network = RILConstants.NETWORK_MODE_WCDMA_PREF;
                break;
        }

        return network;
    }

    /**
     * Get the string version of the variables.
     * <p>
     * Too lazy to refer the {@link RILConstants}
     */
    private String logPrefNetwork(int network) {
        switch (network) {
            // GSM Devices
            case RILConstants.NETWORK_MODE_WCDMA_PREF:
                return "NETWORK_MODE_WCDMA_PREF";
            case RILConstants.NETWORK_MODE_WCDMA_ONLY:
                return "NETWORK_MODE_WCDMA_ONLY";
            case RILConstants.NETWORK_MODE_GSM_UMTS:
                return "NETWORK_MODE_GSM_UMTS";
            case RILConstants.NETWORK_MODE_GSM_ONLY:
                return "NETWORK_MODE_GSM_ONLY";
            case RILConstants.NETWORK_MODE_LTE_GSM_WCDMA:
                return "NETWORK_MODE_LTE_GSM_WCDMA";
            case RILConstants.NETWORK_MODE_LTE_ONLY:
                return "NETWORK_MODE_LTE_ONLY";
            case RILConstants.NETWORK_MODE_LTE_WCDMA:
                return "NETWORK_MODE_LTE_WCDMA";
            // GSM and CDMA devices
            case RILConstants.NETWORK_MODE_GLOBAL:
                return "NETWORK_MODE_GLOBAL";
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                return "NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA --> NETWORK_MODE_WCDMA_PREF";
            default:
                return "N/A";
        }
    }

    /**
     * This method is to check if work of modem and CS was completed by
     * reading the cache file.
     */
    private boolean wasModemCSWorkCompleted() {
        return new File(MODEM_SWITCHER_STATUS).exists();
    }

    /**
     * @return if modem flashed is one of the {@link #DEFAULT_MODEMS}
     */
    private boolean isModemDefault() {
        try {
            File file = new File(MODEM_SWITCHER_STATUS);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine().replace("\n", "").replace("\r", "").trim();
                br.close();
                d("Modem cache: " + line);

                for (String m : DEFAULT_MODEMS) {
                    if (line.split(",")[1].equals(m)) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @return true if enabled.
     */
    private boolean isAirplaneModeOn() {
        return Settings.System.getInt(getApplicationContext().getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    /**
     * Write logs
     * <p>
     * Dir: /data/data/com.yoshino.networkswitcher/files/ns.log
     */
    private void d(String msg) {
        Log.d(TAG, msg);

        File logFile = new File(getFilesDir(), "ns.log");
        try {
            String log = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault())
                    .format(System.currentTimeMillis()) + ": " + msg + " \r\n";

            FileOutputStream fos = getApplicationContext().openFileOutput("ns.log",
                    logFile.exists() ? Context.MODE_APPEND : Context.MODE_PRIVATE);

            fos.write(log.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
