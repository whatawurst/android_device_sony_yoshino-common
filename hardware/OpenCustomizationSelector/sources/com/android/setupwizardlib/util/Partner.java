package com.android.setupwizardlib.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

public class Partner {
    private static final String ACTION_PARTNER_CUSTOMIZATION = "com.android.setupwizard.action.PARTNER_CUSTOMIZATION";
    private static final String TAG = "(SUW) Partner";
    private static Partner sPartner;
    private static boolean sSearched = false;
    private final String mPackageName;
    private final Resources mResources;

    public static class ResourceEntry {
        public int id;
        public boolean isOverlay;
        public Resources resources;

        ResourceEntry(Resources resources, int i, boolean z) {
            this.resources = resources;
            this.id = i;
            this.isOverlay = z;
        }
    }

    private Partner(String str, Resources resources) {
        this.mPackageName = str;
        this.mResources = resources;
    }

    public static Partner get(Context context) {
        Partner partner;
        synchronized (Partner.class) {
            ApplicationInfo applicationInfo;
            try {
                if (!sSearched) {
                    PackageManager packageManager = context.getPackageManager();
                    Intent intent = new Intent(ACTION_PARTNER_CUSTOMIZATION);
                    for (ResolveInfo resolveInfo : VERSION.SDK_INT >= 24 ? packageManager.queryBroadcastReceivers(intent, 1835008) : packageManager.queryBroadcastReceivers(intent, 0)) {
                        if (resolveInfo.activityInfo != null) {
                            applicationInfo = resolveInfo.activityInfo.applicationInfo;
                            if ((applicationInfo.flags & 1) != 0) {
                                sPartner = new Partner(applicationInfo.packageName, packageManager.getResourcesForApplication(applicationInfo));
                                break;
                            }
                            continue;
                        }
                    }
                    sSearched = true;
                }
                partner = sPartner;
            } catch (NameNotFoundException e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to find resources for ");
                stringBuilder.append(applicationInfo.packageName);
                Log.w(TAG, stringBuilder.toString());
            } catch (Throwable th) {
                Class cls = Partner.class;
            }
        }
        return partner;
    }

    public static int getColor(Context context, @ColorRes int i) {
        ResourceEntry resourceEntry = getResourceEntry(context, i);
        return resourceEntry.resources.getColor(resourceEntry.id);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int i) {
        ResourceEntry resourceEntry = getResourceEntry(context, i);
        return resourceEntry.resources.getDrawable(resourceEntry.id);
    }

    public static ResourceEntry getResourceEntry(Context context, @AnyRes int i) {
        Partner partner = get(context);
        if (partner != null) {
            Resources resources = context.getResources();
            int identifier = partner.getIdentifier(resources.getResourceEntryName(i), resources.getResourceTypeName(i));
            if (identifier != 0) {
                return new ResourceEntry(partner.mResources, identifier, true);
            }
        }
        return new ResourceEntry(context.getResources(), i, false);
    }

    public static String getString(Context context, @StringRes int i) {
        ResourceEntry resourceEntry = getResourceEntry(context, i);
        return resourceEntry.resources.getString(resourceEntry.id);
    }

    public static CharSequence getText(Context context, @StringRes int i) {
        ResourceEntry resourceEntry = getResourceEntry(context, i);
        return resourceEntry.resources.getText(resourceEntry.id);
    }

    @VisibleForTesting
    public static void resetForTesting() {
        synchronized (Partner.class) {
            try {
                sSearched = false;
                sPartner = null;
            } finally {
                Class cls = Partner.class;
            }
        }
    }

    public int getIdentifier(String str, String str2) {
        return this.mResources.getIdentifier(str, str2, this.mPackageName);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public Resources getResources() {
        return this.mResources;
    }
}
