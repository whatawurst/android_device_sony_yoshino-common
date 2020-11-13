package com.sonymobile.customizationselector;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.setupwizardlib.util.SystemBarHelper;
import java.lang.ref.WeakReference;

public class CustomizationSelectorSUWActivity extends Activity {
    private static final long MAX_VIEW_TIME_MS = 120000;
    private static final int MSG_CONTINUE = 0;
    private static final int MSG_REBOOT = 1;
    private static final String TAG = CustomizationSelectorSUWActivity.class.getSimpleName();
    private BroadcastReceiver mSimReceiver;
    private TelephonyManager mTelephonyManager;

    private final class SimReceiver extends BroadcastReceiver {
        private SimReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (context != null && intent != null && "android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                int simState = CustomizationSelectorSUWActivity.this.mTelephonyManager.getSimState();
                String access$100 = CustomizationSelectorSUWActivity.TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SimReceiver - sim state: ");
                stringBuilder.append(simState);
                CSLog.d(access$100, stringBuilder.toString());
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String string = extras.getString("ss");
                    access$100 = CustomizationSelectorSUWActivity.TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("SimReceiver - state: ");
                    stringBuilder.append(string);
                    CSLog.d(access$100, stringBuilder.toString());
                    CustomizationSelectorSUWActivity.this.startTimeout();
                    if ("LOADED".equals(string)) {
                        CSLog.d(CustomizationSelectorSUWActivity.TAG, "Default Sim ready");
                        CustomizationSelectorSUWActivity.this.handleConfiguration();
                    } else if ("PERM_DISABLED".equals(string) || "ABSENT".equals(string) || "CARD_IO_ERROR".equals(string)) {
                        CustomizationSelectorSUWActivity.this.continueSetupWizard();
                    } else if ("LOCKED".equals(string)) {
                        CSLog.d(CustomizationSelectorSUWActivity.TAG, "Sim locked, removing timeout.");
                        StateHandler.getStateHandler(CustomizationSelectorSUWActivity.this).removeCallbacksAndMessages(null);
                    }
                }
            }
        }
    }

    private static final class StateHandler extends Handler {
        private static StateHandler sHandler;
        private WeakReference<CustomizationSelectorSUWActivity> weakActivity;

        private StateHandler(CustomizationSelectorSUWActivity customizationSelectorSUWActivity) {
            this.weakActivity = new WeakReference(customizationSelectorSUWActivity);
        }

        public static StateHandler getStateHandler(CustomizationSelectorSUWActivity customizationSelectorSUWActivity) {
            if (sHandler == null || sHandler.weakActivity.get() == null) {
                sHandler = new StateHandler(customizationSelectorSUWActivity);
            }
            return sHandler;
        }

        public void handleMessage(Message message) {
            removeCallbacksAndMessages(null);
            CustomizationSelectorSUWActivity customizationSelectorSUWActivity = (CustomizationSelectorSUWActivity) this.weakActivity.get();
            if (customizationSelectorSUWActivity != null) {
                switch (message.what) {
                    case 0:
                        customizationSelectorSUWActivity.continueSetupWizard();
                        return;
                    case 1:
                        CSLog.d(CustomizationSelectorSUWActivity.TAG, "Configuration changed - rebooting device...");
                        Log.i(customizationSelectorSUWActivity.getString(2131427328), customizationSelectorSUWActivity.getString(2131427329));
                        ((PowerManager) customizationSelectorSUWActivity.getSystemService("power")).reboot(customizationSelectorSUWActivity.getApplicationContext().getString(2131427338));
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private void disableUI() {
        getWindow().addFlags(4194304);
        ((StatusBarManager) getSystemService("statusbar")).disable(67043328);
    }

    private void resetUI() {
        getWindow().addFlags(524288);
        ((StatusBarManager) getSystemService("statusbar")).disable(0);
    }

    private void startTimeout() {
        int simState = this.mTelephonyManager.getSimState();
        if (simState != 2 && simState != 3) {
            CSLog.d(TAG, "Start timeout");
            StateHandler.getStateHandler(this).sendEmptyMessageDelayed(0, MAX_VIEW_TIME_MS);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void continueSetupWizard() {
        CSLog.d(TAG, "Continue Setup Wizard.");
        StateHandler.getStateHandler(this).removeCallbacksAndMessages(null);
        resetUI();
        setResult(-1);
        finish();
    }

    /* Access modifiers changed, original: 0000 */
    public void handleConfiguration() {
        int i;
        boolean z = false;
        StateHandler.getStateHandler(this).removeCallbacksAndMessages(null);
        Configurator configurator = new Configurator(getApplicationContext(), CommonUtil.getCarrierBundle(this));
        if (configurator.isNewConfigurationNeeded()) {
            configurator.set();
            i = 1;
        } else {
            i = 0;
        }
        configurator.saveConfigurationKey();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("handleConfiguration - reboot? ");
        if (i == 1) {
            z = true;
        }
        stringBuilder.append(z);
        CSLog.d(str, stringBuilder.toString());
        StateHandler.getStateHandler(this).sendEmptyMessage(i);
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isSimWorking() {
        int simState = this.mTelephonyManager.getSimState();
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("isSimWorking - sim state: ");
        stringBuilder.append(simState);
        CSLog.d(str, stringBuilder.toString());
        if (simState != 1) {
            switch (simState) {
                case 7:
                case 8:
                    break;
                default:
                    return true;
            }
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(2131361794);
        disableUI();
        SystemBarHelper.hideSystemBars(getWindow());
        this.mTelephonyManager = (TelephonyManager) getSystemService("phone");
        if (CommonUtil.isDualSim(this) || !isSimWorking()) {
            continueSetupWizard();
            return;
        }
        this.mSimReceiver = new SimReceiver();
        registerReceiver(this.mSimReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
    }

    /* Access modifiers changed, original: protected */
    public void onDestroy() {
        if (this.mSimReceiver != null) {
            unregisterReceiver(this.mSimReceiver);
        }
        super.onDestroy();
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        if (isSimWorking()) {
            startTimeout();
        } else {
            continueSetupWizard();
        }
    }
}
