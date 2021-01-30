package com.sonymobile.customizationselector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionManager;

public class PreferenceReceiver extends BroadcastReceiver {

    private static final String TAG = "PreferenceReceiver";
    private static final int ENABLED = 1;
    private static final int DISABLED = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || context == null) {
            CSLog.e(TAG, "Context or intent null !");
            return;
        }

        int change = intent.getIntExtra("cs_ims", DISABLED);
        CSLog.d(TAG, "change received: " + change);
        int subID = context.createDeviceProtectedStorageContext().getSharedPreferences(Configurator.PREF_PKG, Context.MODE_PRIVATE)
                .getInt("event_subID", SubscriptionManager.INVALID_SUBSCRIPTION_ID);
        if (subID == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            CSLog.e(TAG, "Invalid sub ID, returning");
            return;
        }

        ImsSwitcher switcher = new ImsSwitcher(context);
        if (change == ENABLED) {
            switcher.switchOnIMS(subID);
        }
        if (change == DISABLED) {
            switcher.switchOffIMS();
        }
    }
}
