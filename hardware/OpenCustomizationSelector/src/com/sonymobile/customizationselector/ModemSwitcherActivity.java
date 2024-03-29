package com.sonymobile.customizationselector;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.Locale;

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
            ((PowerManager) getSystemService(Context.POWER_SERVICE)).reboot(getApplicationContext().getString(R.string.reboot_reason_modem_debug));
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
        Builder builder = new Builder(this, R.style.AppDialog);
        builder.setTitle(R.string.debug_verify_title).setMessage(getResources().getString(R.string.debug_verify_text, str));
        builder.setPositiveButton("OK", (dialogInterface, i) -> applyModem(str));
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mPreference = (CommonUtil.isDirectBootEnabled() ? createDeviceProtectedStorageContext() : this)
                .getSharedPreferences(Configurator.PREF_PKG, Context.MODE_PRIVATE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mModemSwitcher = new ModemSwitcher();
        try {
            int i2;
            String currentModem = ModemSwitcher.getCurrentModemConfig().replace(ModemSwitcher.MODEM_FS_PATH, "");
            CSLog.d(TAG, "current modem" + currentModem);

            String[] availableModemConfigurations = mModemSwitcher.getAvailableModemConfigurations();
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
            AppCompatEditText editText = findViewById(R.id.search_modem);

            Button button = findViewById(R.id.restore_button);
            button.setClickable(true);

            ArrayList<String> modemList = new ArrayList<>();
            for (String m : availableModemConfigurations) {
                modemList.add(m.replace(ModemSwitcher.MODEM_FS_PATH, ""));
            }

            mModemListView = findViewById(R.id.modem_list);
            setupListAdapter(modemList);
            button.setOnClickListener(view -> {
                if (mInitialModem != null) {
                    verifyPick(mInitialModem);
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ArrayList<String> sModems = new ArrayList<>();
                    for (String modemName : modemList) {
                        if (modemName.toLowerCase(Locale.getDefault())
                                .contains(charSequence.toString().toLowerCase(Locale.getDefault()))) {
                            sModems.add(modemName);
                        }
                    }

                    setupListAdapter(sModems);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        } catch (Exception e) {
            CSLog.e(TAG, "Not possible to read the modem files", e);
            finish();
        }
    }

    private synchronized void setupListAdapter(ArrayList<String> modemList) {
        mModemListView.setAdapter(null);
        mModemListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modemList));
        mModemListView.setOnItemClickListener((adapterView, view, i, j) -> {
            Object itemAtPosition = mModemListView.getItemAtPosition(i);
            if (itemAtPosition != null) {
                verifyPick(itemAtPosition.toString());
            }
        });
    }
}
