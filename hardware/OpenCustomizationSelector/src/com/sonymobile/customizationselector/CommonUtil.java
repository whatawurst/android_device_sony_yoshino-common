package com.sonymobile.customizationselector;

import android.content.Context;
import android.os.PersistableBundle;
import android.os.storage.StorageManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.sonymobile.customizationselector.Parser.DynamicConfigParser;
import com.sonymobile.customizationselector.Parser.ModemConfParser;

import java.util.HashMap;
import java.util.List;

import static com.sonymobile.customizationselector.Parser.XmlConstants.*;

class CommonUtil {

    private static final String TAG = CommonUtil.class.getSimpleName();
    private static final int MIN_MCC_MNC_LENGTH = 5;

    public static PersistableBundle getCarrierBundle(Context context) {
        PersistableBundle persistableBundle = new PersistableBundle(3);

        String id = new SimConfigId(context).getId();

        HashMap<String, String> configuration = DynamicConfigParser.getConfiguration(context);
        String str = configuration.get(id);
        if (TextUtils.isEmpty(str)) {
            str = configuration.get(ANY_SIM);
        }
        if (str == null || DEFAULT_CONFIG.equalsIgnoreCase(str)) {
            str = "";
        }
        String parseModemConf = ModemConfParser.parseModemConf(str);
        CSLog.i(TAG, String.format("Returning bundle with sim id %s, modem: %s, config id: %s", id, parseModemConf, str));
        persistableBundle.putString(SIM_ID, id);
        persistableBundle.putString("modem", parseModemConf);
        persistableBundle.putString(CONFIG_ID, str);
        return persistableBundle;
    }

    public static int getDefaultSubId(Context context) {
        int subscriptionId;
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();

        if (!SubscriptionManager.isUsableSubIdValue(defaultDataSubscriptionId)) {
            List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
            if (activeSubscriptionInfoList != null) {
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    if (SubscriptionManager.isUsableSubIdValue(subscriptionInfo.getSubscriptionId())) {
                        subscriptionId = subscriptionInfo.getSubscriptionId();
                        CSLog.d(TAG, "getDefaultSubId: " + subscriptionId);
                        return subscriptionId;
                    }
                }
            }
        }
        subscriptionId = defaultDataSubscriptionId;
        CSLog.d(TAG, "getDefaultSubId: " + subscriptionId);
        return subscriptionId;
    }

    public static boolean isDefaultDataSlot(Context context, int subID) {
        return getDefaultSubId(context) == subID;
    }

    public static boolean isDirectBootEnabled() {
        return StorageManager.isFileEncryptedNativeOrEmulated();
    }

    public static boolean isDualSim(Context context) {
        TelephonyManager telephonyManager = context.getSystemService(TelephonyManager.class);
        return telephonyManager != null && telephonyManager.getPhoneCount() > 1;
    }

    public static boolean isMandatorySimParamsAvailable(Context context, int i) {
        TelephonyManager telephonyManager = context.getSystemService(TelephonyManager.class);

        if (telephonyManager != null) {
            String simOperator = telephonyManager.getSimOperator(i);
            String subscriberId = telephonyManager.getSubscriberId(i);
            String simSerialNumber = telephonyManager.getSimSerialNumber(i);
            String simOperatorName = telephonyManager.getSimOperatorName(i);
            String groupIdLevel1 = telephonyManager.getGroupIdLevel1(i);
            CSLog.d(TAG, "SimOperator= " + simOperator + ", IMSI= " + subscriberId + ", ICCID = " + simSerialNumber
                    + ", SPN = " + simOperatorName + ", gid1 = " + groupIdLevel1);

            if (!TextUtils.isEmpty(simOperator) && !TextUtils.isEmpty(subscriberId) && !TextUtils.isEmpty(simSerialNumber)) {
                CSLog.d(TAG, "isMandatorySimParamsAvailable: true");
                return true;
            }
        }
        CSLog.d(TAG, "isMandatorySimParamsAvailable: false");
        return false;
    }

    public static boolean isSIMLoaded(Context context, int subID) {
        TelephonyManager telephonyManager = context.getSystemService(TelephonyManager.class);

        if (telephonyManager != null && subID != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            int slotIndex = SubscriptionManager.getSlotIndex(subID);
            if (slotIndex != SubscriptionManager.INVALID_SIM_SLOT_INDEX) {
                boolean isLoaded = telephonyManager.getSimState(slotIndex) == TelephonyManager.SIM_STATE_READY
                        && !TextUtils.isEmpty(telephonyManager.getSubscriberId(subID))
                        && !TextUtils.isEmpty(telephonyManager.getSimOperator(subID))
                        && telephonyManager.getSimOperator(subID).length() >= MIN_MCC_MNC_LENGTH;

                CSLog.d(TAG, "isSIMLoaded: " + isLoaded);
                return isLoaded;
            }
        }
        CSLog.d(TAG, "isSIMLoaded: false");
        return false;
    }
}
