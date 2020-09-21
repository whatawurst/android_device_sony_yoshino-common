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

    public static final String WAS_NETWORK_3G = "network_pref_3g";
    public static final String PREFERRED_3G = "pref_3g";
    public static final String DEFAULT_3G = "is_def_3g_stored";

    private static SharedPreferences getPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences("NetworkCycler", Context.MODE_PRIVATE);
    }

    /**
     * This preference stores if the user-set network mode preference was 3G or not
     */
    public static void putWasNetwork3G(boolean was, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(WAS_NETWORK_3G, was);
        editor.apply();
    }
    public static boolean getWasNetwork3G(Context context, boolean def) {
        return getPreferences(context).getBoolean(WAS_NETWORK_3G, def);
    }

    /**
     * This preference is to store the auto-set 3G network mode preference
     */
    public static void putPreferred3G(int network, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(PREFERRED_3G, network);
        editor.apply();
    }
    public static int getPreferred3G(Context context, int def) {
        return getPreferences(context).getInt(PREFERRED_3G, def);
    }

    /**
     * This preference is a flag to indicate that the {@link #putPreferred3G(int, Context)}
     * was already done.
     */
    public static void put3GTaken(boolean taken, Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(DEFAULT_3G, taken);
        editor.apply();
    }
    public static boolean get3GTaken(Context context) {
        return getPreferences(context).getBoolean(DEFAULT_3G, false);
    }
}
