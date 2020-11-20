package com.sonymobile.customizationselector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LogActivity extends Activity {

    private static final long SIZE_THRESH_HOLD = 2 * 1024 * 1024L; // 2 MB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        AppCompatTextView logText = findViewById(R.id.log_text);
        ProgressBar progressBar = findViewById(R.id.log_pr);

        File logFile = new File(createDeviceProtectedStorageContext().getFilesDir(), "cs.log");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
            String line;
            ArrayList<String> lines = new ArrayList<>();

            // Get all the lines
            while ((line = br.readLine()) != null) {
                lines.add(line + "\n");
            }
            br.close();
            br = null;

            new Handler(getMainLooper()).postDelayed(() -> {
                boolean threshTrimmed = false;
                ArrayList<String> outLines = new ArrayList<>();

                // Append from last line to first till it reaches thresh hold
                long len = 0L;
                for (int i = lines.size() - 1; i >= 0; i--) {
                    if (len >= SIZE_THRESH_HOLD) {
                        threshTrimmed = true;
                        break;
                    }
                    len += lines.get(i).length();
                    outLines.add(lines.get(i));
                }

                // Print it
                progressBar.setVisibility(View.GONE);
                for (int i = outLines.size() - 1; i >= 0; i--) {
                    logText.append(outLines.get(i));
                }

                if (threshTrimmed) {
                    Toast.makeText(this, "Data trimmed to 2MB of chars", Toast.LENGTH_LONG).show();
                }

                // Clear memory as much as possible
                lines.clear();
                outLines.clear();
                System.gc();
            }, 400);
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
