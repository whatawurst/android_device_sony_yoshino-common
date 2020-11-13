package com.android.setupwizardlib.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.support.annotation.StyleRes;
import android.support.annotation.VisibleForTesting;
import com.android.setupwizardlib.R;
import java.util.Arrays;

public class WizardManagerHelper {
    private static final String ACTION_NEXT = "com.android.wizard.NEXT";
    @VisibleForTesting
    static final String EXTRA_ACTION_ID = "actionId";
    @VisibleForTesting
    static final String EXTRA_IS_DEFERRED_SETUP = "deferredSetup";
    @VisibleForTesting
    static final String EXTRA_IS_FIRST_RUN = "firstRun";
    @VisibleForTesting
    static final String EXTRA_IS_PRE_DEFERRED_SETUP = "preDeferredSetup";
    private static final String EXTRA_RESULT_CODE = "com.android.setupwizard.ResultCode";
    @VisibleForTesting
    static final String EXTRA_SCRIPT_URI = "scriptUri";
    public static final String EXTRA_THEME = "theme";
    public static final String EXTRA_USE_IMMERSIVE_MODE = "useImmersiveMode";
    @VisibleForTesting
    static final String EXTRA_WIZARD_BUNDLE = "wizardBundle";
    public static final String SETTINGS_GLOBAL_DEVICE_PROVISIONED = "device_provisioned";
    public static final String SETTINGS_SECURE_USER_SETUP_COMPLETE = "user_setup_complete";
    public static final String THEME_GLIF = "glif";
    public static final String THEME_GLIF_LIGHT = "glif_light";
    public static final String THEME_GLIF_V2 = "glif_v2";
    public static final String THEME_GLIF_V2_LIGHT = "glif_v2_light";
    public static final String THEME_GLIF_V3 = "glif_v3";
    public static final String THEME_GLIF_V3_LIGHT = "glif_v3_light";
    public static final String THEME_HOLO = "holo";
    public static final String THEME_HOLO_LIGHT = "holo_light";
    public static final String THEME_MATERIAL = "material";
    public static final String THEME_MATERIAL_LIGHT = "material_light";

    public static void copyWizardManagerExtras(Intent intent, Intent intent2) {
        intent2.putExtra(EXTRA_WIZARD_BUNDLE, intent.getBundleExtra(EXTRA_WIZARD_BUNDLE));
        for (String str : Arrays.asList(new String[]{EXTRA_IS_FIRST_RUN, EXTRA_IS_DEFERRED_SETUP, EXTRA_IS_PRE_DEFERRED_SETUP})) {
            intent2.putExtra(str, intent.getBooleanExtra(str, false));
        }
        for (String str2 : Arrays.asList(new String[]{EXTRA_THEME, EXTRA_SCRIPT_URI, EXTRA_ACTION_ID})) {
            intent2.putExtra(str2, intent.getStringExtra(str2));
        }
    }

    public static Intent getNextIntent(Intent intent, int i) {
        return getNextIntent(intent, i, null);
    }

    public static Intent getNextIntent(Intent intent, int i, Intent intent2) {
        Intent intent3 = new Intent(ACTION_NEXT);
        copyWizardManagerExtras(intent, intent3);
        intent3.putExtra(EXTRA_RESULT_CODE, i);
        if (!(intent2 == null || intent2.getExtras() == null)) {
            intent3.putExtras(intent2.getExtras());
        }
        intent3.putExtra(EXTRA_THEME, intent.getStringExtra(EXTRA_THEME));
        return intent3;
    }

    @StyleRes
    public static int getThemeRes(Intent intent, @StyleRes int i) {
        return getThemeRes(intent.getStringExtra(EXTRA_THEME), i);
    }

    @StyleRes
    public static int getThemeRes(String str, @StyleRes int i) {
        if (str == null) {
            return i;
        }
        Object obj = -1;
        switch (str.hashCode()) {
            case -2128555920:
                if (str.equals(THEME_GLIF_V2_LIGHT)) {
                    obj = 2;
                    break;
                }
                break;
            case -1270463490:
                if (str.equals(THEME_MATERIAL_LIGHT)) {
                    obj = 6;
                    break;
                }
                break;
            case -1241052239:
                if (str.equals(THEME_GLIF_V3_LIGHT)) {
                    obj = null;
                    break;
                }
                break;
            case 3175618:
                if (str.equals(THEME_GLIF)) {
                    obj = 5;
                    break;
                }
                break;
            case 115650329:
                if (str.equals(THEME_GLIF_V2)) {
                    obj = 3;
                    break;
                }
                break;
            case 115650330:
                if (str.equals(THEME_GLIF_V3)) {
                    obj = 1;
                    break;
                }
                break;
            case 299066663:
                if (str.equals(THEME_MATERIAL)) {
                    obj = 7;
                    break;
                }
                break;
            case 767685465:
                if (str.equals(THEME_GLIF_LIGHT)) {
                    obj = 4;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                return R.style.SuwThemeGlifV3_Light;
            case 1:
                return R.style.SuwThemeGlifV3;
            case 2:
                return R.style.SuwThemeGlifV2_Light;
            case 3:
                return R.style.SuwThemeGlifV2;
            case 4:
                return R.style.SuwThemeGlif_Light;
            case 5:
                return R.style.SuwThemeGlif;
            case 6:
                return R.style.SuwThemeMaterial_Light;
            case 7:
                return R.style.SuwThemeMaterial;
            default:
                return i;
        }
    }

    public static boolean isDeferredSetupWizard(Intent intent) {
        return intent != null && intent.getBooleanExtra(EXTRA_IS_DEFERRED_SETUP, false);
    }

    public static boolean isDeviceProvisioned(Context context) {
        if (VERSION.SDK_INT >= 17) {
            if (Global.getInt(context.getContentResolver(), SETTINGS_GLOBAL_DEVICE_PROVISIONED, 0) != 1) {
                return false;
            }
        } else if (Secure.getInt(context.getContentResolver(), SETTINGS_GLOBAL_DEVICE_PROVISIONED, 0) != 1) {
            return false;
        }
        return true;
    }

    public static boolean isLightTheme(Intent intent, boolean z) {
        return isLightTheme(intent.getStringExtra(EXTRA_THEME), z);
    }

    public static boolean isLightTheme(String str, boolean z) {
        return (THEME_HOLO_LIGHT.equals(str) || THEME_MATERIAL_LIGHT.equals(str) || THEME_GLIF_LIGHT.equals(str) || THEME_GLIF_V2_LIGHT.equals(str) || THEME_GLIF_V3_LIGHT.equals(str)) ? true : (THEME_HOLO.equals(str) || THEME_MATERIAL.equals(str) || THEME_GLIF.equals(str) || THEME_GLIF_V2.equals(str) || THEME_GLIF_V3.equals(str)) ? false : z;
    }

    public static boolean isPreDeferredSetupWizard(Intent intent) {
        return intent != null && intent.getBooleanExtra(EXTRA_IS_PRE_DEFERRED_SETUP, false);
    }

    public static boolean isSetupWizardIntent(Intent intent) {
        return intent.getBooleanExtra(EXTRA_IS_FIRST_RUN, false);
    }

    public static boolean isUserSetupComplete(Context context) {
        if (VERSION.SDK_INT >= 17) {
            if (Secure.getInt(context.getContentResolver(), SETTINGS_SECURE_USER_SETUP_COMPLETE, 0) != 1) {
                return false;
            }
        } else if (Secure.getInt(context.getContentResolver(), SETTINGS_GLOBAL_DEVICE_PROVISIONED, 0) != 1) {
            return false;
        }
        return true;
    }
}
