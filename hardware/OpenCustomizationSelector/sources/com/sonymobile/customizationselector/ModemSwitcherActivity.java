package com.sonymobile.customizationselector;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class ModemSwitcherActivity extends Activity {
    private static final int COMPONENT_ENABLE_DISABLE_TIMEOFF = 8000;
    private static final String INITIAL_MODEM_PREF = "initialModem";
    public static final String PREFERENCE_ENABLE_MODEM_SWAP_ON_SIM = "enableModemSwapOnSIM";
    private static final String TAG = "ModemSwitcher_Debug";
    private String mInitialModem;
    private ListView mModemListView;
    private ModemSwitcher mModemSwitcher;
    private SharedPreferences mPreference;

    private void applyModem(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("selected modem is ");
        stringBuilder.append(str);
        CSLog.d(TAG, stringBuilder.toString());
        ModemSwitcher modemSwitcher = this.mModemSwitcher;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(ModemSwitcher.MODEM_FS_PATH);
        stringBuilder2.append(str);
        if (modemSwitcher.setModemConfiguration(stringBuilder2.toString())) {
            ((PowerManager) getSystemService("power")).reboot(getApplicationContext().getString(2131427339));
        }
    }

    private void saveInitialModem(String str) {
        this.mInitialModem = this.mPreference.getString(INITIAL_MODEM_PREF, "");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Save initial modem");
        stringBuilder.append(this.mInitialModem);
        CSLog.d(TAG, stringBuilder.toString());
        if (this.mInitialModem.equals("") && str != null && !str.equals("")) {
            CSLog.d(TAG, "Save initial modem in preference");
            this.mInitialModem = str;
            Editor edit = this.mPreference.edit();
            edit.putString(INITIAL_MODEM_PREF, str);
            edit.commit();
        }
    }

    private void verifyPick(final String str) {
        Builder builder = new Builder(this);
        builder.setTitle(2131427337).setMessage(getResources().getString(2131427336, new Object[]{str}));
        builder.setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ModemSwitcherActivity.this.applyModem(str);
            }
        });
        builder.setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        int i = 0;
        super.onCreate(bundle);
        if (CSLog.DEBUG) {
            this.mPreference = PreferenceManager.getDefaultSharedPreferences(CommonUtil.isDirectBootEnabled() ? createDeviceProtectedStorageContext() : this);
            setRequestedOrientation(1);
            this.mModemSwitcher = new ModemSwitcher();
            try {
                int i2;
                String replace = this.mModemSwitcher.getCurrentModemConfig().replace(ModemSwitcher.MODEM_FS_PATH, "");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("current modem");
                stringBuilder.append(replace);
                CSLog.d(TAG, stringBuilder.toString());
                String[] availableModemConfigurations = this.mModemSwitcher.getAvailableModemConfigurations();
                for (String str : availableModemConfigurations) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("modems");
                    stringBuilder2.append(str);
                    CSLog.d(TAG, stringBuilder2.toString());
                }
                if (replace.equals(ModemSwitcher.SINGLE_MODEM_FS) || replace.equals("")) {
                    CSLog.d(TAG, "Single modem device");
                    i2 = 1;
                } else {
                    saveInitialModem(replace);
                    i2 = 0;
                }
                if (i2 != 0) {
                    setContentView(2131361793);
                    return;
                }
                setContentView(2131361792);
                ((TextView) findViewById(2131230727)).setText(this.mInitialModem);
                ((TextView) findViewById(2131230723)).setText(replace);
                Button button = (Button) findViewById(2131230732);
                button.setClickable(true);
                ArrayList arrayList = new ArrayList();
                int length = availableModemConfigurations.length;
                while (i < length) {
                    arrayList.add(availableModemConfigurations[i].replace(ModemSwitcher.MODEM_FS_PATH, ""));
                    i++;
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367043, arrayList);
                this.mModemListView = (ListView) findViewById(2131230729);
                this.mModemListView.setAdapter(arrayAdapter);
                this.mModemListView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        Object itemAtPosition = ModemSwitcherActivity.this.mModemListView.getItemAtPosition(i);
                        if (itemAtPosition != null) {
                            ModemSwitcherActivity.this.verifyPick(itemAtPosition.toString());
                        }
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (ModemSwitcherActivity.this.mInitialModem != null) {
                            ModemSwitcherActivity.this.verifyPick(ModemSwitcherActivity.this.mInitialModem);
                        }
                    }
                });
                return;
            } catch (Exception e) {
                CSLog.d(TAG, "Not possible to read the modem files");
                finish();
                return;
            }
        }
        CSLog.d(TAG, "Activity not shown for user devices");
        finish();
    }
}
