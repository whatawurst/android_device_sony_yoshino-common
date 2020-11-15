package com.sonymobile.customizationselector;

import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

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
//import com.android.setupwizardlib.util.SystemBarHelper;

import java.lang.ref.WeakReference;

public class CustomizationSelectorSUWActivity extends Activity {

    private static final String TAG = CustomizationSelectorSUWActivity.class.getSimpleName();

    private static final long MAX_VIEW_TIME_MS = 120000;
    private static final int MSG_CONTINUE = 0;
    private static final int MSG_REBOOT = 1;

    private final BroadcastReceiver mSimReceiver = new BroadcastReceiver() {
        private static final String TAG = "CustomizationSelectorSUWActivity - SimReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null && intent != null && "android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                int simState = mTelephonyManager.getSimState();
                CSLog.d(TAG, "SimReceiver - sim state: " + simState);

                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String state = extras.getString("ss");
                    CSLog.d(TAG, "SimReceiver - state: " + state);

                    startTimeout();
                    if ("LOADED".equals(state)) {
                        CSLog.d(TAG, "Default Sim ready");
                        handleConfiguration();
                    } else if ("PERM_DISABLED".equals(state) || "ABSENT".equals(state) || "CARD_IO_ERROR".equals(state)) {
                        continueSetupWizard();
                    } else if ("LOCKED".equals(state)) {
                        CSLog.d(TAG, "Sim locked, removing timeout.");
                        StateHandler.getStateHandler(CustomizationSelectorSUWActivity.this).removeCallbacksAndMessages(null);
                    }
                }
            }
        }
    };
    private TelephonyManager mTelephonyManager;

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

        @Override
        public void handleMessage(Message message) {
            removeCallbacksAndMessages(null);
            CustomizationSelectorSUWActivity customizationSelectorSUWActivity = this.weakActivity.get();
            if (customizationSelectorSUWActivity != null) {
                switch (message.what) {
                    case MSG_CONTINUE:
                        customizationSelectorSUWActivity.continueSetupWizard();
                        return;
                    case MSG_REBOOT:
                        CSLog.d(CustomizationSelectorSUWActivity.TAG, "Configuration changed - rebooting device...");
                        Log.i(customizationSelectorSUWActivity.getString(R.string.app_name), customizationSelectorSUWActivity.getString(R.string.customization_restart_desc_txt));
                        ((PowerManager) customizationSelectorSUWActivity.getSystemService("power")).reboot(customizationSelectorSUWActivity.getApplicationContext().getString(R.string.reboot_reason));
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private void disableUI() {
        getWindow().addFlags(FLAG_DISMISS_KEYGUARD);
        ((StatusBarManager) getSystemService("statusbar")).disable(StatusBarManager.DISABLE_MASK);
    }

    private void resetUI() {
        getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);
        ((StatusBarManager) getSystemService("statusbar")).disable(StatusBarManager.DISABLE_NONE);
    }

    private void startTimeout() {
        int simState = mTelephonyManager.getSimState();
        if (simState != 2 && simState != 3) {
            CSLog.d(TAG, "Start timeout");
            StateHandler.getStateHandler(this).sendEmptyMessageDelayed(MSG_CONTINUE, MAX_VIEW_TIME_MS);
        }
    }

    public void continueSetupWizard() {
        CSLog.d(TAG, "Continue Setup Wizard.");
        StateHandler.getStateHandler(this).removeCallbacksAndMessages(null);
        resetUI();
        setResult(-1);
        finish();
    }

    public void handleConfiguration() {
        int msg;
        boolean rebootRequired = false;

        StateHandler.getStateHandler(this).removeCallbacksAndMessages(null);
        Configurator configurator = new Configurator(getApplicationContext(), CommonUtil.getCarrierBundle(this));
        if (configurator.isNewConfigurationNeeded()) {
            configurator.set();
            msg = MSG_REBOOT;
        } else {
            msg = MSG_CONTINUE;
        }

        configurator.saveConfigurationKey();
        if (msg == MSG_REBOOT) {
            rebootRequired = true;
        }
        CSLog.d(TAG, "handleConfiguration - reboot? " + rebootRequired);
        StateHandler.getStateHandler(this).sendEmptyMessage(msg);
    }

    public boolean isSimWorking() {
        int simState = mTelephonyManager.getSimState();
        CSLog.d(TAG, "isSimWorking - sim state: " + simState);
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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        disableUI();
        mTelephonyManager = getSystemService(TelephonyManager.class);
        if (CommonUtil.isDualSim(this) || !isSimWorking()) {
            continueSetupWizard();
            return;
        }
        registerReceiver(mSimReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mSimReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSimWorking()) {
            startTimeout();
        } else {
            continueSetupWizard();
        }
    }
}
