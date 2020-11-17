package com.sonymobile.customizationselector;

import android.os.Environment;
import com.sonymobile.miscta.MiscTA;
import com.sonymobile.miscta.MiscTaException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

public class ModemSwitcher {

    private static final String TAG = ModemSwitcher.class.getSimpleName();

    private static final int MODEM_COMMAND_UNIT = 2405;
    private static final int MODEM_MAGIC_COMMAND_LENGTH = 3;
    private static final byte MODEM_COMMAND_CHANGE = (byte) 1;
    private static final byte MODEM_MISC_TA_MAGIC1 = (byte) -16;
    private static final byte MODEM_MISC_TA_MAGIC2 = (byte) 122;

    private static final String DEFAULT_MODEM = "default";
    private static final String RESET_MODEM_ST1 = "reset_modemst1";
    private static final String RESET_MODEM_ST2 = "reset_modemst2";

    public static final String MODEM_FS_PATH = Environment.getRootDirectory() + "/etc/customization/modem/";
    public static final String MODEM_STATUS_FILE = "/cache/modem/modem_switcher_status";
    public static final String SINGLE_MODEM_FS = "single_filesystem";

    private static final int MAXIMUM_STATUS_FILE_LENGTH = 128;
    public static final int UA_MODEM_SWITCHER_STATUS_SUCCESS = 0;

    private String[] mCachedModemConfigurations = null;
    private boolean mConfigurationSet = false;

    private static class ModemFilter implements FilenameFilter {
        private final String modemST1Name;
        private final String modemST2Name;

        ModemFilter(String str, String str2) {
            this.modemST1Name = str;
            this.modemST2Name = str2;
        }

        public boolean accept(File file, String str) {
            // Actual: (str.equals(this.modemST1Name) || str.equals(this.modemST2Name)) ? false : str.endsWith("_tar.mbn")
            return !str.equals(this.modemST1Name) && !str.equals(this.modemST2Name) && str.endsWith("_tar.mbn");
        }
    }

    private static class ModemStatus {
        final String currentModem;
        final boolean modemStatusSuccessful;

        ModemStatus(boolean statusSuccess, String modem) {
            this.modemStatusSuccessful = statusSuccess;
            this.currentModem = modem;
        }
    }

    private static String lookupSymlinkTarget(String str) {
        String name;
        try {
            name = new File(MODEM_FS_PATH, str).getCanonicalFile().getName();
        } catch (IOException e) {
            CSLog.e(TAG, "Error when getting canonical File: ", e);
            name = str;
        }
        CSLog.d(TAG, "Target filename of: " + str + " is: " + name);
        return name;
    }

    private static ModemStatus readModemAndStatus() {
        File statusFile = new File(MODEM_STATUS_FILE);
        String currentModem = DEFAULT_MODEM;

        if (!statusFile.isFile()) {
            CSLog.e(TAG, "Status file does not exists or is not a file.");
            return new ModemStatus(false, currentModem);

        } else if (statusFile.length() > MAXIMUM_STATUS_FILE_LENGTH) {
            CSLog.e(TAG, "Status file is too large: " + statusFile.length() + ", more than limit: " + MAXIMUM_STATUS_FILE_LENGTH);
            return new ModemStatus(false, currentModem);

        } else {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(statusFile), StandardCharsets.UTF_8));

                String line = in.readLine();
                if (line == null) {
                    CSLog.e(TAG, "Line is null");
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new ModemStatus(false, currentModem);
                }

                String[] values = line.split(",");
                CSLog.d(TAG, "Read line: " + line);

                if (values.length != 2) {
                    CSLog.e(TAG, "Format error status file, nbr of fields found:" + values.length);
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new ModemStatus(false, currentModem);
                }

