package com.sonymobile.customizationselector;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

class SimConfigId {
    static final String GID1 = "gid1";
    static final String GID2 = "gid2";
    static final String ICCID = "iccid";
    static final String IMSI = "imsi";
    static final String MCC = "mcc";
    private static final int MCC_LENGTH = 3;
    static final String MNC = "mnc";
    static final String SP = "sp";
    static final String SUBSCRIPTION = "subscription";
    static final String TAG = SimConfigId.class.getSimpleName();
    private final Context mContext;

    SimConfigId(Context context) {
        this.mContext = context;
    }

    static HashMap<String, String> extractSimInfo(TelephonyManager telephonyManager, int i) {
        HashMap hashMap = new HashMap();
        String simOperator = telephonyManager.getSimOperator(i);
        String simOperatorName = telephonyManager.getSimOperatorName(i);
        String subscriberId = telephonyManager.getSubscriberId(i);
        Object groupIdLevel1 = telephonyManager.getGroupIdLevel1(i);
        Object groupIdLevel12 = telephonyManager.getGroupIdLevel1(i);
        Object simSerialNumber = telephonyManager.getSimSerialNumber(i);
        if (!(TextUtils.isEmpty(simOperator) || TextUtils.isEmpty(subscriberId))) {
            simOperatorName = simOperatorName != null ? simOperatorName.replaceAll("[\n\r]", "") : "";
            if (groupIdLevel1 == null) {
                groupIdLevel1 = "";
            }
            if (groupIdLevel12 == null) {
                groupIdLevel12 = "";
            }
            if (simSerialNumber == null) {
                simSerialNumber = "";
            }
            hashMap.put(MCC, simOperator.substring(0, 3));
            hashMap.put(MNC, simOperator.substring(3));
            hashMap.put(SP, simOperatorName.trim());
            hashMap.put(IMSI, subscriberId);
            hashMap.put(GID1, groupIdLevel1);
            hashMap.put(GID2, groupIdLevel12);
            hashMap.put(ICCID, simSerialNumber);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i);
            hashMap.put(SUBSCRIPTION, stringBuilder.toString());
        }
        return hashMap;
    }

    private String getIdFromSimValues(HashMap<String, String> hashMap) {
        return getMappingMatch(Parser.ServiceProvidersParser.getServiceProviders(this.mContext), hashMap);
    }

    private String getMappingMatch(List<SimCombination> list, HashMap<String, String> hashMap) {
        int i = 0;
        String str = null;
        for (SimCombination simCombination : list) {
            int i2;
            String str2;
            StringBuilder stringBuilder;
            int i3;
            StringBuilder stringBuilder2;
            int i4;
            if (simCombination.getMCC() == null) {
                i2 = 0;
            } else if (simCombination.getMCC().equals(hashMap.get(MCC))) {
                str2 = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("getMappingMatch - mcc: ");
                stringBuilder.append(simCombination.getMCC());
                stringBuilder.append(" for: ");
                stringBuilder.append(simCombination.getSimConfigId());
                CSLog.d(str2, stringBuilder.toString());
                i2 = 1;
            }
            if (simCombination.getMNC() == null) {
                i3 = i2;
            } else if (simCombination.getMNC().equals(hashMap.get(MNC))) {
                String str3 = TAG;
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("getMappingMatch - mnc: ");
                stringBuilder2.append(simCombination.getMNC());
                stringBuilder2.append(" for: ");
                stringBuilder2.append(simCombination.getSimConfigId());
                CSLog.d(str3, stringBuilder2.toString());
                i3 = i2 + 1;
            }
            if (simCombination.getServiceProvider() != null) {
                if (matchOnSP(simCombination.getServiceProvider(), (String) hashMap.get(SP))) {
                    str2 = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("getMappingMatch - sp: ");
                    stringBuilder2.append(simCombination.getServiceProvider());
                    stringBuilder2.append(" for: ");
                    stringBuilder2.append(simCombination.getSimConfigId());
                    CSLog.d(str2, stringBuilder2.toString());
                    i3++;
                } else {
                    str2 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("getMappingMatch - Go to next simCombination since there is no match on Service provider for: ");
                    stringBuilder.append(simCombination.getSimConfigId());
                    CSLog.d(str2, stringBuilder.toString());
                }
            }
            if (simCombination.getIMSI() != null) {
                if (matchOnImsi(simCombination.getIMSI(), (String) hashMap.get(IMSI))) {
                    str2 = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("getMappingMatch - imsi: ");
                    stringBuilder2.append(simCombination.getIMSI());
                    stringBuilder2.append(" for: ");
                    stringBuilder2.append(simCombination.getSimConfigId());
                    CSLog.d(str2, stringBuilder2.toString());
                    i3++;
                } else {
                    str2 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("getMappingMatch - Go to next simCombination since there is no match on IMSI for: ");
                    stringBuilder.append(simCombination.getSimConfigId());
                    CSLog.d(str2, stringBuilder.toString());
                }
            }
            if (simCombination.getGid1() != null) {
                if (hashMap.get(GID1) == null || !((String) hashMap.get(GID1)).toLowerCase().startsWith(simCombination.getGid1().toLowerCase())) {
                    str2 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("getMappingMatch - Go to next simCombination since there is no match on GID1 for: ");
                    stringBuilder.append(simCombination.getGid1());
                    CSLog.d(str2, stringBuilder.toString());
                } else {
                    str2 = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("getMappingMatch - gid1: ");
                    stringBuilder2.append(simCombination.getGid1());
                    stringBuilder2.append(" for: ");
                    stringBuilder2.append(simCombination.getSimConfigId());
                    CSLog.d(str2, stringBuilder2.toString());
                    i3++;
                }
            }
            if (simCombination.getGid2() != null) {
                if (hashMap.get(GID2) == null || !((String) hashMap.get(GID2)).toLowerCase().startsWith(simCombination.getGid2().toLowerCase())) {
                    str2 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("getMappingMatch - Go to next simCombination since there is no match on GID2 for: ");
                    stringBuilder.append(simCombination.getGid2());
                    CSLog.d(str2, stringBuilder.toString());
                } else {
                    str2 = TAG;
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("getMappingMatch - gid2: ");
                    stringBuilder2.append(simCombination.getGid2());
                    stringBuilder2.append(" for: ");
                    stringBuilder2.append(simCombination.getSimConfigId());
                    CSLog.d(str2, stringBuilder2.toString());
                    i3++;
                }
            }
            if (i3 > i) {
                str2 = simCombination.getSimConfigId();
                String str4 = TAG;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("Saving id: ");
                stringBuilder3.append(str2);
                stringBuilder3.append(" - nbr matches: ");
                stringBuilder3.append(i3);
                CSLog.d(str4, stringBuilder3.toString());
                i4 = i3;
            } else {
                i4 = i;
                str2 = str;
            }
            i = i4;
            str = str2;
        }
        return str;
    }

    private boolean matchOnImsi(String str, String str2) {
        return str2 != null ? Pattern.compile(str).matcher(str2).matches() : false;
    }

    private boolean matchOnSP(String str, String str2) {
        return "null".equalsIgnoreCase(str) ? TextUtils.isEmpty(str2) || "null".equalsIgnoreCase(str2) : str2 != null ? Pattern.compile(str).matcher(str2).matches() : false;
    }

    /* Access modifiers changed, original: 0000 */
    public String getId() {
        CharSequence charSequence;
        String str;
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int defaultSubId = CommonUtil.getDefaultSubId(this.mContext);
        if (telephonyManager == null || defaultSubId == -1) {
            charSequence = "";
        } else {
            HashMap extractSimInfo = extractSimInfo(telephonyManager, defaultSubId);
            CSLog.d(TAG, "***********************************");
            String str2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("extractSimInfo: ");
            stringBuilder.append(extractSimInfo.toString());
            CSLog.d(str2, stringBuilder.toString());
            CSLog.d(TAG, "***********************************");
            charSequence = getIdFromSimValues(extractSimInfo);
        }
        CSLog.d(TAG, "***********************************");
        String str3 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Best SIM configuration id= ");
        if (TextUtils.isEmpty(charSequence)) {
            str = "NOT FOUND - RETURNING \"\"";
        } else {
            CharSequence str4 = charSequence;
        }
        stringBuilder2.append(str4);
        CSLog.d(str3, stringBuilder2.toString());
        CSLog.d(TAG, "***********************************");
        return charSequence;
    }
}
