package com.sonymobile.customizationselector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MasterClearReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.intent.action.MASTER_CLEAR_NOTIFICATION";

    public void onReceive(Context context, Intent intent) {
        if (intent != null && ACTION.equals(intent.getAction())) {
            Configurator.clearMiscTaConfigId();
        }
    }
}
