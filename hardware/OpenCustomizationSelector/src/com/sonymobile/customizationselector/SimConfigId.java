package com.sonymobile.customizationselector;

import android.content.Context;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.sonymobile.customizationselector.Parser.ServiceProvidersParser;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.sonymobile.customizationselector.Parser.XmlConstants.*;

public class SimConfigId {

    private static final String TAG = SimConfigId.class.getSimpleName();

    private static final int MCC_LENGTH = 3;
    private static final String SUBSCRIPTION = "subscription";

    private final Context mContext;

    public SimConfigId(Context context) {
        this.mContext = context;
    }

    public static HashMap<String, String> extractSimInfo(TelephonyManager telephonyManager, int subID) {
        HashMap<String, String> hashMap = new HashMap<>();

        String simOperator = telephonyManager.getSimOperator(subID);
        String simOperatorName = telephonyManager.getSimOperatorName(subID);
        String subscriberId = telephonyManager.getSubscriberId(subID);
        String groupIdLevel1 = telephonyManager.getGroupIdLevel1(subID);
        String groupIdLevel2 = telephonyManager.getGroupIdLevel1(subID);
        String simSerialNumber = telephonyManager.getSimSerialNumber(subID);

        if (!TextUtils.isEmpty(simOperator) && !TextUtils.isEmpty(subscriberId)) {
            simOperatorName = simOperatorName != null ? simOperatorName.replaceAll("[\n\r]", "") : "";
            if (groupIdLevel1 == null) {
                groupIdLevel1 = "";
            }
            if (groupIdLevel2 == null) {
                groupIdLevel2 = "";
            }
            if (simSerialNumber == null) {
                simSerialNumber = "";
            }
            hashMap.put(MCC, simOperator.substring(0, MCC_LENGTH));
            hashMap.put(MNC, simOperator.substring(MCC_LENGTH));
            hashMap.put(SP, simOperatorName.trim());
            hashMap.put(IMSI, subscriberId);
            hashMap.put(GID1, groupIdLevel1);
            hashMap.put(GID2, groupIdLevel2);
            hashMap.put(ICCID, simSerialNumber);
            hashMap.put(SUBSCRIPTION, String.valueOf(subID));
        }
        return hashMap;
    }

    private String getIdFromSimValues(HashMap<String, String> hashMap) {
        return getMappingMatch(ServiceProvidersParser.getServiceProviders(mContext), hashMap);
    }

    private String getMappingMatch(List<SimCombination> list, HashMap<String, String> simParams) {
        String simConfigId = null;
        int numberOfMatches = 0;
        for (SimCombination simCombo : list) {
            int count = 0;
            if (simCombo.getMCC() != null) {
                if (simCombo.getMCC().equals(simParams.get(MCC))) {
                    CSLog.d(TAG, "getMappingMatch - mcc: " + simCombo.getMCC() + " for: " + simCombo.getSimConfigId());
                    count++;
                }
            }
            if (simCombo.getMNC() != null) {
                if (simCombo.getMNC().equals(simParams.get(MNC))) {
                    CSLog.d(TAG, "getMappingMatch - mnc: " + simCombo.getMNC() + " for: " + simCombo.getSimConfigId());
                    count++;
                }
            }
            if (simCombo.getServiceProvider() != null) {
                if (!matchOnSP(simCombo.getServiceProvider(), simParams.get(SP))) {
                    CSLog.d(TAG, "getMappingMatch - Go to next simCombination since there is no match on Service provider for: "
                            + simCombo.getSimConfigId());
                } else {
                    CSLog.d(TAG, "getMappingMatch - sp: " + simCombo.getServiceProvider() + " for: " + simCombo.getSimConfigId());
                    count++;
                }
            }
            if (simCombo.getIMSI() != null) {
                if (!matchOnImsi(simCombo.getIMSI(), simParams.get(IMSI))) {
                    CSLog.d(TAG, "getMappingMatch - Go to next simCombination since there is no match on IMSI for: " + simCombo.getSimConfigId());
                } else {
                    CSLog.d(TAG, "getMappingMatch - imsi: " + simCombo.getIMSI() + " for: " + simCombo.getSimConfigId());
                    count++;
                }
            }
            if (simCombo.getGid1() != null) {
                if (simParams.get(GID1) == null || !simParams.get(GID1).toLowerCase().startsWith(simCombo.getGid1().toLowerCase())) {
                    CSLog.d(TAG, "getMappingMatch - Go to next simCombination since there is no match on GID1 for: " + simCombo.getGid1());
                } else {
                    CSLog.d(TAG, "getMappingMatch - gid1: " + simCombo.getGid1() + " for: " + simCombo.getSimConfigId());
                    count++;
                }
            }
            if (simCombo.getGid2() != null) {
                if (simParams.get(GID2) == null || !simParams.get(GID2).toLowerCase().startsWith(simCombo.getGid2().toLowerCase())) {
                    CSLog.d(TAG, "getMappingMatch - Go to next simCombination since there is no match on GID2 for: " + simCombo.getGid2());
                } else {
                    CSLog.d(TAG, "getMappingMatch - gid2: " + simCombo.getGid2() + " for: " + simCombo.getSimConfigId());
                    count++;
                }
            }
            if (count > numberOfMatches) {
                numberOfMatches = count;
                simConfigId = simCombo.getSimConfigId();
                CSLog.d(TAG, "Saving id: " + simConfigId + " - nbr matches: " + numberOfMatches);
            }
        }
        return simConfigId;
    }

    private boolean matchOnImsi(String str, String str2) {
        return str2 != null && Pattern.compile(str).matcher(str2).matches();
    }

    private boolean matchOnSP(String str, String str2) {
        return NULL_VALUE.equalsIgnoreCase(str) ?
                TextUtils.isEmpty(str2) || NULL_VALUE.equalsIgnoreCase(str2) :
                str2 != null && Pattern.compile(str).matcher(str2).matches();
    }

    public String getId() {
        String id = "";
        TelephonyManager telephonyManager = mContext.getSystemService(TelephonyManager.class);
        int subId = CommonUtil.getDefaultSubId(mContext);

        if (telephonyManager != null && subId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            HashMap<String, String> simInfo = extractSimInfo(telephonyManager, subId);
            CSLog.d(TAG, "***********************************");
            CSLog.d(TAG, "extractSimInfo: " + simInfo.toString());
            CSLog.d(TAG, "***********************************");
            id = getIdFromSimValues(simInfo);
        }

        CSLog.d(TAG, "***********************************");
        StringBuilder sb = new StringBuilder();
        sb.append("Best SIM configuration id= ");
        if (TextUtils.isEmpty(id)) {
            sb.append("NOT FOUND - RETURNING \"\"");
        } else {
            sb.append(id);
        }
        CSLog.d(TAG, sb.toString());
        CSLog.d(TAG, "***********************************");
        return id;
    }
}
