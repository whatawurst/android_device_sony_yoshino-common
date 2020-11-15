package com.sonymobile.customizationselector;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ModemSwitcherActivity extends Activity {

    private static final String TAG = "ModemSwitcher_Debug";

    private static final String INITIAL_MODEM_PREF = "initialModem";

    private String mInitialModem;
    private ListView mModemListView;
    private ModemSwitcher mModemSwitcher;
    private SharedPreferences mPreference;

    private void applyModem(String str) {
        CSLog.d(TAG, "selected modem is " + str);

        if (mModemSwitcher.setModemConfiguration(ModemSwitcher.MODEM_FS_PATH + str)) {
            ((PowerManager) getSystemService("power")).reboot(getApplicationContext().getString(R.string.reboot_reason_modem_debug));
        }
    }

    private void saveInitialModem(String str) {
        mInitialModem = mPreference.getString(INITIAL_MODEM_PREF, "");
        CSLog.d(TAG, "Save initial modem" + mInitialModem);

        if (mInitialModem.equals("") && str != null && !str.equals("")) {
            CSLog.d(TAG, "Save initial modem in preference");
            mInitialModem = str;
            mPreference.edit().putString(INITIAL_MODEM_PREF, str).apply();
        }
    }

    private void verifyPick(final String str) {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.debug_verify_title).setMessage(getResources().getString(R.string.debug_verify_text, str));
        builder.setPositiveButton("OK", (dialogInterface, i) -> applyModem(str));
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mPreference = (CommonUtil.isDirectBootEnabled() ? createDeviceProtectedStorageContext() : this)
                .getSharedPreferences(Configurator.PREF_PKG, Context.MODE_PRIVATE);

        setRequestedOrientation(1);
        mModemSwitcher = new ModemSwitcher();
        try {
            int i2;
            String currentModem = mModemSwitcher.getCurrentModemConfig().replace(ModemSwitcher.MODEM_FS_PATH, "");
            CSLog.d(TAG, "current modem" + currentModem);

            String[] availableModemConfigurations = mModemSwitcher.getAvailableModemConfigurations();
            for (String str : availableModemConfigurations) {
                CSLog.d(TAG, "modems: " + str);
            }
            if (currentModem.equals(ModemSwitcher.SINGLE_MODEM_FS) || currentModem.equals("")) {
                CSLog.d(TAG, "Single modem device");
                i2 = 1;
            } else {
                saveInitialModem(currentModem);
                i2 = 0;
            }
            if (i2 != 0) {
                setContentView(R.layout.modem_handling_single);
                return;
            }
            setContentView(R.layout.modem_handling);

            ((TextView) findViewById(R.id.initial_modem)).setText(mInitialModem);
            ((TextView) findViewById(R.id.current_modem)).setText(currentModem);

            Button button = findViewById(R.id.restore_button);
            button.setClickable(true);

            ArrayList<String> modemList = new ArrayList<>();
            for (String m : availableModemConfigurations) {
                modemList.add(m.replace(ModemSwitcher.MODEM_FS_PATH, ""));
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modemList);
            mModemListView = findViewById(R.id.modem_list);
            mModemListView.setAdapter(arrayAdapter);
            mModemListView.setOnItemClickListener((adapterView, view, i, j) -> {
                Object itemAtPosition = mModemListView.getItemAtPosition(i);
                if (itemAtPosition != null) {
                    verifyPick(itemAtPosition.toString());
                }
            });
            button.setOnClickListener(view -> {
                if (mInitialModem != null) {
                    verifyPick(mInitialModem);
                }
            });
        } catch (Exception e) {
            CSLog.e(TAG, "Not possible to read the modem files", e);
            finish();
        }
    }
}
