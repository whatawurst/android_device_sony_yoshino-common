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

package com.yoshino.networkswitcher

import android.content.Context
import android.content.SharedPreferences

/**
 * Simple class for handling network preference
 *
 * @author shank03
 */
object Preference {

    private const val WAS_NETWORK_3G = "network_pref_3g"
    private const val PREFERENCE_STORED = "preference_stored"
    private const val SERVICE_CRASHED = "service_crashed"

    private fun getPreferences(context: Context): SharedPreferences =
            context.applicationContext.createDeviceProtectedStorageContext().getSharedPreferences("NetworkSwitcher", Context.MODE_PRIVATE)

    /**
     * This preference stores if the user-set network mode preference was 3G or not
     */
    fun putWasNetwork3G(was: Boolean, context: Context) {
        getPreferences(context).edit().apply {
            putBoolean(WAS_NETWORK_3G, was)
            apply()
        }
        putPreferenceStored(context)
    }
    fun getWasNetwork3G(context: Context, def: Boolean): Boolean = getPreferences(context).getBoolean(WAS_NETWORK_3G, def)

    /**
     * This preference stores if the rest of the preferences are empty or not
     */
    private fun putPreferenceStored(context: Context) {
        getPreferences(context).edit().apply {
            putBoolean(PREFERENCE_STORED, true)
            apply()
        }
    }
    fun getPreferenceStored(context: Context): Boolean = getPreferences(context).getBoolean(PREFERENCE_STORED, false)

    /**
     * Store 'true' if service destroy was not called intentionally
     *
     * The need of this is that due to OS memory management, app service is killed
     * So to prevent conflicts when restarting app, this preference will help
     */
    fun putServiceCrashed(changedOnBoot: Boolean, context: Context) {
        getPreferences(context).edit().apply {
            putBoolean(SERVICE_CRASHED, changedOnBoot)
            apply()
        }
    }
    fun getServiceCrashed(context: Context): Boolean = getPreferences(context).getBoolean(SERVICE_CRASHED, false)
}
