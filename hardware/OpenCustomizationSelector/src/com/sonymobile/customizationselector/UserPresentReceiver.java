package com.sonymobile.customizationselector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserPresentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            context.startActivity(new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_HOME)
                    // This int is result of OR operation with different intent flags
                    // .addFlags(270565376));
                    // Solved by coin3x
                    .addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}
