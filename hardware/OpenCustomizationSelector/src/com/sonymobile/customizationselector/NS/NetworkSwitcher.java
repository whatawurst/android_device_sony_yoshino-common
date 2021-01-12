package com.sonymobile.customizationselector.NS;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.CellSignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.RILConstants;
import com.sonymobile.customizationselector.CSLog;
import com.sonymobile.customizationselector.CommonUtil;

/**
 * Service to handle Network mode
 * <p>
 * - On shutdown/reboot, get the preference if network was 3G
 * - If no, then toggle to 3G and the system process continues else do nothing
 * - On boot, if preference was 3G, do nothing, else toggle to LTE
 *
 * @author shank03
 */
public class NetworkSwitcher extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final String TAG = "NetworkSwitcher";

    // Network bitmasks
    // 2G
    private static final long GSM = TelephonyManager.NETWORK_TYPE_BITMASK_GSM | TelephonyManager.NETWORK_TYPE_BITMASK_GPRS |
            TelephonyManager.NETWORK_TYPE_BITMASK_EDGE;

    private static final long CDMA = TelephonyManager.NETWORK_TYPE_BITMASK_CDMA | TelephonyManager.NETWORK_TYPE_BITMASK_1xRTT;

    // 3G
    private static final long EVDO = TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_0 | TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_A |
            TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_B | TelephonyManager.NETWORK_TYPE_BITMASK_EHRPD;

    private static final long WCDMA = TelephonyManager.NETWORK_TYPE_BITMASK_HSUPA |
            TelephonyManager.NETWORK_TYPE_BITMASK_HSDPA | TelephonyManager.NETWORK_TYPE_BITMASK_HSPA |
            TelephonyManager.NETWORK_TYPE_BITMASK_HSPAP | TelephonyManager.NETWORK_TYPE_BITMASK_UMTS;

    // 4G
    private static final long LTE = TelephonyManager.NETWORK_TYPE_BITMASK_LTE | TelephonyManager.NETWORK_TYPE_BITMASK_LTE_CA;

    private AirplaneModeObserver airplaneModeObserver;
    private SimServiceObserver simServiceObserver;

    @Override
    public void onCreate() {
        d("onCreate");
        airplaneModeObserver = new AirplaneModeObserver(getApplicationContext(), new Handler(getMainLooper()));
        simServiceObserver = new SimServiceObserver(getApplicationContext());

        // Start process
        try {
            int subID = getSubID();
            if (subID == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                new SubIdObserver(getApplicationContext()).register(this::initProcess);
            } else {
                initProcess(subID);
            }
        } catch (Exception e) {
            CSLog.e(TAG, "Error: ", e);
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        return START_STICKY;
    }

    /**
     * Get the subscription IDs based on phone count and sim status.
     */
    private int getSubID() {
        int[] subs = null;
        if (CommonUtil.isDualSim(getApplicationContext())) {
            d("initSubID: device is dual sim");
            subs = SubscriptionManager.getSubId(Settings.System.getInt(getApplicationContext().getContentResolver(), "ns_slot", 0));
        } else {
            d("initSubID: single sim device");
            subs = SubscriptionManager.getSubId(0);
        }
        return subs == null ? SubscriptionManager.INVALID_SUBSCRIPTION_ID : subs[0];
    }

    private void initProcess(int subID) {
        if (CommonUtil.isSIMLoaded(getApplicationContext(), subID)) {
            new Handler(getMainLooper()).postDelayed(() -> task(subID), 1400);
        } else {
            new SlotObserver(getApplicationContext()).register(subID,
                    () -> new Handler(getMainLooper()).postDelayed(() -> task(subID), 1400));
        }
    }

    private void task(int subID) {
        if (subID < 0) {
            d("task: Error, invalid subID");
            stopSelf();
            return;
        }

        TelephonyManager tm = getSystemService(TelephonyManager.class).createForSubscriptionId(subID);

        int currentNetwork = getPreferredNetwork(subID);
        if (isLTE(currentNetwork)) {
            toggle(tm, subID, currentNetwork);

            if (StorageManager.isFileEncryptedNativeOrEmulated()) {
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        unregisterReceiver(this);
                        handleConnection(tm, subID);
                    }
                }, new IntentFilter(Intent.ACTION_USER_UNLOCKED));
            } else {
                handleConnection(tm, subID);
            }
        } else {
            d("Network is not LTE, no work.");
            stopSelf();
        }
    }

    private void handleConnection(TelephonyManager tm, int subID) {
        if (isAirplaneModeOn()) {
            airplaneModeObserver.register(uri -> {
                if (uri != null && uri == Settings.System.getUriFor(Settings.Global.AIRPLANE_MODE_ON)) {
                    if (isAirplaneModeOn()) {
                        simServiceObserver.unregister();
                    } else {
                        simServiceObserver.register(subID, () -> {
                            airplaneModeObserver.unregister();
                            toggle(tm, subID, getPreferredNetwork(subID));
                            stopSelf();
                        });
                    }
                }
            });
        } else {
            if (tm.getSignalStrength() != null && tm.getSignalStrength().getLevel() != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                toggle(tm, subID, getPreferredNetwork(subID));
                stopSelf();
            } else {
                new SimServiceObserver(getApplicationContext()).register(subID, () -> {
                    toggle(tm, subID, getPreferredNetwork(subID));
                    stopSelf();
                });
            }
        }
    }

    /**
     * The method to toggle the network
     *
     * @param tm             {@link TelephonyManager} specific to subID
     * @param subID          the subscription ID from [subscriptionsChangedListener]
     * @param currentNetwork current preferred network mode from [task]
     */
    private void toggle(TelephonyManager tm, int subID, int currentNetwork) {
        int networkToChange = getToggledNetwork(currentNetwork);
        d("toggle: To be changed to = " + logPrefNetwork(networkToChange));

        if (networkToChange == -99) {
            d("toggle: Couldn't get proper network to change");
            return;
        }

        if (tm.setPreferredNetworkTypeBitmask(getNetworkBitmask(networkToChange))) {
            Settings.Global.putInt(getApplicationContext().getContentResolver(), Settings.Global.PREFERRED_NETWORK_MODE + subID, networkToChange);
            d("toggle: Successfully changed to " + logPrefNetwork(networkToChange));
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
        switch (currentNetwork) {
            // 3G to LTE
            case RILConstants.NETWORK_MODE_WCDMA_PREF:
            case RILConstants.NETWORK_MODE_WCDMA_ONLY:
            case RILConstants.NETWORK_MODE_GSM_UMTS:
            case RILConstants.NETWORK_MODE_GSM_ONLY:
                return RILConstants.NETWORK_MODE_LTE_GSM_WCDMA;

            // LTE to 3G
            case RILConstants.NETWORK_MODE_LTE_GSM_WCDMA:
            case RILConstants.NETWORK_MODE_LTE_ONLY:
            case RILConstants.NETWORK_MODE_LTE_WCDMA:
            case RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA:
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                return RILConstants.NETWORK_MODE_WCDMA_PREF;

            // Global to GSM ?, vice-versa
            case RILConstants.NETWORK_MODE_GLOBAL:
                return RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA;
            default:
                return -99;
        }
    }

    /**
     * @param network is the returned value from {@link #getToggledNetwork(int)}
     * @return network bitmask as per the toggled network
     */
    private long getNetworkBitmask(int network) {
        switch (network) {
            case RILConstants.NETWORK_MODE_LTE_GSM_WCDMA:
                return LTE | GSM | WCDMA;
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                return LTE | CDMA | EVDO | GSM | WCDMA;
            default:
                return GSM | WCDMA;    // 3G default
        }
    }

    /**
     * Get the string version of the variables.
     * <p>
     * Too lazy to refer the {@link RILConstants}
     */
    private String logPrefNetwork(int network) {
        switch (network) {
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
            case RILConstants.NETWORK_MODE_GLOBAL:
                return "NETWORK_MODE_GLOBAL";
            case RILConstants.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA:
                return "NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA";
            case RILConstants.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                return "NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA --> NETWORK_MODE_WCDMA_PREF";
            default:
                return "N/A";
        }
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @return true if enabled.
     */
    private boolean isAirplaneModeOn() {
        return Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    private void d(String msg) {
        CSLog.d(TAG, msg);
    }
}
