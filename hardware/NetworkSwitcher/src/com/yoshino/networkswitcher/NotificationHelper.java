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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID = "Sony Modem";

    private static volatile NotificationManager manager = null;
    private Context context;

    public NotificationHelper(@NonNull Context context) {
        this.context = context.getApplicationContext();
        createChannel();
    }

    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.WHITE);
        channel.setSound(null, null);
        channel.enableVibration(false);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public Notification getToggleNotification(String msg) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(CHANNEL_ID)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_baseline_sim_card_24)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(null)
                .setColorized(true).build();
    }

    public Notification getModemNotification(String modemConfig, String status, String registration) {
        String finalStatus;
        if (status.equals("0")) {
            finalStatus = "Success (" + status + ")";
        } else {
            finalStatus = "Failed (" + status + ")";
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(CHANNEL_ID)
                .setContentText("Status: " + finalStatus + "\nConfig: " + modemConfig + "\nRegistration: " + registration)
                .setSmallIcon(R.drawable.ic_baseline_sim_card_24)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(null)
                .setColorized(true).build();
    }
}
