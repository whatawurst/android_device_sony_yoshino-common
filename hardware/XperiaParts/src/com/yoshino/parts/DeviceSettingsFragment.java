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

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import static com.yoshino.parts.Constants.*;

public class DeviceSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.device_settings);

        SwitchPreference glovePref = findPreference(GLOVE_MODE);
        if (glovePref != null) {
            glovePref.setChecked(Settings.System.getInt(glovePref.getContext().getContentResolver(), GLOVE_MODE, 0) == 1);
            glovePref.setOnPreferenceChangeListener(this);
        }

        SwitchPreference notificationPref = findPreference(CS_NOTIFICATION);
        if (notificationPref != null) {
            notificationPref.setChecked(Settings.System.getInt(notificationPref.getContext().getContentResolver(),
                    CS_NOTIFICATION, 1) == 1);
            notificationPref.setOnPreferenceChangeListener(this);
        }

        Preference statusPref = findPreference(CS_STATUS);
        if (statusPref != null) {
            statusPref.setOnPreferenceClickListener(preference -> {
                preference.getContext().startActivity(new Intent()
                        .setClassName("com.sonymobile.customizationselector", "com.sonymobile.customizationselector.StatusActivity"));
                return true;
            });
        }

        Preference logPref = findPreference(CS_LOG);
        if (logPref != null) {
            logPref.setOnPreferenceClickListener(preference -> {
                preference.getContext().startActivity(new Intent()
                        .setClassName("com.sonymobile.customizationselector", "com.sonymobile.customizationselector.LogActivity"));
                return true;
            });
        }

        Preference msActPref = findPreference(MODEM_SWITCHER);
        if (msActPref != null) {
            msActPref.setOnPreferenceClickListener(preference -> {
                preference.getContext().startActivity(new Intent()
                        .setClassName("com.sonymobile.customizationselector", "com.sonymobile.customizationselector.ModemSwitcherActivity"));
                return true;
            });
        }

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

                nsService.setEnabled(false);
            } else {
                slotPref.setVisible(false);
                nsService.setEnabled(true);
            }
            nsService.setChecked(Settings.System.getInt(nsService.getContext().getContentResolver(), NS_SERVICE, 0) == 1);
            nsService.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case GLOVE_MODE:
                Settings.System.putInt(preference.getContext().getContentResolver(), GLOVE_MODE, (boolean) o ? 1 : 0);
                SystemProperties.set(GLOVE_PROP, (boolean) o ? "1" : "0");
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
}
