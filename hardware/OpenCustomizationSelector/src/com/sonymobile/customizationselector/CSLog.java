package com.sonymobile.customizationselector;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CSLog {

    private static final String PREFIX = "CS-";

    public static void d(String tag, String msg) {
        Log.d(PREFIX + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(PREFIX + tag, msg);
    }

    public static void e(String tag, String msg, Exception e) {
        Log.e(PREFIX + tag, msg, e);
    }

    public static void i(String tag, String msg) {
        Log.i(PREFIX + tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(PREFIX + tag, msg);
    }

    public static void logSimValues(Context context, String tag) {
        String subscriberID = "", simOP = "", simOpName = "";

        int defaultSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        TelephonyManager telephonyManager = context.getSystemService(TelephonyManager.class);

        if (defaultSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            simOP = telephonyManager.getSimOperator(defaultSubscriptionId);
            if (simOP == null) {
                simOP = "";
            }
            subscriberID = telephonyManager.getSubscriberId(defaultSubscriptionId);
            String simOperatorName = telephonyManager.getSimOperatorName(defaultSubscriptionId);
            simOpName = simOperatorName != null ? simOperatorName.replaceAll("[\n\r]", "").trim() : "";
        }
        d(tag, "SimValues: MCC-MNC=" + simOP + ", SP-name=" + simOpName + ", IMSI=" + subscriberID);
    }

    public static void logVersion(Context context, String tag) {
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
            d(tag, "Version: " + packageInfo.versionName);
        }
    }
}
