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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.CellSignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.internal.telephony.RILConstants;

import java.io.File;
import java.util.List;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    SubscriptionManager sm;

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
     * Broadcast receiver to perform network toggle on shutdown/reboot
     */
    private BroadcastReceiver shutDownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Intent.ACTION_SHUTDOWN) || action.equals(Intent.ACTION_REBOOT)) {
                    Log.d(TAG, "onReceive: Action received: " + action);
                    if (mSubID == -99) {
                        Log.d(TAG, "onReceive: Could not perform network switch; mSubID = " + mSubID);
                        return;
                    }
                    performIntendedTask(mSubID, false);
                }
            }
        }
    };

    private SubscriptionManager.OnSubscriptionsChangedListener subscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        @Override
        public void onSubscriptionsChanged() {
            Log.d(TAG, "onSubscriptionsChanged: Called");
            super.onSubscriptionsChanged();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, Manifest.permission.READ_PHONE_STATE + " was denied.");
                return;
            }

            if (!changedOnBoot) {
                if (isAirplaneModeOn()) {
                    Log.d(TAG, "onSubscriptionsChanged: Airplane mode was ON. Waiting ...");
                    return;
                }

                List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
                Log.d(TAG, "onSubscriptionsChanged: list size " + list.size());

                if (list.size() >= 1) {
                    // TODO: dual sim
                    mSubID = list.get(0).getSubscriptionId();

                    // Delay 1 sec, not to immediately react
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            performIntendedTask(mSubID, true);
                        }
                    }, 1000);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "Service started");
        sm = getSystemService(SubscriptionManager.class);
        sm.addOnSubscriptionsChangedListener(subscriptionsChangedListener);

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
    private void performIntendedTask(int subID, boolean isBoot) {
        if (!wasModemCSWorkCompleted()) {
            Log.d(TAG, "performIntendedTask: Modem Work was not completed. Skipping Toggle task");
            wasModemDone = false;
            return;
        }

        TelephonyManager tm = getSystemService(TelephonyManager.class).createForSubscriptionId(subID);

        int currentNetwork = getPreferredNetwork(subID);
        Log.d(TAG, "performIntendedTask: Current network = " + logPrefNetwork(currentNetwork));

        // Continue the toggle task
        if (isBoot) {
            Log.d(TAG, "performIntendedTask: Boot task");

            if (tm.getSignalStrength() == null ||
                    tm.getSignalStrength().getLevel() == CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                Log.d(TAG, "performIntendedTask: SIM not in service. Waiting ...");
                return;
            }

            if (Preference.getWasNetwork3G(getApplicationContext(), !isLTE(currentNetwork))) {
                Log.d(TAG, "performIntendedTask: User pref was 3G Not toggling");
            } else {
                Log.d(TAG, "performIntendedTask: User pref was LTE; Toggling ...");
                toggle(tm, subID, currentNetwork);
            }
            changedOnBoot = true;
        } else {
            Log.d(TAG, "performIntendedTask: Shutdown/reboot task");

            if (wasModemDone) {
                boolean lte = isLTE(currentNetwork);
                if (lte) {
                    Log.d(TAG, "performIntendedTask: Current network is LTE; Toggling ...");
                    toggle(tm, subID, currentNetwork);
                } else {
                    Log.d(TAG, "performIntendedTask: Current network was NOT LTE");
                }
                Preference.putWasNetwork3G(!lte, getApplicationContext());
            } else {
                Log.d(TAG, "performIntendedTask: Modem task was incomplete when checked on boot, skipping ...");
            }
        }
    }

    /**
     * The method to toggle the network
     *
     * @param tm             {@link TelephonyManager} specific to subID
     * @param subID          the subscription ID from {@link #subscriptionsChangedListener}
     * @param currentNetwork current preferred network mode from {@link #performIntendedTask(int, boolean)}
     */
    private void toggle(TelephonyManager tm, int subID, int currentNetwork) {
        int networkToChange = getToggledNetwork(currentNetwork);
        Log.d(TAG, "toggle: To be changed to = " + logPrefNetwork(networkToChange));

        if (networkToChange == -99) {
            Log.d(TAG, "toggle: Couldn't get proper network to change");
            return;
        }

        if (tm.setPreferredNetworkType(subID, networkToChange)) {
            Settings.Global.putInt(getApplicationContext().getContentResolver(),
                    Settings.Global.PREFERRED_NETWORK_MODE + subID, networkToChange);
            Log.d(TAG, "toggle: Successfully changed to " + logPrefNetwork(networkToChange));
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
    public boolean wasModemCSWorkCompleted() {
        return new File("/cache/modem/modem_switcher_status").exists();
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
}
