package com.sonymobile.customizationselector.NS;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import com.sonymobile.customizationselector.CSLog;
import com.sonymobile.customizationselector.CommonUtil;

public class SubIdObserver {

    private static final String TAG = "SubIdObserver";

    interface Listener {
        void onConnected(int subID);
    }

    private final Context context;

    private boolean registered = false;

    private Handler handler;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                synchronized (new Object()) {
                    int sub = getSubID();
                    if (sub != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                        listener.onConnected(sub);
                        unregister();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (handler != null) {
                    handler.postDelayed(this, 2000);
                }
            }
        }
    };

    private Listener listener;

    public void register(Listener listener) {
        if (!registered) {
            this.listener = listener;
            handler = new Handler(context.getMainLooper());

            handler.post(runnable);
            registered = true;

            CSLog.d(TAG, "Registered");
        }
    }

    private int getSubID() {
        int[] subs = null;
        if (CommonUtil.isDualSim(context)) {
            subs = SubscriptionManager.getSubId(Settings.System.getInt(context.getContentResolver(), "ns_slot", 0));
        } else {
            subs = SubscriptionManager.getSubId(0);
        }
        return subs == null ? SubscriptionManager.INVALID_SUBSCRIPTION_ID : subs[0];
    }

    private void unregister() {
        listener = null;
        handler.removeCallbacks(runnable);
        handler = null;

        registered = false;
        CSLog.d(TAG, "Unregistered");
    }

    public SubIdObserver(Context context) {
        this.context = context;
    }
}
