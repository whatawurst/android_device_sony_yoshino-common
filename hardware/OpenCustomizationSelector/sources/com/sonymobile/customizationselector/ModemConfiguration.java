package com.sonymobile.customizationselector;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import java.io.IOException;

public class ModemConfiguration {
    private static final String MODEM_APPENDIX = "_tar.mbn";
    public static final String SAVED_MODEM_CONFIG = "saved_config";
    private static final String TAG = ModemConfiguration.class.getSimpleName();
    private ModemSwitcher mModemSwitcher = new ModemSwitcher();
    private SharedPreferences mPreference;

    public ModemConfiguration(SharedPreferences sharedPreferences) {
        this.mPreference = sharedPreferences;
    }

    /* Access modifiers changed, original: 0000 */
    public String getModemConfiguration(String str) {
        String str2;
        int i = 0;
        String str3 = "";
        String[] availableModemConfigurations = this.mModemSwitcher.getAvailableModemConfigurations();
        if (str == null) {
            str = "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(MODEM_APPENDIX);
        String stringBuilder2 = stringBuilder.toString();
        if (availableModemConfigurations != null && availableModemConfigurations.length > 0) {
            str2 = availableModemConfigurations[0];
            str3 = TAG;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("getModemConfiguration - Finding modem with suffix: ");
            stringBuilder3.append(stringBuilder2);
            CSLog.d(str3, stringBuilder3.toString());
            int length = availableModemConfigurations.length;
            while (i < length) {
                str3 = availableModemConfigurations[i];
                if (str3.endsWith(stringBuilder2)) {
                    break;
                }
                i++;
            }
            str3 = str2;
        }
        str2 = TAG;
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("modemConfiguration: ");
        stringBuilder4.append(str3);
        CSLog.d(str2, stringBuilder4.toString());
        return str3;
    }

    public String getModemConfigurationNeeded(String str) {
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("updateModem - modemVariant = ");
        stringBuilder.append(str);
        CSLog.d(str2, stringBuilder.toString());
        try {
            String currentModemConfig = this.mModemSwitcher.getCurrentModemConfig();
            str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Current modem: '");
            stringBuilder2.append(currentModemConfig);
            stringBuilder2.append("'");
            CSLog.d(str2, stringBuilder2.toString());
            if (ModemSwitcher.SINGLE_MODEM_FS.equalsIgnoreCase(currentModemConfig)) {
                return "";
            }
            str2 = getModemConfiguration(str);
            if (!TextUtils.isEmpty(str2) && !str2.equalsIgnoreCase(currentModemConfig)) {
                return str2;
            }
            CSLog.d(TAG, "updateModem - No need to update modem.");
            return "";
        } catch (IOException e) {
            CSLog.e(TAG, "Could not retrieve current modem configuration.");
            return "";
        }
    }

    public boolean setConfiguration(String str) {
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("setConfiguration - modem configuration = ");
        stringBuilder.append(str);
        CSLog.d(str2, stringBuilder.toString());
        String string = this.mPreference.getString(SAVED_MODEM_CONFIG, "");
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        StringBuilder stringBuilder2;
        if (this.mModemSwitcher.isModemStatusSuccess() || !string.equals(str)) {
            Editor edit = this.mPreference.edit();
            edit.putString(SAVED_MODEM_CONFIG, str);
            edit.commit();
            string = TAG;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Updating the modem configuration with ");
            stringBuilder2.append(str);
            CSLog.d(string, stringBuilder2.toString());
            if (this.mModemSwitcher.setModemConfiguration(str)) {
                CSLog.d(TAG, "Modem configuration set");
                return true;
            }
            CSLog.d(TAG, "Unable to set modem configuration");
            return false;
        }
        string = TAG;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Modem id: ");
        stringBuilder2.append(str);
        stringBuilder2.append(" has been unsuccessfully tried");
        CSLog.e(string, stringBuilder2.toString());
        return false;
    }
}
