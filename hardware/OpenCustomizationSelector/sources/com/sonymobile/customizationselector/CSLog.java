package com.sonymobile.customizationselector;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CSLog {
    static final boolean DEBUG;
    private static final String PREFIX = "CS-";
    private static final int SKIPPED = 0;

    static {
        boolean z = (Build.TYPE.contentEquals("userdebug") || Build.TYPE.contentEquals("eng")) ? true : DEBUG;
        DEBUG = z;
    }

    static int d(String str, String str2) {
        if (!DEBUG) {
            return 0;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append(str);
        return Log.d(stringBuilder.toString(), str2);
    }

    static int e(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append(str);
        return Log.e(stringBuilder.toString(), str2);
    }

    static int e(String str, String str2, Exception exception) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append(str);
        return Log.e(stringBuilder.toString(), str2, exception);
    }

    static int i(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append(str);
        return Log.i(stringBuilder.toString(), str2);
    }

    static void logSimValues(Context context, String str) {
        String str2;
        String str3;
        String str4;
        int defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null || defaultSubscriptionId == -1) {
            str2 = "";
            str3 = "";
            str4 = "";
        } else {
            str3 = telephonyManager.getSimOperator(defaultSubscriptionId);
            String simOperatorName = telephonyManager.getSimOperatorName(defaultSubscriptionId);
            str2 = telephonyManager.getSubscriberId(defaultSubscriptionId);
            if (str3 == null) {
                str3 = "";
            }
            str4 = simOperatorName != null ? simOperatorName.replaceAll("[\n\r]", "").trim() : "";
        }
        StringBuilder stringBuilder = new StringBuilder("SimValues: MCCMNC=");
        stringBuilder.append(str3);
        stringBuilder.append(", SP-name=");
        stringBuilder.append(str4);
        stringBuilder.append(", IMSI=");
        stringBuilder.append(str2);
        d(str, stringBuilder.toString());
    }

    static void logVersion(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        if (packageManager != null) {
            try {
                packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
        if (packageInfo != null) {
            String str2 = packageInfo.versionName;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Version: ");
            stringBuilder.append(str2);
            d(str, stringBuilder.toString());
        }
    }

    static int w(String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PREFIX);
        stringBuilder.append(str);
        return Log.w(stringBuilder.toString(), str2);
    }
}
