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
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RILConstants;

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

    // Global variable to be accessed on shutdown
    int mSubID = -99;

    /**
     * The {@link SubscriptionManager#addOnSubscriptionsChangedListener(SubscriptionManager.OnSubscriptionsChangedListener)}
     * maybe called many times and every time network mode changes.
     * <p>
     * This boolean will make sure that the network toggle takes place only once.
     */
    boolean changedOnBoot = false;

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

    @Override
    public void onCreate() {
        Log.d(TAG, "Service started");
        final SubscriptionManager sm = getSystemService(SubscriptionManager.class);
        sm.addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener() {
            @Override
            public void onSubscriptionsChanged() {
                Log.d(TAG, "onSubscriptionsChanged: Called: ");
                super.onSubscriptionsChanged();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, Manifest.permission.READ_PHONE_STATE + " was denied.");
                    return;
                }

                if (!changedOnBoot) {
                    List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
                    Log.d(TAG, "onSubscriptionsChanged: list size " + list.size());

                    if (list.size() >= 1) {
                        // TODO: dual sim
                        int subID = list.get(0).getSubscriptionId();
                        mSubID = subID;     // Storing it to be accessible on shutdown
                        performIntendedTask(subID, true);

                        changedOnBoot = true;
                    }
                }
            }
        });

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

    private void performIntendedTask(int subID, boolean isBoot) {
        TelephonyManager tm = getSystemService(TelephonyManager.class).createForSubscriptionId(subID);

        boolean isLteOnCdma = tm.getLteOnCdmaMode() == PhoneConstants.LTE_ON_CDMA_TRUE;

        int currentNetwork = getPreferredNetwork(subID, isLteOnCdma);
        Log.d(TAG, "performIntendedTask: Current network = " + logPrefNetwork(currentNetwork, isLteOnCdma));

        // Store the preferred default 3G network (set when clean flashed/installed)
        if (!Preference.get3GTaken(getApplicationContext())) {
            if (isLTE(currentNetwork)) {
                int toggled = getToggledNetwork(currentNetwork, isLteOnCdma);
                Log.d(TAG, "performIntendedTask: (Toggled) Default 3G stored = " + logPrefNetwork(toggled, isLteOnCdma));
                Preference.putPreferred3G(toggled, getApplicationContext());
            } else {
                Log.d(TAG, "performIntendedTask: Default 3G stored = " + logPrefNetwork(currentNetwork, isLteOnCdma));
                Preference.putPreferred3G(currentNetwork, getApplicationContext());
            }
            Preference.put3GTaken(true, getApplicationContext());
        }

        // Continue the toggle task
        if (isBoot) {
            Log.d(TAG, "performIntendedTask: Boot task");

            if (Preference.getWasNetwork3G(getApplicationContext(), !isLTE(currentNetwork))) {
                Log.d(TAG, "performIntendedTask: User pref was 3G Not toggling");
                Preference.putPreferred3G(currentNetwork, getApplicationContext());
            } else {
                Log.d(TAG, "performIntendedTask: User pref was LTE; Toggling ...");
                toggle(tm, subID, currentNetwork, isLteOnCdma);
            }
        } else {
            Log.d(TAG, "performIntendedTask: Shutdown/reboot task");

            boolean lte = isLTE(currentNetwork);
            if (lte) {
                Log.d(TAG, "performIntendedTask: Current network is LTE; Toggling ...");
                toggle(tm, subID, currentNetwork, isLteOnCdma);
            } else {
                Log.d(TAG, "performIntendedTask: Current network was NOT LTE");
            }
            Preference.putWasNetwork3G(!lte, getApplicationContext());
        }
    }

    private void toggle(TelephonyManager tm, int subID, int currentNetwork, boolean isLteOnCdma) {
        int networkToChange = getToggledNetwork(currentNetwork, isLteOnCdma);
        Log.d(TAG, "toggle: To be changed to = " + logPrefNetwork(networkToChange, isLteOnCdma));

        if (networkToChange == -99) {
            Log.d(TAG, "toggle: Couldn't get proper network to change");
            return;
        }

        if (tm.setPreferredNetworkType(subID, networkToChange)) {
            Settings.Global.putInt(getApplicationContext().getContentResolver(),
                    Settings.Global.PREFERRED_NETWORK_MODE + subID, networkToChange);
            Log.d(TAG, "toggle: Successfully changed to " + logPrefNetwork(networkToChange, isLteOnCdma));
        }
    }

    /*
     * Get the current in-use network mode preference
     */
    private int getPreferredNetwork(int subID, boolean isCDMA) {
        int preferredNetworkMode = RILConstants.PREFERRED_NETWORK_MODE;
        if (isCDMA) {
            preferredNetworkMode = RILConstants.NETWORK_MODE_GLOBAL;
        }
        return Settings.Global.getInt(getApplicationContext().getContentResolver(),
                Settings.Global.PREFERRED_NETWORK_MODE + subID, preferredNetworkMode);
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
    private int getToggledNetwork(int currentNetwork, boolean isCDMA) {
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
                network = Preference.getPreferred3G(getApplicationContext(), RILConstants.NETWORK_MODE_WCDMA_PREF);
                break;
            // GSM and CDMA devices
            case RILConstants.NETWORK_MODE_GLOBAL:
                // Wtf to do here?
                network = RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA;
                break;
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                // Determine the correct network type
                if (isCDMA) {
                    network = RILConstants.NETWORK_MODE_CDMA;
                } else {
                    network = RILConstants.NETWORK_MODE_WCDMA_PREF;
                }
                break;
            // CDMA Devices
            case RILConstants.NETWORK_MODE_CDMA:
                network = RILConstants.NETWORK_MODE_LTE_CDMA_EVDO;
                break;
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO:
                network = RILConstants.NETWORK_MODE_CDMA;
                break;
        }

        return network;
    }

    /**
     * Get the string version of the variables.
     * <p>
     * Too lazy to refer the {@link RILConstants}
     */
    private String logPrefNetwork(int network, boolean isCDMADevice) {
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
                if (isCDMADevice) {
                    return "NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA --> NETWORK_MODE_CDMA";
                } else {
                    return "NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA --> NETWORK_MODE_WCDMA_PREF";
                }
                // CDMA Devices
            case RILConstants.NETWORK_MODE_CDMA:
                return "NETWORK_MODE_CDMA";
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO:
                return "NETWORK_MODE_LTE_CDMA_EVDO";
            default:
                return "N/A";
        }
    }
}
