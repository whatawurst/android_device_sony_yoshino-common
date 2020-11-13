package com.sonymobile.customizationselector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

public class CustomizationSelectorActivity extends Activity implements OnClickListener {
    private static final String TAG = CustomizationSelectorActivity.class.getSimpleName();
    private AlertDialog mAlertDialog;
    private Configurator mConfigurator;
    private UserPresentReceiver mUserPresentReceiver;

    private void disableActivity() {
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, CustomizationSelectorActivity.class), 2, 1);
    }

    private void startDialog() {
        if (this.mAlertDialog == null) {
            Builder builder = new Builder(this, 2131492923);
            builder.setCancelable(false);
            builder.setMessage(2131427329);
            builder.setPositiveButton(17039370, this);
            this.mAlertDialog = builder.create();
            this.mAlertDialog.setCanceledOnTouchOutside(false);
            this.mAlertDialog.getWindow().setType(2009);
        }
        if (!this.mAlertDialog.isShowing()) {
            CSLog.d(TAG, "Show dialog");
            this.mAlertDialog.show();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void disableUI() {
        getWindow().addFlags(4194304);
        ((StatusBarManager) getSystemService("statusbar")).disable(67043328);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        CSLog.d(TAG, "onClick - Reboot");
        disableActivity();
        this.mConfigurator.set();
        this.mConfigurator.saveConfigurationKey();
        Log.i(getString(2131427328), getString(2131427329));
        ((PowerManager) getSystemService("power")).reboot(getString(2131427338));
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        CSLog.d(TAG, "onCreate()");
        this.mConfigurator = new Configurator(this, CommonUtil.getCarrierBundle(this));
        if (CommonUtil.isSIMLoaded(this, CommonUtil.getDefaultSubId(this)) && this.mConfigurator.isNewConfigurationNeeded()) {
            disableUI();
            setFinishOnTouchOutside(false);
            setupUserPresent();
            startDialog();
            return;
        }
        disableActivity();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        startActivity(intent);
        finish();
    }

    /* Access modifiers changed, original: protected */
    public void onDestroy() {
        CSLog.d(TAG, "onDestroy()");
        if (this.mUserPresentReceiver != null) {
            unregisterReceiver(this.mUserPresentReceiver);
        }
        super.onDestroy();
    }

    /* Access modifiers changed, original: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startDialog();
    }

    /* Access modifiers changed, original: 0000 */
    public void setupUserPresent() {
        this.mUserPresentReceiver = new UserPresentReceiver();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(this.mUserPresentReceiver, intentFilter);
    }
}
