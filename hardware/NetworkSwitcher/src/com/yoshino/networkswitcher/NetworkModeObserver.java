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
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.annotation.Nullable;

/**
 * An observer class to observe the changes in the network mode
 * preference in Settings
 *
 * @author shank03
 */
public class NetworkModeObserver extends ContentObserver {

    private OnNetworkChangeListener listener;
    private Context context;
    private int subID;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public NetworkModeObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    public void register(OnNetworkChangeListener listener, int subID) {
        this.subID = subID;
        this.listener = listener;
        context.getContentResolver().registerContentObserver(Settings.Global.getUriFor(Settings.Global.PREFERRED_NETWORK_MODE + subID),
                false, this, UserHandle.USER_CURRENT);
    }

    public void unregister() {
        context.getContentResolver().unregisterContentObserver(this);
    }

    @Override
    public void onChange(boolean selfChange, @Nullable Uri uri) {
        listener.onUpdate(uri, subID);
    }

    public interface OnNetworkChangeListener {
        void onUpdate(@Nullable Uri uri, int subID);
    }
}
