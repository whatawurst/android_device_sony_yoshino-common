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
    private static final int GSM = (int) (TelephonyManager.NETWORK_TYPE_BITMASK_GSM | TelephonyManager.NETWORK_TYPE_BITMASK_GPRS |
            TelephonyManager.NETWORK_TYPE_BITMASK_EDGE);

    private static final int CDMA = (int) (TelephonyManager.NETWORK_TYPE_BITMASK_CDMA | TelephonyManager.NETWORK_TYPE_BITMASK_1xRTT);

    // 3G
    private static final int EVDO = (int) (TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_0 | TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_A |
            TelephonyManager.NETWORK_TYPE_BITMASK_EVDO_B | TelephonyManager.NETWORK_TYPE_BITMASK_EHRPD);

    private static final int WCDMA = (int) (TelephonyManager.NETWORK_TYPE_BITMASK_HSUPA |
            TelephonyManager.NETWORK_TYPE_BITMASK_HSDPA | TelephonyManager.NETWORK_TYPE_BITMASK_HSPA |
            TelephonyManager.NETWORK_TYPE_BITMASK_HSPAP | TelephonyManager.NETWORK_TYPE_BITMASK_UMTS);

    // 4G
    private static final int LTE = (int) (TelephonyManager.NETWORK_TYPE_BITMASK_LTE | TelephonyManager.NETWORK_TYPE_BITMASK_LTE_CA);
    private static final int INVALID_NETWORK = -1;

    private AirplaneModeObserver airplaneModeObserver;
    private SimServiceObserver simServiceObserver;
    // Set until the phone is unlocked
    private BroadcastReceiver unlockObserver;

    @Override
    public void onCreate() {
        d("onCreate");
        airplaneModeObserver = new AirplaneModeObserver(getApplicationContext(), new Handler(getMainLooper()));
        simServiceObserver = new SimServiceObserver(getApplicationContext());
        unlockObserver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(unlockObserver);
                unlockObserver = null;
            }
        };
        registerReceiver(unlockObserver, new IntentFilter(Intent.ACTION_USER_UNLOCKED));

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
        int[] subs;
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

        if (isLTE(getPreferredNetwork(tm))) {
            toggle(tm);

            if (StorageManager.isFileEncryptedNativeOrEmulated() && unlockObserver != null) {
                // Delay resetting the network until phone is unlocked.
                // The current unlock observer is no longer required
                unregisterReceiver(unlockObserver);
                unlockObserver = null;
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
                            toggle(tm);
                            stopSelf();
                        });
                    }
                }
            });
        } else {
            if (tm.getSignalStrength() != null && tm.getSignalStrength().getLevel() != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                toggle(tm);
                stopSelf();
            } else {
                new SimServiceObserver(getApplicationContext()).register(subID, () -> {
                    toggle(tm);
                    stopSelf();
                });
            }
        }
    }

    /**
     * The method to toggle the network
     *
     * @param tm {@link TelephonyManager} specific to subID
     */
    private void toggle(TelephonyManager tm) {
        int networkToChange = getToggledNetwork(getPreferredNetwork(tm));
        d("toggle: To be changed to = " + logPrefNetwork(networkToChange));

        if (networkToChange == INVALID_NETWORK) {
            d("toggle: Couldn't get proper network to change");
            return;
        }

        tm.setAllowedNetworkTypesForReason(TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER, getRafFromNetworkType(networkToChange));
        d("toggle: Successfully changed to " + logPrefNetwork(networkToChange));
    }

    /**
     * Get the current in-use network mode preference
     * <p>
     * There are no defaults other than {@link #INVALID_NETWORK}
     */
    private int getPreferredNetwork(TelephonyManager tm) {
        return getNetworkTypeFromRaf((int) tm.getAllowedNetworkTypesForReason(TelephonyManager.ALLOWED_NETWORK_TYPES_REASON_USER));
    }

    /**
     * Returns whether
     *
     * @param network is LTE or not
     */
    private boolean isLTE(int network) {
        return network == TelephonyManager.NETWORK_MODE_GLOBAL
                || network == TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO
                || network == TelephonyManager.NETWORK_MODE_LTE_GSM_WCDMA
                || network == TelephonyManager.NETWORK_MODE_LTE_ONLY
                || network == TelephonyManager.NETWORK_MODE_LTE_WCDMA
                || network == TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA
                || network == TelephonyManager.NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA
                || network == TelephonyManager.NETWORK_MODE_LTE_TDSCDMA_WCDMA
                || network == TelephonyManager.NETWORK_MODE_LTE_TDSCDMA_GSM
                || network == TelephonyManager.NETWORK_MODE_LTE_TDSCDMA
                || network == TelephonyManager.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA;
    }

    /**
     * This method returns the toggled network between 3G and LTE
     */
    private int getToggledNetwork(int currentNetwork) {
        switch (currentNetwork) {
            // 3G to LTE
            case TelephonyManager.NETWORK_MODE_WCDMA_PREF:
            case TelephonyManager.NETWORK_MODE_WCDMA_ONLY:
            case TelephonyManager.NETWORK_MODE_GSM_UMTS:
            case TelephonyManager.NETWORK_MODE_GSM_ONLY:
                return TelephonyManager.NETWORK_MODE_LTE_GSM_WCDMA;

            // LTE to 3G
            case TelephonyManager.NETWORK_MODE_LTE_GSM_WCDMA:
            case TelephonyManager.NETWORK_MODE_LTE_ONLY:
            case TelephonyManager.NETWORK_MODE_LTE_WCDMA:
            case TelephonyManager.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA:
            case TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                return TelephonyManager.NETWORK_MODE_WCDMA_PREF;

            // Global to GSM ?, vice-versa
            case TelephonyManager.NETWORK_MODE_GLOBAL:
                return TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA;
            default:
                return INVALID_NETWORK;
        }
    }

    /**
     * @param network is the returned value from {@link #getToggledNetwork(int)}
     * @return network bitmask as per the toggled network
     */
    private long getRafFromNetworkType(int network) {
        switch (network) {
            case TelephonyManager.NETWORK_MODE_LTE_GSM_WCDMA:
                return LTE | GSM | WCDMA;
            case TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
                return LTE | CDMA | EVDO | GSM | WCDMA;
            default:
                return GSM | WCDMA;    // 3G default
        }
    }

    /**
     * Get network type from the bitmask
     * so that {@link #getToggledNetwork(int)}
     * can return correct value
     */
    private int getNetworkTypeFromRaf(int raf) {
        switch (getAdjustedRaf(raf)) {
            case (GSM | WCDMA):
                return TelephonyManager.NETWORK_MODE_WCDMA_PREF;
            case GSM:
                return TelephonyManager.NETWORK_MODE_GSM_ONLY;
            case WCDMA:
                return TelephonyManager.NETWORK_MODE_WCDMA_ONLY;
            case (CDMA | EVDO):
                return TelephonyManager.NETWORK_MODE_CDMA_EVDO;
            case (LTE | CDMA | EVDO):
                return TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO;
            case (LTE | GSM | WCDMA):
                return TelephonyManager.NETWORK_MODE_LTE_GSM_WCDMA;
            case (LTE | CDMA | EVDO | GSM | WCDMA):
                return TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA;
            case LTE:
                return TelephonyManager.NETWORK_MODE_LTE_ONLY;
            case (LTE | WCDMA):
                return TelephonyManager.NETWORK_MODE_LTE_WCDMA;
            case CDMA:
                return TelephonyManager.NETWORK_MODE_CDMA_NO_EVDO;
            case EVDO:
                return TelephonyManager.NETWORK_MODE_EVDO_NO_CDMA;
            case (GSM | WCDMA | CDMA | EVDO):
                return TelephonyManager.NETWORK_MODE_GLOBAL;
            default:
                return INVALID_NETWORK;
        }
    }

    /**
     * If the raf includes ANY bit set for a group
     * adjust it to contain ALL the bits for that group
     */
    private int getAdjustedRaf(int raf) {
        raf = ((GSM & raf) > 0) ? (GSM | raf) : raf;
        raf = ((WCDMA & raf) > 0) ? (WCDMA | raf) : raf;
        raf = ((CDMA & raf) > 0) ? (CDMA | raf) : raf;
        raf = ((EVDO & raf) > 0) ? (EVDO | raf) : raf;
        raf = ((LTE & raf) > 0) ? (LTE | raf) : raf;
        raf = ((TelephonyManager.NETWORK_TYPE_BITMASK_NR & raf) > 0) ? ((int) TelephonyManager.NETWORK_TYPE_BITMASK_NR | raf) : raf;
        return raf;
    }

    /**
     * Get the string version of the variables.
     * <p>
     * Too lazy to refer the {@link TelephonyManager}
     */
    private String logPrefNetwork(int network) {
        switch (network) {
            case TelephonyManager.NETWORK_MODE_WCDMA_PREF:
                return "NETWORK_MODE_WCDMA_PREF";
            case TelephonyManager.NETWORK_MODE_WCDMA_ONLY:
                return "NETWORK_MODE_WCDMA_ONLY";
            case TelephonyManager.NETWORK_MODE_GSM_UMTS:
                return "NETWORK_MODE_GSM_UMTS";
            case TelephonyManager.NETWORK_MODE_GSM_ONLY:
                return "NETWORK_MODE_GSM_ONLY";
            case TelephonyManager.NETWORK_MODE_LTE_GSM_WCDMA:
                return "NETWORK_MODE_LTE_GSM_WCDMA";
            case TelephonyManager.NETWORK_MODE_LTE_ONLY:
                return "NETWORK_MODE_LTE_ONLY";
            case TelephonyManager.NETWORK_MODE_LTE_WCDMA:
                return "NETWORK_MODE_LTE_WCDMA";
            case TelephonyManager.NETWORK_MODE_GLOBAL:
                return "NETWORK_MODE_GLOBAL";
            case TelephonyManager.NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA:
                return "NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA";
            case TelephonyManager.NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA:
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
