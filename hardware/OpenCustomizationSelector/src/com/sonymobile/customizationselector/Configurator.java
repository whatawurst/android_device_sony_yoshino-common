package com.sonymobile.customizationselector;

import android.content.Context;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.sonymobile.miscta.MiscTA;
import com.sonymobile.miscta.MiscTaException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Configurator {

    private static final String TAG = Configurator.class.getSimpleName();
    public static final String PREF_PKG = "CS";

    public static final String KEY_CONFIG_ID = "config_id";
    public static final String KEY_MODEM = "modem";
    public static final String KEY_SIM_ID = "sim_id";

    private static final String OLD_CONFIG_KEY = "config_key";
    private static final String PROP_CUST = "ro.semc.version.cust";
    private static final String PROP_CUST_REV = "ro.semc.version.cust_revision";
    private static final String PROP_SIM_CONFIG_ID = "persist.sys.sim_config_ids";
    private static final String PROP_SW = "ro.semc.version.sw";
    private static final String PROP_SW_REV = "ro.semc.version.sw_revision";
    private static final String PROP_TA_AC_VERSION = "ro.semc.version.cust.active";

    private static final int TA_AC_VERSION = 2212;
    private static final int TA_FOTA_INTERNAL = 2404;

    private final PersistableBundle mBundle;
    private final Context mContext;
    private String mConfigId = "", mModem = "";

    public Configurator(Context context, PersistableBundle persistableBundle) {
        this.mContext = context;
        this.mBundle = persistableBundle;
    }

    private boolean anythingChangedSinceLastEvaluation() {
        String configKey = getTargetContext().getSharedPreferences(PREF_PKG, Context.MODE_PRIVATE).getString(OLD_CONFIG_KEY, "");
        CSLog.d(TAG, "OldConfigKey: " + configKey);
        return !configKey.equals(createCurrentConfigurationKey());
    }

    public static void clearMiscTaConfigId() {
        CSLog.d(TAG, "Clear MiscTa value for Config Id");
        MiscTA.write(TA_AC_VERSION, "".getBytes(StandardCharsets.UTF_8));
    }

    private String createCurrentConfigurationKey() {
        String status = SystemProperties.get(PROP_CUST, "") + SystemProperties.get(PROP_CUST_REV, "") +
                SystemProperties.get(PROP_SW, "") + SystemProperties.get(PROP_SW_REV, "") + getIccid();
        CSLog.d(TAG, "CurrentConfKey: " + status);
        return status;
    }

    private String evaluateCarrierConfigId(String ID) {
        CSLog.d(TAG, "config_id = " + ID);
        return (ID == null || ID.equals(SystemProperties.get(PROP_TA_AC_VERSION, null))) ? null : ID;
    }

    private String evaluateModem(String modem) {
        CSLog.d(TAG, "modem = " + modem);
        return new ModemConfiguration(getTargetContext().getSharedPreferences(PREF_PKG, Context.MODE_PRIVATE)).getModemConfigurationNeeded(modem);
    }

    private String getIccid() {
        TelephonyManager telephonyManager = mContext.getSystemService(TelephonyManager.class);
        int defaultSubId = CommonUtil.getDefaultSubId(mContext);
        String simSerialNumber = (telephonyManager == null || defaultSubId == -1) ? "" : telephonyManager.getSimSerialNumber(defaultSubId);
        CSLog.d(TAG, "getIccid: " + simSerialNumber);
        return simSerialNumber != null ? simSerialNumber : "";
    }

    public Context getTargetContext() {
        if (CommonUtil.isDirectBootEnabled()) {
            CSLog.d(TAG, "Direct Boot is enabled. Use device encrypted storage.");
            return mContext.createDeviceProtectedStorageContext();
        }
        CSLog.d(TAG, "Direct Boot is disabled. Use credential encrypted storage.");
        return mContext;
    }

    public boolean isNewConfigurationNeeded() {
        if (anythingChangedSinceLastEvaluation()) {
            if (mBundle != null) {
                String simID = mBundle.getString(KEY_SIM_ID, "");
                SystemProperties.set(PROP_SIM_CONFIG_ID, simID);

                mModem = evaluateModem(mBundle.getString(KEY_MODEM, ""));
                mConfigId = evaluateCarrierConfigId(mBundle.getString(KEY_CONFIG_ID));

                CSLog.d(TAG, "isNewConfigurationNeeded - Sim Id: " + simID);
                CSLog.d(TAG, "isNewConfigurationNeeded - Modem: " + mModem);
                CSLog.d(TAG, "isNewConfigurationNeeded - Carrier Config Id: " + mConfigId);
            }
            // Actual: (this.mConfigId == null && TextUtils.isEmpty(this.mModem)) ? false : true
            return mConfigId != null || !TextUtils.isEmpty(mModem);
        } else {
            CSLog.d(TAG, "isNewConfigurationNeeded - ConfigKey not updated, no need to evaluate");
            return false;
        }
    }

    public void saveConfigurationKey() {
        String createCurrentConfigurationKey = createCurrentConfigurationKey();
        getTargetContext().getSharedPreferences(PREF_PKG, Context.MODE_PRIVATE).edit()
                .putString(OLD_CONFIG_KEY, createCurrentConfigurationKey)
                .apply();

        CSLog.d(TAG, "saveConfigKey - key saved: " + createCurrentConfigurationKey);
    }

    public void set() {
        CSLog.d(TAG, String.format("Set() - modem = '%s' - carrier config id = '%s'", mModem, mConfigId));
        if (anythingChangedSinceLastEvaluation()) {
            saveConfigurationKey();
            if (mConfigId != null) {
                MiscTA.write(TA_AC_VERSION, mConfigId.getBytes(StandardCharsets.UTF_8));
            }
            if (!TextUtils.isEmpty(mModem)) {
                new ModemConfiguration(getTargetContext().getSharedPreferences(PREF_PKG, Context.MODE_PRIVATE))
                        .setConfiguration(mModem);
            }
        }
    }

    public void reApplyModem() {
        try {
            String modem = ModemSwitcher.getCurrentModemConfig();
            if (!TextUtils.isEmpty(modem)) {
                CSLog.d(TAG, "reApplyModem - current modem: " + modem);
                CSLog.d(TAG, "reApplyModem - Re-writing 2405 with modem " +
                        modem.replace(ModemSwitcher.MODEM_FS_PATH, ""));

                // Store preference without checks - ModemConfiguration:75
                getTargetContext().getSharedPreferences(PREF_PKG, Context.MODE_PRIVATE).edit()
                        .putString(ModemConfiguration.SAVED_MODEM_CONFIG, modem).apply();

                // Way of writing to Misc TA - ModemSwitcher:226
                if (ModemSwitcher.writeModemToMiscTA(new File(modem).getName())) {
                    CSLog.i(TAG, "reApplyModem - 2405 was re-written successfully");

                    try {
                        MiscTA.write(TA_FOTA_INTERNAL, "".getBytes(StandardCharsets.UTF_8));
                        CSLog.i(TAG, "reApplyModem - Modem Switcher 2404 cleared");
                    } catch (MiscTaException e) {
                        CSLog.e(TAG, "reApplyModem - There was an error clearing 2404: ", e);
                    }
                } else {
                    CSLog.e(TAG, "reApplyModem - 2405 was NOT re-written");
                }
            } else {
                CSLog.e(TAG, "reApplyModem - Modem is EMPTY !");
            }
        } catch (IOException e) {
            CSLog.e(TAG, "reApplyModem - There was exception getting current modem: ", e);
        }
    }

    public boolean isFirstApply() {
        return getTargetContext().getSharedPreferences(PREF_PKG, Context.MODE_PRIVATE).getBoolean("first_boot_cs", true);
    }
}
