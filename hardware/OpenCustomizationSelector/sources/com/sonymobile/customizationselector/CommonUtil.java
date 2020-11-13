package com.sonymobile.customizationselector;

import android.content.Context;
import android.os.PersistableBundle;
import android.os.storage.StorageManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.List;
import com.sonymobile.customizationselector.Parser.*;

class CommonUtil {
    private static final int MIN_MCCMNC_LENGTH = 5;
    private static final String TAG = CommonUtil.class.getSimpleName();

    CommonUtil() {
    }

    static PersistableBundle getCarrierBundle(Context context) {
        PersistableBundle persistableBundle = new PersistableBundle(3);
        String id = new SimConfigId(context).getId();
        HashMap configuration = DynamicConfigParser.getConfiguration(context);
        String str = (String) configuration.get(id);
        if (TextUtils.isEmpty(str)) {
            str = (String) configuration.get("anysim");
        }
        if (str == null || "default".equalsIgnoreCase(str)) {
            str = "";
        }
        String parseModemConf = ModemConfParser.parseModemConf(str);
        CSLog.i(TAG, String.format("Returning bundle with sim id %s, modem: %s, config id: %s", new Object[]{id, parseModemConf, str}));
        persistableBundle.putString("sim_id", id);
        persistableBundle.putString("modem", parseModemConf);
        persistableBundle.putString("config_id", str);
        return persistableBundle;
    }

    static int getDefaultSubId(Context context) {
        int subscriptionId;
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        if (!SubscriptionManager.isUsableSubIdValue(defaultDataSubscriptionId)) {
            List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
            if (activeSubscriptionInfoList != null) {
                for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                    if (SubscriptionManager.isUsableSubIdValue(subscriptionInfo.getSubscriptionId())) {
                        subscriptionId = subscriptionInfo.getSubscriptionId();
                        break;
                    }
                }
            }
        }
        subscriptionId = defaultDataSubscriptionId;
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getDefaultSubId: ");
        stringBuilder.append(subscriptionId);
        CSLog.d(str, stringBuilder.toString());
        return subscriptionId;
    }

    static boolean isDefaultDataSlot(Context context, int i) {
        return getDefaultSubId(context) == i;
    }

    static boolean isDirectBootEnabled() {
        return StorageManager.isFileEncryptedNativeOrEmulated();
    }

    static boolean isDualSim(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        return telephonyManager != null ? telephonyManager.getPhoneCount() > 1 : false;
    }

    static boolean isMandatorySimParmsAvailable(Context context, int i) {
        String simOperator;
        boolean z;
        StringBuilder stringBuilder;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager != null) {
            simOperator = telephonyManager.getSimOperator(i);
            String subscriberId = telephonyManager.getSubscriberId(i);
            String simSerialNumber = telephonyManager.getSimSerialNumber(i);
            String simOperatorName = telephonyManager.getSimOperatorName(i);
            String groupIdLevel1 = telephonyManager.getGroupIdLevel1(i);
            String str = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("SimOperator= ");
            stringBuilder2.append(simOperator);
            stringBuilder2.append(", IMSI= ");
            stringBuilder2.append(subscriberId);
            stringBuilder2.append(", ICCID = ");
            stringBuilder2.append(simSerialNumber);
            stringBuilder2.append(", SPN = ");
            stringBuilder2.append(simOperatorName);
            stringBuilder2.append(", gid1 = ");
            stringBuilder2.append(groupIdLevel1);
            CSLog.d(str, stringBuilder2.toString());
            if (!(TextUtils.isEmpty(simOperator) || TextUtils.isEmpty(subscriberId) || TextUtils.isEmpty(simSerialNumber))) {
                z = true;
                simOperator = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("isMandatorySimParmsAvailable: ");
                stringBuilder.append(z);
                CSLog.d(simOperator, stringBuilder.toString());
                return z;
            }
        }
        z = false;
        simOperator = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("isMandatorySimParmsAvailable: ");
        stringBuilder.append(z);
        CSLog.d(simOperator, stringBuilder.toString());
        return z;
    }

    static boolean isSIMLoaded(Context context, int i) {
        boolean z;
        String str;
        StringBuilder stringBuilder;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (!(telephonyManager == null || i == -1)) {
            int slotIndex = SubscriptionManager.getSlotIndex(i);
            if (slotIndex != -1) {
                z = telephonyManager.getSimState(slotIndex) == 5 && !TextUtils.isEmpty(telephonyManager.getSubscriberId(i)) && !TextUtils.isEmpty(telephonyManager.getSimOperator(i)) && telephonyManager.getSimOperator(i).length() >= 5;
                str = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("isSIMLoaded: ");
                stringBuilder.append(z);
                CSLog.d(str, stringBuilder.toString());
                return z;
            }
        }
        z = false;
        str = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("isSIMLoaded: ");
        stringBuilder.append(z);
        CSLog.d(str, stringBuilder.toString());
        return z;
    }
}
