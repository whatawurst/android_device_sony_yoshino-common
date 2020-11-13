package com.sonymobile.customizationselector.Parser;

import android.os.Environment;
import android.text.TextUtils;
import com.sonymobile.customizationselector.CSLog;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ModemConfParser {

    private static final String TAG = ModemConfParser.class.getSimpleName();

    private static final String LEGACY_PATH = "/etc/customization/modem";
    private static final String MODEM_CONF = "/modem.conf";
    private static final String OEM_PATH = "/modem-config";

    public static String parseModemConf(String conf) {
        CSLog.d(TAG, "setupFilePaths - configId: " + conf);

        File oemDir = new File(Environment.getOemDirectory() + OEM_PATH);
        StringBuilder filePath = new StringBuilder();

        if (oemDir.exists() && oemDir.isDirectory()) {
            filePath.append(oemDir.toString());
        } else {
            filePath.append(Environment.getRootDirectory());
            filePath.append(LEGACY_PATH);
        }
        if (!TextUtils.isEmpty(conf)) {
            filePath.append("/");
            filePath.append(conf);
        }
        filePath.append(MODEM_CONF);

        File modemFileName = new File(filePath.toString());
        if (!modemFileName.exists() && !TextUtils.isEmpty(conf)) {
            CSLog.d(TAG, "setupFilePaths - Not found: " + modemFileName.getAbsoluteFile());
            if (oemDir.exists() && oemDir.isDirectory()) {
                modemFileName = new File(oemDir + MODEM_CONF);
            } else {
                modemFileName = new File(Environment.getRootDirectory() + LEGACY_PATH + MODEM_CONF);
            }
        }
        CSLog.d(TAG, "setupFilePaths - path: " + modemFileName.getAbsoluteFile());
        return parseModemFileName(modemFileName);
    }

    private static String parseModemFileName(File modemFile) {
        String modemVariant = "";
        if (modemFile == null || !modemFile.exists()) {
            return modemVariant;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(modemFile), StandardCharsets.UTF_8));
            String line = br.readLine();
            if (line != null) {
                modemVariant = line.trim();
            } else {
                modemVariant = "";
            }
        } catch (FileNotFoundException e) {
            CSLog.w(TAG, "File not found: " + modemFile);
        } catch (IOException e) {
            CSLog.e(TAG, "IOException: " + modemFile, e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                CSLog.e(TAG, "IOException: while closing reader", e);
            }
        }
        CSLog.d(TAG, "Parsed modem: '" + modemVariant + "'");
        return modemVariant;
    }
}
