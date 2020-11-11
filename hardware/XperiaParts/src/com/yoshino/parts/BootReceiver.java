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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;

import static com.yoshino.parts.Constants.GLOVE_MODE;
import static com.yoshino.parts.Constants.GLOVE_PROP;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) {
            return;
        }

        boolean isGloveModeEnabled = Settings.System.getInt(context.getContentResolver(), GLOVE_MODE, 0) == 1;
        SystemProperties.set(GLOVE_PROP, isGloveModeEnabled ? "1" : "0");
    }
}
