package com.sonymobile.customizationselector;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.provider.Settings.Secure;
import com.android.setupwizardlib.util.WizardManagerHelper;

public class CustomizationSelectorService extends IntentService {
    static final String EVALUATE_ACTION = "evaluate_action";
    private static final String TAG = CustomizationSelectorService.class.getSimpleName();

    public CustomizationSelectorService() {
        super(CustomizationSelectorService.class.getName());
    }

    static void evaluateCarrierBundle(Context context) {
        synchronized (CustomizationSelectorService.class) {
            try {
                if (CSLog.DEBUG) {
                    CSLog.logVersion(context, TAG);
                    CSLog.logSimValues(context, TAG);
                }
                if (!CommonUtil.isDirectBootEnabled()) {
                    UserManager userManager = (UserManager) context.getSystemService("user");
                    if (!(userManager == null || userManager.isUserUnlocked())) {
                        CSLog.d(TAG, "user is locked. private app data storage is not available.");
                        return;
                    }
                }
                Configurator configurator = new Configurator(context, CommonUtil.getCarrierBundle(context));
                if (configurator.isNewConfigurationNeeded()) {
                    context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, CustomizationSelectorActivity.class), 1, 1);
                    if (isUserSetupComplete(context)) {
                        CSLog.d(TAG, "isNewConfigurationNeeded - Need to reboot, starting dialog.");
                        context.startActivity(new Intent("android.intent.action.MAIN", null).addCategory("android.intent.category.HOME").addFlags(270565376));
                    } else {
                        CSLog.d(TAG, "isNewConfigurationNeeded - Need to reboot, user setup not complete");
                    }
                } else {
                    configurator.saveConfigurationKey();
                    CSLog.d(TAG, "isNewConfigurationNeeded - No new configuration.");
                }
            } finally {
                Class cls = CustomizationSelectorService.class;
            }
        }
    }

    private static boolean isUserSetupComplete(Context context) {
        return Secure.getInt(context.getContentResolver(), WizardManagerHelper.SETTINGS_SECURE_USER_SETUP_COMPLETE, 0) != 0;
    }

    /* Access modifiers changed, original: protected */
    public void onHandleIntent(Intent intent) {
        if (EVALUATE_ACTION.equals(intent != null ? intent.getAction() : "")) {
            evaluateCarrierBundle(this);
        }
    }
}
