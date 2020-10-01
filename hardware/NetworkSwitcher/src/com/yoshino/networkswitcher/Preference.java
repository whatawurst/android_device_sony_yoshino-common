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

package com.yoshino.networkswitcher;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple class for handling network pref
 *
 * @author shank03
 */
public class Preference {

    private static final String WAS_NETWORK_3G = "network_pref_3g";
    private static final String ENHANCED_4G_VOLTE_ENABLED = "enhanced_4g_volte_enable";
    private static final String PREFERENCE_STORED = "preference_stored";

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences("NetworkSwitcher", Context.MODE_PRIVATE);
    }

    /**
     * This preference stores if the user-set network mode preference was 3G or not
     */
    public static void putWasNetwork3G(boolean was, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(WAS_NETWORK_3G, was);
        editor.apply();

        putPreferenceStored(context);
    }
    public static boolean getWasNetwork3G(Context context, boolean def) {
        return getPreferences(context).getBoolean(WAS_NETWORK_3G, def);
    }

    /**
     * This preference stores if VoLTE or 4G Calling preference was enabled
     */
    public static void putEnhanced4GEnabled(boolean was, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(ENHANCED_4G_VOLTE_ENABLED, was);
        editor.apply();

        putPreferenceStored(context);
    }
    public static boolean getEnhanced4GEnabled(Context context, boolean def) {
        return getPreferences(context).getBoolean(ENHANCED_4G_VOLTE_ENABLED, def);
    }

    /**
     * This preference stores if the rest of the preferences are empty or not
     */
    private static void putPreferenceStored(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(PREFERENCE_STORED, true);
        editor.apply();
    }
    public static boolean getPreferenceStored(Context context) {
        return getPreferences(context).getBoolean(PREFERENCE_STORED, false);
    }
}
