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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

public class CustomizationSelectorActivity extends Activity implements OnClickListener {

    private static final String TAG = CustomizationSelectorActivity.class.getSimpleName();

    private AlertDialog mAlertDialog;
    private Configurator mConfigurator;
    private UserPresentReceiver mUserPresentReceiver;

    private void disableActivity() {
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, CustomizationSelectorActivity.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private void startDialog() {
        if (mAlertDialog == null) {
            Builder builder = new Builder(this, R.style.ThemeLightDialog);
            builder.setCancelable(false);
            builder.setMessage(R.string.customization_restart_desc_txt);
            builder.setPositiveButton("OK", this);
            mAlertDialog = builder.create();
            mAlertDialog.setCanceledOnTouchOutside(false);
            // TODO: Unknown constant(s)
            mAlertDialog.getWindow().setType(2009);
        }
        if (!mAlertDialog.isShowing()) {
            CSLog.d(TAG, "Show dialog");
            mAlertDialog.show();
        }
    }

    // TODO: Unknown constants
    public void disableUI() {
        getWindow().addFlags(4194304);
        (getSystemService(StatusBarManager.class)).disable(67043328);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        CSLog.d(TAG, "onClick - Reboot");
        disableActivity();
        mConfigurator.set();
        mConfigurator.saveConfigurationKey();
        Log.i(getString(R.string.app_name), getString(R.string.customization_restart_desc_txt));
        (getSystemService(PowerManager.class)).reboot(getString(R.string.reboot_reason));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        CSLog.d(TAG, "onCreate()");
        mConfigurator = new Configurator(this, CommonUtil.getCarrierBundle(this));

        if (CommonUtil.isSIMLoaded(this, CommonUtil.getDefaultSubId(this)) && mConfigurator.isNewConfigurationNeeded()) {
            disableUI();
            setFinishOnTouchOutside(false);
            setupUserPresent();
            startDialog();
            return;
        }

        disableActivity();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        // TODO: Unknown constant(s)
        intent.setFlags(268435456);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        CSLog.d(TAG, "onDestroy()");
        if (mUserPresentReceiver != null) {
            unregisterReceiver(mUserPresentReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startDialog();
    }

    private void setupUserPresent() {
        mUserPresentReceiver = new UserPresentReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mUserPresentReceiver, intentFilter);
    }
}
