package com.sonymobile.customizationselector;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.sonymobile.miscta.MiscTA;
import java.nio.charset.StandardCharsets;

class Configurator {
    static final String KEY_CONFIG_ID = "config_id";
    static final String KEY_MODEM = "modem";
    static final String KEY_SIM_ID = "sim_id";
    private static final String OLD_CONFIG_KEY = "config_key";
    private static final String PROP_CUST = "ro.semc.version.cust";
    private static final String PROP_CUST_REV = "ro.semc.version.cust_revision";
    private static final String PROP_SIM_CONFIG_ID = "persist.sys.sim_config_ids";
    private static final String PROP_SW = "ro.semc.version.sw";
    private static final String PROP_SW_REV = "ro.semc.version.sw_revision";
    private static final String PROP_TA_AC_VERSION = "ro.semc.version.cust.active";
    private static final String TAG = Configurator.class.getSimpleName();
    private static final int TA_AC_VERSION = 2212;
    private final PersistableBundle mBundle;
    private String mConfigId = "";
    private final Context mContext;
    private String mModem = "";

    Configurator(Context context, PersistableBundle persistableBundle) {
        this.mContext = context;
        this.mBundle = persistableBundle;
    }

    private boolean anythingChangedSinceLastEvaluation() {
        String string = PreferenceManager.getDefaultSharedPreferences(getTargetContext()).getString(OLD_CONFIG_KEY, "");
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("OldConfigKey: ");
        stringBuilder.append(string);
        CSLog.d(str, stringBuilder.toString());
        return !string.equals(createCurrentConfigurationKey());
    }

    static void clearMiscTaConfigId() {
        CSLog.d(TAG, "Clear MiscTa value for Config Id");
        MiscTA.write(TA_AC_VERSION, "".getBytes(StandardCharsets.UTF_8));
    }

    private String createCurrentConfigurationKey() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SystemProperties.get(PROP_CUST, ""));
        stringBuilder.append(SystemProperties.get(PROP_CUST_REV, ""));
        stringBuilder.append(SystemProperties.get(PROP_SW, ""));
        stringBuilder.append(SystemProperties.get(PROP_SW_REV, ""));
        stringBuilder.append(getIccid());
        String stringBuilder2 = stringBuilder.toString();
        String str = TAG;
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append("CurrentConfKey: ");
        stringBuilder3.append(stringBuilder2);
        CSLog.d(str, stringBuilder3.toString());
        return stringBuilder2;
    }

    private String evaluateCarrierConfigId(String str) {
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("config_id = ");
        stringBuilder.append(str);
        CSLog.d(str2, stringBuilder.toString());
        return (str == null || str.equals(SystemProperties.get(PROP_TA_AC_VERSION, null))) ? null : str;
    }

    private String evaluateModem(String str) {
        String str2 = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("modem = ");
        stringBuilder.append(str);
        CSLog.d(str2, stringBuilder.toString());
        return new ModemConfiguration(PreferenceManager.getDefaultSharedPreferences(getTargetContext())).getModemConfigurationNeeded(str);
    }

    private String getIccid() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int defaultSubId = CommonUtil.getDefaultSubId(this.mContext);
        String simSerialNumber = (telephonyManager == null || defaultSubId == -1) ? "" : telephonyManager.getSimSerialNumber(defaultSubId);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getIccid: ");
        stringBuilder.append(simSerialNumber);
        CSLog.d(str, stringBuilder.toString());
        return simSerialNumber != null ? simSerialNumber : "";
    }

    private Context getTargetContext() {
        if (CommonUtil.isDirectBootEnabled()) {
            CSLog.d(TAG, "Direct Boot is enabled. Use device encrypted storage.");
            return this.mContext.createDeviceProtectedStorageContext();
        }
        CSLog.d(TAG, "Direct Boot is disabled. Use credential encrypted storage.");
        return this.mContext;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isNewConfigurationNeeded() {
        if (anythingChangedSinceLastEvaluation()) {
            if (this.mBundle != null) {
                String string = this.mBundle.getString(KEY_SIM_ID, "");
                SystemProperties.set(PROP_SIM_CONFIG_ID, string);
                this.mModem = evaluateModem(this.mBundle.getString(KEY_MODEM, ""));
                this.mConfigId = evaluateCarrierConfigId(this.mBundle.getString(KEY_CONFIG_ID));
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("isNewConfigurationNeeded - Sim Id: ");
                stringBuilder.append(string);
                CSLog.d(str, stringBuilder.toString());
                string = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("isNewConfigurationNeeded - Modem: ");
                stringBuilder2.append(this.mModem);
                CSLog.d(string, stringBuilder2.toString());
                string = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("isNewConfigurationNeeded - Carrier Config Id: ");
                stringBuilder2.append(this.mConfigId);
                CSLog.d(string, stringBuilder2.toString());
            }
            return (this.mConfigId == null && TextUtils.isEmpty(this.mModem)) ? false : true;
        } else {
            CSLog.d(TAG, "isNewConfigurationNeeded - ConfigKey not updated, no need to evaluate");
            return false;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void saveConfigurationKey() {
        String createCurrentConfigurationKey = createCurrentConfigurationKey();
        Editor edit = PreferenceManager.getDefaultSharedPreferences(getTargetContext()).edit();
        edit.putString(OLD_CONFIG_KEY, createCurrentConfigurationKey);
        edit.commit();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("saveConfigKey - key saved: ");
        stringBuilder.append(createCurrentConfigurationKey);
        CSLog.d(str, stringBuilder.toString());
    }

    /* Access modifiers changed, original: 0000 */
    public void set() {
        CSLog.d(TAG, String.format("Set() - modem = '%s' - carrier config id = '%s'", new Object[]{this.mModem, this.mConfigId}));
        if (anythingChangedSinceLastEvaluation()) {
            saveConfigurationKey();
            if (this.mConfigId != null) {
                MiscTA.write(TA_AC_VERSION, this.mConfigId.getBytes(StandardCharsets.UTF_8));
            }
            if (!TextUtils.isEmpty(this.mModem)) {
                new ModemConfiguration(PreferenceManager.getDefaultSharedPreferences(getTargetContext())).setConfiguration(this.mModem);
            }
        }
    }
}
