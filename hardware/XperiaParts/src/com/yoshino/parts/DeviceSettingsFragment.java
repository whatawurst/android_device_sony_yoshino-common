/*
 * Copyright (c) 2020, Shashank Verma (shank03) <shashank.verma2002@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.yoshino.parts;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import static com.yoshino.parts.Constants.*;

public class DeviceSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.device_settings);

        SwitchPreference glovePref = findPreference(GLOVE_MODE);
        assert glovePref != null;
        glovePref.setChecked(Settings.System.getInt(glovePref.getContext().getContentResolver(), GLOVE_MODE, 0) == 1);
        glovePref.setOnPreferenceChangeListener(this);

        SwitchPreference smartStaminPref = findPreference(SMART_STAMINA_MODE);
        assert smartStaminPref != null;
        smartStaminPref.setChecked(Settings.System.getInt(smartStaminPref.getContext().getContentResolver(), SMART_STAMINA_MODE, 0) == 1);
        smartStaminPref.setOnPreferenceChangeListener(this);

        SwitchPreference notificationPref = findPreference(CS_NOTIFICATION);
        assert notificationPref != null;
        notificationPref.setChecked(Settings.System.getInt(notificationPref.getContext().getContentResolver(),
                CS_NOTIFICATION, 1) == 1);
        notificationPref.setOnPreferenceChangeListener(this);

        Preference statusPref = findPreference(CS_STATUS);
        assert statusPref != null;
        statusPref.setOnPreferenceClickListener(preference -> {
            preference.getContext().startActivity(new Intent()
                    .setClassName("com.sonymobile.customizationselector", "com.sonymobile.customizationselector.StatusActivity"));
            return true;
        });

        Preference logPref = findPreference(CS_LOG);
        assert logPref != null;
        logPref.setOnPreferenceClickListener(preference -> {
            preference.getContext().startActivity(new Intent()
                    .setClassName("com.sonymobile.customizationselector", "com.sonymobile.customizationselector.LogActivity"));
            return true;
        });

        Preference msActPref = findPreference(MODEM_SWITCHER);
        assert msActPref != null;
        msActPref.setOnPreferenceClickListener(preference -> {
            preference.getContext().startActivity(new Intent()
                    .setClassName("com.sonymobile.customizationselector", "com.sonymobile.customizationselector.ModemSwitcherActivity"));
            return true;
        });

        Preference slotPref = findPreference(NS_SLOT);
        SwitchPreference nsService = findPreference(NS_SERVICE);
        if (slotPref != null && nsService != null) {
            if (getContext().getSystemService(TelephonyManager.class).getActiveModemCount() > 1) {
                slotPref.setVisible(true);

                int slot = Settings.System.getInt(nsService.getContext().getContentResolver(), NS_SLOT, -1);
                slotPref.setSummary(slotPref.getContext().getString(R.string.sim_slot_summary) + (slot == -1 ? " INVALID" : " " + (slot + 1)));

                slotPref.setOnPreferenceClickListener(preference -> {
                    int nSlot = Settings.System.getInt(nsService.getContext().getContentResolver(), NS_SLOT, -1);
                    switch (nSlot) {
                        case 0:
                            Settings.System.putInt(nsService.getContext().getContentResolver(), NS_SLOT, 1);
                            slotPref.setSummary(slotPref.getContext().getString(R.string.sim_slot_summary) + " 2");
                            nsService.setEnabled(true);
                            break;
                        case 1:
                            Settings.System.putInt(nsService.getContext().getContentResolver(), NS_SLOT, -1);
                            slotPref.setSummary(slotPref.getContext().getString(R.string.sim_slot_summary) + " INVALID");
                            nsService.setEnabled(false);
                            break;
                        case -1:
                            Settings.System.putInt(nsService.getContext().getContentResolver(), NS_SLOT, 0);
                            slotPref.setSummary(slotPref.getContext().getString(R.string.sim_slot_summary) + " 1");
                            nsService.setEnabled(true);
                            break;
                    }
                    return true;
                });

                nsService.setEnabled(slot != -1);
            } else {
                slotPref.setVisible(false);
                nsService.setEnabled(true);
            }
            nsService.setChecked(Settings.System.getInt(nsService.getContext().getContentResolver(), NS_SERVICE, 0) == 1);
            nsService.setOnPreferenceChangeListener(this);
        }

        SwitchPreference imsPref = findPreference(CS_IMS);
        assert imsPref != null;
        if (Settings.System.getInt(imsPref.getContext().getContentResolver(), CS_IMS, 1) == 0) {
            imsPref.setChecked(false);
            notificationPref.setEnabled(false);
            msActPref.setEnabled(false);
        } else {
            imsPref.setChecked(true);
            notificationPref.setEnabled(true);
            msActPref.setEnabled(true);
        }
        imsPref.setOnPreferenceClickListener(preference -> {
            int ims = Settings.System.getInt(imsPref.getContext().getContentResolver(), CS_IMS, 1);
            if (ims == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imsPref.getContext());
                builder.setCancelable(false);
                builder.setTitle("Disable IMS features ?");
                builder.setMessage("You will lose all the carrier specific features such as VoLTE, VoWiFi and " +
                        "WiFi Calling; and Your device will switch to default modem config with basic mobile data feature.");
                builder.setPositiveButton("Disable", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Settings.System.putInt(imsPref.getContext().getContentResolver(), CS_IMS, 0);
                    imsPref.setChecked(false);
                    notificationPref.setEnabled(false);
                    msActPref.setEnabled(false);

                    sendBroadcast(preference.getContext(), 0);
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    imsPref.setChecked(true);
                });
                builder.create().show();
            }
            if (ims == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imsPref.getContext());
                builder.setCancelable(false);
                builder.setTitle("Enable IMS features ?");
                builder.setMessage("This will allow you to use the provided carrier specific features such as VoLTE, " +
                        "VoWiFi and WiFi Calling; only if it worked on stock.\n\nYour device will prompt reboot if it " +
                        "found carrier specific modem.");
                builder.setPositiveButton("Enable", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Settings.System.putInt(imsPref.getContext().getContentResolver(), CS_IMS, 1);
                    imsPref.setChecked(true);
                    notificationPref.setEnabled(true);
                    msActPref.setEnabled(true);

                    sendBroadcast(preference.getContext(), 0);
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    imsPref.setChecked(false);
                });
                builder.create().show();
            }
            return true;
        });

        SwitchPreference modemPref = findPreference(CS_RE_APPLY_MODEM);
        assert modemPref != null;
        modemPref.setChecked(Settings.System.getInt(modemPref.getContext().getContentResolver(), CS_RE_APPLY_MODEM, 0) == 1);
        modemPref.setOnPreferenceClickListener(preference -> {
            int applyModem = Settings.System.getInt(modemPref.getContext().getContentResolver(), CS_RE_APPLY_MODEM, 0);

            AlertDialog.Builder builder = new AlertDialog.Builder(modemPref.getContext());
            builder.setCancelable(false);
            builder.setTitle("Reboot required");
            builder.setMessage("A reboot is required to " + (applyModem == 0 ? "enable" : "disable") + " this feature. Are you sure you want to reboot ?");
            builder.setPositiveButton("Reboot", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                Settings.System.putInt(modemPref.getContext().getContentResolver(), CS_RE_APPLY_MODEM, (applyModem ^ 1));
                modemPref.setChecked(applyModem == 0);

                sendBroadcast(preference.getContext(), 1);
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                modemPref.setChecked(applyModem == 1);
            });
            builder.create().show();
            return true;
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case GLOVE_MODE:
                Settings.System.putInt(preference.getContext().getContentResolver(), GLOVE_MODE, (boolean) o ? 1 : 0);
                SystemProperties.set(GLOVE_PROP, (boolean) o ? "1" : "0");
                return true;
            case SMART_STAMINA_MODE:
                Settings.System.putInt(preference.getContext().getContentResolver(), SMART_STAMINA_MODE, (boolean) o ? 1 : 0);
                SystemProperties.set(SMART_STAMINA_PROP, (boolean) o ? "1" : "0");
                return true;
            case CS_NOTIFICATION:
                Settings.System.putInt(preference.getContext().getContentResolver(), CS_NOTIFICATION, (boolean) o ? 1 : 0);
                return true;
            case NS_SERVICE:
                Settings.System.putInt(preference.getContext().getContentResolver(), NS_SERVICE, (boolean) o ? 1 : 0);
                return true;
        }
        return false;
    }

    private void sendBroadcast(Context context, int pref) {
        Intent broadcast = new Intent();
        broadcast.putExtra("pref", pref);
        if (pref == 0) broadcast.putExtra(CS_IMS, Settings.System.getInt(context.getContentResolver(), CS_IMS, 1));
        if (pref == 1)
            broadcast.putExtra(CS_RE_APPLY_MODEM, Settings.System.getInt(context.getContentResolver(), CS_RE_APPLY_MODEM, 0));
        broadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES).setComponent(new ComponentName("com.sonymobile.customizationselector",
                "com.sonymobile.customizationselector.PreferenceReceiver"));
        context.sendBroadcast(broadcast);
    }
}
