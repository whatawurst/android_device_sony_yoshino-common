package com.yoshino.parts;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.BufferedReader;
import com.yoshino.parts.Constants;
import com.yoshino.parts.BootReceiver;

public class QSUltraDim extends TileService {

    private static final String TAG = "QSUltraDim";
    private static int defaultValue = 0;
    private static int lowValue = 5000;
    private boolean mIsSupported = false;

    private static int readFSCurr() {
        try {
            File file = new File(Constants.ULTRA_DIM_FILE);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String data = br.readLine();
                br.close();
                Log.d(TAG, "Read FSCurr: " + data);
                return Integer.parseInt(data);
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void setValue(Context context, boolean enabled) {
        int value;
        if (enabled)
            value = lowValue;
        else
            value = (defaultValue > 0) ? defaultValue : 17500;
        Log.d(TAG, "writeUltraDim: Setting to " + value);
        Settings.System.putInt(context.getContentResolver(), Constants.ULTRA_DIM, enabled ? 1 : 0);
        try {
            PrintWriter writer = new PrintWriter(new File(Constants.ULTRA_DIM_FILE));
            writer.println(value);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // Read defaults possibly set by device-specific config
        readConfigProperties();
        // If unset read from sysfs and set property in case the service crashes and is restarted
        if(defaultValue == -1) {
            defaultValue = readFSCurr();
            SystemProperties.set(Constants.ULTRA_DIM_DEFAULT_PROP, String.valueOf(defaultValue));
        }
        if (isEnabled(context) && defaultValue > 0)
            setValue(context, true);
    }

    private void updateTile(boolean enabled) {
        Tile tile = getQsTile();
        if(!mIsSupported)
            tile.setState(Tile.STATE_UNAVAILABLE);
        else
            tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(getString(enabled ? R.string.ultra_dim_label_enabled : R.string.ultra_dim_label_disabled));
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