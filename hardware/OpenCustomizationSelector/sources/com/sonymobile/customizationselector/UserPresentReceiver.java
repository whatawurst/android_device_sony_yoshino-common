package com.sonymobile.customizationselector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserPresentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null && "android.intent.action.SCREEN_ON".equals(intent.getAction())) {
            context.startActivity(new Intent("android.intent.action.MAIN", null).addCategory("android.intent.category.HOME").addFlags(270565376));
        }
    }
}
