package com.sonymobile.customizationselector.NS;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import com.sonymobile.customizationselector.CSLog;

public class AirplaneModeObserver extends ContentObserver {

    private static final String TAG = "AirplaneModeObserver";

    interface Listener {
        void onChange(Uri uri);
    }

    private final Context context;
    private boolean registered = false;
    private Listener listener = null;

    public AirplaneModeObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    public void register(Listener listener) {
        if (!registered) {
            this.listener = listener;
            context.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.Global.AIRPLANE_MODE_ON),
                    false, this, UserHandle.USER_CURRENT);
            registered = true;
            CSLog.d(TAG, "Registered");
        }
    }

    public void unregister() {
        if (registered) {
            context.getContentResolver().unregisterContentObserver(this);
            registered = false;
            CSLog.d(TAG, "Unregistered");
        }
    }

    @Override
    public void onChange(boolean b, Uri uri) {
        if (!b) {
            listener.onChange(uri);
        }
    }
}
