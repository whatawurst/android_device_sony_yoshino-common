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

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import java.io.File

class LogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_activity)

        val log = findViewById<AppCompatTextView>(R.id.log_text)
        val file = File(createDeviceProtectedStorageContext().filesDir, "ns.log")
        if (file.exists()) {
            file.forEachLine {
                Handler(mainLooper).postDelayed({ log.append("$it\n") }, 300)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}