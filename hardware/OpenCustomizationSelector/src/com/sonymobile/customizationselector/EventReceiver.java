package com.sonymobile.customizationselector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionManager;

public class EventReceiver extends BroadcastReceiver {

    private static final String TAG = EventReceiver.class.getSimpleName();

    private int getSubId(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("subscription", SubscriptionManager.INVALID_SUBSCRIPTION_ID);
        CSLog.d(TAG, "Event received for subscription: " + intExtra);
        return (intExtra == -1 || !CommonUtil.isMandatorySimParamsAvailable(context, intExtra)) ? -1 : intExtra;
    }

    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            CSLog.d(TAG, "Context or Intent was null.");
            return;
        }

        String action = intent.getAction();
        if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
            CSLog.d(TAG, "Carrier config changed received");

            if (CommonUtil.isDefaultDataSlot(context, getSubId(context, intent))) {
                CSLog.d(TAG, "Default data SIM loaded");
                Intent service = new Intent(context, CustomizationSelectorService.class);
                service.setAction("evaluate_action");
                context.startService(service);
            }
        } else if ("android.intent.action.BOOT_COMPLETED".equals(action) && CommonUtil.isDualSim(context)) {
            DSDataSubContentJob.scheduleJob(context);
        }
    }
}
