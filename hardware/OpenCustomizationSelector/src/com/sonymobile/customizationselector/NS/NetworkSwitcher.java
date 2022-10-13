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
import android.telephony.RadioAccessFamily;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.RILConstants;
import com.sonymobile.customizationselector.CSLog;
import com.sonymobile.customizationselector.CommonUtil;

/**
 * Service to handle Network mode
 * - On boot if preference network is set to LTE switch to the selected lower network (e.g. 3G)
 * - When the device is unlocked and the SIM has got service connection
 *   switch back to the previous network and exit
 *
 * @author shank03
 */
public class NetworkSwitcher extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final String TAG = "NetworkSwitcher";
    private static final String NS_LOWER_NETWORK = "ns_lowNet";
    private static final String NS_PREFERRED = "ns_preferred";

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
            new Handler(getMainLooper()).postDelayed(() -> switchDown(subID), 1400);
        } else {
            new SlotObserver(getApplicationContext()).register(subID,
                    () -> new Handler(getMainLooper()).postDelayed(() -> switchDown(subID), 1400));
        }
    }

    private void switchDown(int subID) {
        if (subID < 0) {
            d("switchDown: Error, invalid subID");
            stopSelf();
            return;
        }

        TelephonyManager tm = getSystemService(TelephonyManager.class).createForSubscriptionId(subID);

        int currentNetwork = getPreferredNetwork(subID);
        if (isLTE(currentNetwork)) {
            setOriginalNetwork(subID, currentNetwork);
            changeNetwork(tm, subID, getLowerNetwork());

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
                            changeNetwork(tm, subID, getOriginalNetwork(subID));
                            stopSelf();
                        });
                    }
                }
            });
        } else {
            if (tm.getSignalStrength() != null && tm.getSignalStrength().getLevel() != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                changeNetwork(tm, subID, getOriginalNetwork(subID));
                stopSelf();
            } else {
                new SimServiceObserver(getApplicationContext()).register(subID, () -> {
                    changeNetwork(tm, subID, getOriginalNetwork(subID));
                    stopSelf();
                });
            }
        }
    }

    /**
     * The method to change the network
     *
     * @param tm             {@link TelephonyManager} specific to subID
     * @param subID          the subscription ID from [subscriptionsChangedListener]
     * @param newNetwork     network to change to
     */
    private void changeNetwork(TelephonyManager tm, int subID, int newNetwork) {
        d("changeNetwork: To be changed to = " + networkToString(newNetwork));

        if (tm.setPreferredNetworkTypeBitmask(RadioAccessFamily.getRafFromNetworkType(newNetwork))) {
            Settings.Global.putInt(getApplicationContext().getContentResolver(), Settings.Global.PREFERRED_NETWORK_MODE + subID, newNetwork);
            d("changeNetwork: Successfully changed to " + networkToString(newNetwork));
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
     * Get the original network mode preference
     *
     * @return Stored value, defaults to {@link RILConstants#NETWORK_MODE_LTE_GSM_WCDMA}
     */
    private int getOriginalNetwork(int subID) {
        return Settings.System.getInt(getApplicationContext().getContentResolver(), NS_PREFERRED + subID,
                                      RILConstants.NETWORK_MODE_LTE_GSM_WCDMA);
    }

    private void setOriginalNetwork(int subID, int network) {
        Settings.System.putInt(getApplicationContext().getContentResolver(), NS_PREFERRED + subID, network);
    }

    /**
     * Returns whether @param network is LTE or not
     */
    private boolean isLTE(int network) {
        int lteMask = RadioAccessFamily.RAF_LTE | RadioAccessFamily.RAF_LTE_CA;
        return (RadioAccessFamily.getRafFromNetworkType(network) & lteMask) != 0;
    }

    /**
     * This method returns the lower network to switch to
     */
    private int getLowerNetwork() {
        return Settings.System.getInt(getApplicationContext().getContentResolver(), NS_LOWER_NETWORK, RILConstants.NETWORK_MODE_WCDMA_PREF);
    }

    /**
     * Get the string version of the variables.
     * <p>
     * Too lazy to refer the {@link RILConstants}
     */
    private String networkToString(int network) {
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
                return "NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA";
            default:
                return "N/A(" + network + ")";
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
