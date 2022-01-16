package com.yoshino.parts;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import com.yoshino.parts.Constants;
import com.yoshino.parts.BootReceiver;

public class QSUltraDim extends TileService {

    private static final String TAG = "QSUltraDim";
    private static int defaultValue = 0;
    private static int lowValue = 5000;
    private boolean mIsSupported = false;

    private static void setValue(Context context, boolean enabled) {
        int value;
        if (enabled)
            value = lowValue;
        else
            value = (defaultValue > 0) ? defaultValue : 17500;
        Log.d(TAG, "writeUltraDim: Setting to " + value);
        Settings.System.putInt(context.getContentResolver(), Constants.ULTRA_DIM, enabled ? 1 : 0);
        SystemProperties.set(Constants.ULTRA_DIM_CURRENT_PROP, String.valueOf(value));
    }
    
    private boolean isEnabled() {
        return isEnabled(this);
    }

    private static boolean isEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Constants.ULTRA_DIM, 0) != 0;
    }

    private static void readConfigProperties() {
        if(defaultValue == 0) {
            defaultValue = SystemProperties.getInt(Constants.ULTRA_DIM_DEFAULT_PROP, -1);
            lowValue = SystemProperties.getInt(Constants.ULTRA_DIM_LOW_PROP, 5000);
        }
    }

    /// Init after Boot
    public static void init(Context context) {
        readConfigProperties();
        if (isEnabled(context) && defaultValue > 0)
            setValue(context, true);
    }

    private void updateTile(boolean enabled) {
        Tile tile = getQsTile();
        if(!mIsSupported) {
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.setSubtitle(getString(R.string.ultra_dim_label_unsupported));
        } else {
            tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setSubtitle(getString(enabled ? R.string.ultra_dim_label_enabled : R.string.ultra_dim_label_disabled));
        }
        tile.updateTile();
    }

    @Override
    public void onClick() {
        if(!mIsSupported)
            return;
        final boolean enabled = !isEnabled();
        Log.i(TAG, "Setting to " + enabled);
        setValue(this, enabled);
        updateTile(enabled);
    }

    @Override
    public void onStartListening() {
        readConfigProperties();
        mIsSupported = defaultValue > 0;
        updateTile(isEnabled());
    }
}