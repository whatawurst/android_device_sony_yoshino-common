package com.sonymobile.customizationselector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventReceiver extends BroadcastReceiver {
    private static final String TAG = EventReceiver.class.getSimpleName();

    private int getSubId(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("subscription", -1);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Event received for subscription: ");
        stringBuilder.append(intExtra);
        CSLog.d(str, stringBuilder.toString());
        return (intExtra == -1 || !CommonUtil.isMandatorySimParmsAvailable(context, intExtra)) ? -1 : intExtra;
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
                Intent intent2 = new Intent(context, CustomizationSelectorService.class);
                intent2.setAction("evaluate_action");
                context.startService(intent2);
            }
        } else if ("android.intent.action.BOOT_COMPLETED".equals(action) && CommonUtil.isDualSim(context)) {
            DSDataSubContentJob.scheduleJob(context);
        }
    }
}
