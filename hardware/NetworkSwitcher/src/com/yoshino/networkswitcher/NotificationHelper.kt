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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.SystemProperties
import android.provider.Settings
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "Sony Modem"

        @Volatile
        private var manager: NotificationManager? = null
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH).apply {
            lightColor = Color.WHITE
            setSound(null, null)
            enableVibration(false)
        }
        getManager().createNotificationChannel(channel)
    }

    private fun getManager(): NotificationManager {
        if (manager == null) manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager!!
    }

    fun notifyToggleNotification(msg: String) {
        if (Settings.System.getInt(context.contentResolver, "ns_notification", 1) == 1) {
            getManager().notify(1, getToggleNotification(msg))
        }
    }

    fun notifyModemNotification(subID: Int, modemConfig: String, status: String, registration: String) {
        Settings.System.putString(context.contentResolver, "ns_status", "Sub ID: $subID\nStatus: $status\n" +
                "Config: $modemConfig\nIMS: $registration\nCust ID: ${SystemProperties.get("ro.somc.customerid", "N/A")}")

        if (Settings.System.getInt(context.contentResolver, "ns_notification", 1) == 1) {
            getManager().notify(1, getModemNotification(modemConfig, status, registration))
        }
    }

    fun cancel() = getManager().cancel(1)

    private fun getToggleNotification(msg: String): Notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(CHANNEL_ID)
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_baseline_sim_card_24)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .setColorized(true).build()

    private fun getModemNotification(modemConfig: String, status: String, registration: String): Notification {
        val finalStatus = if (status.contains("0")) "Success (0)" else "Failed ($status)"
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(CHANNEL_ID)
                .setContentText("Status: $finalStatus\nConfig: $modemConfig\nIMS: $registration")
                .setSmallIcon(R.drawable.ic_baseline_sim_card_24)
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(null)
                .setColorized(true).build()
    }
}