                int tmpStatus = Integer.parseInt(values[0]);
                if (tmpStatus != UA_MODEM_SWITCHER_STATUS_SUCCESS) {
                    CSLog.d(TAG, "Unsuccessful status code found: " + tmpStatus);
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new ModemStatus(false, currentModem);
                }
                currentModem = values[1];
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new ModemStatus(true, currentModem);
            } catch (Exception e) {
                CSLog.e(TAG, "Failed to read FOTA STATUS() " + e);
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                return null;
            }
        }
    }

    public String[] getAvailableModemConfigurations() {
        if (mCachedModemConfigurations == null) {
            File file = new File(MODEM_FS_PATH);
            if (!file.isDirectory()) {
                CSLog.e(TAG, "Could not open directory: " + MODEM_FS_PATH);
                mCachedModemConfigurations = new String[]{SINGLE_MODEM_FS};
            }

            if (mCachedModemConfigurations == null) {
                String[] strArr;
                String[] list = file.list(new ModemFilter(lookupSymlinkTarget(RESET_MODEM_ST1), lookupSymlinkTarget(RESET_MODEM_ST2)));
                if (list == null || list.length <= 0) {
                    CSLog.e(TAG, "Could not get list of available modem filesystems");
                    strArr = new String[]{SINGLE_MODEM_FS};
                } else {
                    for (int i = 0; i < list.length; i++) {
                        list[i] = MODEM_FS_PATH + list[i];
                    }
                    strArr = list;
                }
                mCachedModemConfigurations = strArr;
            }
            Arrays.sort(mCachedModemConfigurations, Comparator.comparingInt(String::length));
        }
        return Arrays.copyOf(mCachedModemConfigurations, mCachedModemConfigurations.length);
    }

    public static String getCurrentModemConfig() throws IOException {
        ModemStatus readModemAndStatus = readModemAndStatus();

        if (readModemAndStatus != null && readModemAndStatus.modemStatusSuccessful) {
            String lookupSymlinkTarget = lookupSymlinkTarget(readModemAndStatus.currentModem);

            if (new File(MODEM_FS_PATH, lookupSymlinkTarget).exists()) {
                if (DEFAULT_MODEM.equals(lookupSymlinkTarget)) {
                    CSLog.e(TAG, "Default modem is a file node that does not point to valid modem fs");
                    throw new IOException("Default modem is a file node that does not point to valid modem fs");
                }
                return MODEM_FS_PATH + lookupSymlinkTarget;

            } else if (DEFAULT_MODEM.equals(lookupSymlinkTarget)) {
                CSLog.d(TAG, "No modem filesystems exists, return SINGLE_MODEM_FS");
                return SINGLE_MODEM_FS;

            } else {
                CSLog.w(TAG, "Current modem configuration is set to an invalid value: " + lookupSymlinkTarget + ". Returning");
                return "";
            }
        }
        throw new IOException("Current modem configuration could not be read due to error ");
    }

    public boolean isModemStatusSuccess() {
        ModemStatus status = readModemAndStatus();
        return status != null && status.modemStatusSuccessful;
    }

    public boolean setModemConfiguration(String modemConfig) {
        if (mConfigurationSet) {
            CSLog.e(TAG, "A configuration has already been set, phone needs to reboot");
            return false;
        }
        boolean hasModemConfig = false;
        for (String m : getAvailableModemConfigurations()) {
            if (m.equals(modemConfig)) {
                hasModemConfig = true;
                break;
            }
        }

        if (hasModemConfig) {
            try {
                if (getCurrentModemConfig().equals(modemConfig)) {
                    CSLog.e(TAG, "Selected modem configuration is already active!: " + modemConfig);
                    return false;
                }
            } catch (IOException e) {
                CSLog.w(TAG, "Unable to read out current configuration");
            }

            String modemFileName = new File(modemConfig).getName();
            if (writeModemToMiscTA(modemFileName)) {
                CSLog.d(TAG, "Modem set " + modemFileName);
                mConfigurationSet = true;
                return true;
            }
            CSLog.e(TAG, "Failed to write selected configuration to miscta");
            return false;
        }
        CSLog.e(TAG, "Modem name " + modemConfig + " is not valid.");
        return false;
    }

    public static boolean writeModemToMiscTA(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] bArr = new byte[(bytes.length + MODEM_MAGIC_COMMAND_LENGTH)];
        bArr[0] = MODEM_MISC_TA_MAGIC1;
        bArr[1] = MODEM_MISC_TA_MAGIC2;
        bArr[2] = MODEM_COMMAND_CHANGE;
        System.arraycopy(bytes, 0, bArr, MODEM_MAGIC_COMMAND_LENGTH, bytes.length);
        try {
            MiscTA.write(MODEM_COMMAND_UNIT, bArr);
            return true;
        } catch (MiscTaException e) {
            CSLog.e(TAG, "Unable to write to miscta:" + e);
            return false;
        }
    }
}
