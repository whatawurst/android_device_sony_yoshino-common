package com.sonymobile.customizationselector.NS;

import android.content.Context;
import android.os.Handler;
import android.telephony.CellSignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.sonymobile.customizationselector.CSLog;

public class SimServiceObserver {

    private static final String TAG = "SimServiceObserver";

    interface Listener {
        void onConnected();
    }

    private final Context context;

    private boolean registered = false;
    private int subID = SubscriptionManager.INVALID_SUBSCRIPTION_ID;

    private Handler handler;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                synchronized (new Object()) {
                    if (subID != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                        TelephonyManager tm = context.getSystemService(TelephonyManager.class).createForSubscriptionId(subID);
                        if (tm.getSignalStrength() != null && tm.getSignalStrength().getLevel() != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                            listener.onConnected();
                            unregister();
                        }
                    } else {
                        unregister();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                handler.postDelayed(this, 2000);
            }
        }
    };

    private Listener listener;

    public SimServiceObserver(Context context) {
        this.context = context;
    }

    public void register(int subID, Listener listener) {
        if (!registered) {
            this.subID = subID;
            this.listener = listener;
            handler = new Handler(context.getMainLooper());

            handler.post(runnable);
            registered = true;
            CSLog.d(TAG, "Registered");
        }
    }

    public void unregister() {
        if (registered) {
            listener = null;
            subID = SubscriptionManager.INVALID_SUBSCRIPTION_ID;

            handler.removeCallbacks(runnable);

            registered = false;
            CSLog.d(TAG, "Unregistered");
        }
    }
}
