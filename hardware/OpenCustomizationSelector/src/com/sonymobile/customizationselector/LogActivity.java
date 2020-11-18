package com.sonymobile.customizationselector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.AppCompatTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        AppCompatTextView logText = findViewById(R.id.log_text);

        File logFile = new File(createDeviceProtectedStorageContext().getFilesDir(), "cs.log");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
            String line;

            while ((line = br.readLine()) != null) {
                String finalLine = line;
                new Handler(getMainLooper()).postDelayed(() -> {
                    logText.append(finalLine);
                    logText.append("\n");
                }, 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
