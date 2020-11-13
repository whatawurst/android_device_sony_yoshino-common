package com.sonymobile.customizationselector;

import android.os.Environment;
import com.sonymobile.miscta.MiscTA;
import com.sonymobile.miscta.MiscTaException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

public class ModemSwitcher {

    private static final String DEFAULT_MODEM = "default";
    private static final int MAXIMUM_STATUS_FILE_LENGTH = 128;
    static final byte MODEM_COMMAND_CHANGE = (byte) 1;
    static final int MODEM_COMMAND_UNIT = 2405;
    public static final String MODEM_FS_PATH;
    static final int MODEM_MAGIC_COMMAND_LENGTH = 3;
    static final byte MODEM_MISCTA_MAGIC1 = (byte) -16;
    static final byte MODEM_MISCTA_MAGIC2 = (byte) 122;
    public static final String MODEM_STATUS_FILE = "/cache/modem/modem_switcher_status";
    private static final String RESET_MODEM_ST1 = "reset_modemst1";
    private static final String RESET_MODEM_ST2 = "reset_modemst2";
    public static final String SINGLE_MODEM_FS = "single_filesystem";
    private static final String TAG = ModemSwitcher.class.getSimpleName();
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
            return (str.equals(this.modemST1Name) || str.equals(this.modemST2Name)) ? false : str.endsWith("_tar.mbn");
        }
    }

    private static class ModemStatus {
        final String currentModem;
        final boolean modemStatusSuccessful;

        ModemStatus(boolean z, String str) {
            this.modemStatusSuccessful = z;
            this.currentModem = str;
        }
    }

    static {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Environment.getRootDirectory());
        stringBuilder.append("/etc/customization/modem/");
        MODEM_FS_PATH = stringBuilder.toString();
    }

    private static String lookupSymlinkTarget(String str) {
        String name;
        String str2;
        StringBuilder stringBuilder;
        try {
            name = new File(MODEM_FS_PATH, str).getCanonicalFile().getName();
        } catch (IOException e) {
            str2 = TAG;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Error when getting canonical File: ");
            stringBuilder.append(e);
            CSLog.e(str2, stringBuilder.toString());
            name = str;
        }
        str2 = TAG;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Target filename of: ");
        stringBuilder.append(str);
        stringBuilder.append(" is: ");
        stringBuilder.append(name);
        CSLog.d(str2, stringBuilder.toString());
        return name;
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x0129 A:{SYNTHETIC, Splitter:B:46:0x0129} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0120 A:{SYNTHETIC, Splitter:B:40:0x0120} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0129 A:{SYNTHETIC, Splitter:B:46:0x0129} */
    private static com.sonymobile.customizationselector.ModemSwitcher.ModemStatus readModemAndStatus() {
        /*
        r5 = 0;
        r2 = 1;
        r3 = 0;
        r0 = new java.io.File;
        r1 = MODEM_STATUS_FILE;
        r0.<init>(r1);
        r1 = "default";
        r4 = r0.isFile();
        if (r4 != 0) goto L_0x0021;
    L_0x0012:
        r0 = TAG;
        r1 = "Status file does not exists or is not a file.";
        com.sonymobile.customizationselector.CSLog.e(r0, r1);
        r0 = new com.sonymobile.customizationselector.ModemSwitcher$ModemStatus;
        r1 = "default";
        r0.<init>(r3, r1);
    L_0x0020:
        return r0;
    L_0x0021:
        r6 = r0.length();
        r8 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 <= 0) goto L_0x0057;
    L_0x002b:
        r1 = TAG;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "Status file is too large: ";
        r2.append(r4);
        r4 = r0.length();
        r2.append(r4);
        r0 = ", more than limit: ";
        r2.append(r0);
        r0 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r2.append(r0);
        r0 = r2.toString();
        com.sonymobile.customizationselector.CSLog.e(r1, r0);
        r0 = new com.sonymobile.customizationselector.ModemSwitcher$ModemStatus;
        r1 = "default";
        r0.<init>(r3, r1);
        goto L_0x0020;
    L_0x0057:
        r4 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0106, all -> 0x0125 }
        r6 = new java.io.InputStreamReader;	 Catch:{ IOException -> 0x0106, all -> 0x0125 }
        r7 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0106, all -> 0x0125 }
        r7.<init>(r0);	 Catch:{ IOException -> 0x0106, all -> 0x0125 }
        r0 = java.nio.charset.StandardCharsets.UTF_8;	 Catch:{ IOException -> 0x0106, all -> 0x0125 }
        r6.<init>(r7, r0);	 Catch:{ IOException -> 0x0106, all -> 0x0125 }
        r4.<init>(r6);	 Catch:{ IOException -> 0x0106, all -> 0x0125 }
        r0 = r4.readLine();	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        if (r0 != 0) goto L_0x0083;
    L_0x006e:
        r0 = TAG;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2 = "Line is null";
        com.sonymobile.customizationselector.CSLog.e(r0, r2);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r0 = new com.sonymobile.customizationselector.ModemSwitcher$ModemStatus;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2 = 0;
        r5 = "default";
        r0.<init>(r2, r5);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r4.close();	 Catch:{ IOException -> 0x0081 }
        goto L_0x0020;
    L_0x0081:
        r1 = move-exception;
        goto L_0x0020;
    L_0x0083:
        r5 = ",";
        r5 = r0.split(r5);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r6 = TAG;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r7 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r7.<init>();	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r8 = "Read line: ";
        r7.append(r8);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r7.append(r0);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r0 = r7.toString();	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        com.sonymobile.customizationselector.CSLog.d(r6, r0);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r0 = r5.length;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r6 = 2;
        if (r0 == r6) goto L_0x00ca;
    L_0x00a3:
        r0 = TAG;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2.<init>();	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r6 = "Format error status file, nbr of fields found:";
        r2.append(r6);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r5 = r5.length;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2.append(r5);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2 = r2.toString();	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        com.sonymobile.customizationselector.CSLog.e(r0, r2);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r0 = new com.sonymobile.customizationselector.ModemSwitcher$ModemStatus;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2 = 0;
        r5 = "default";
        r0.<init>(r2, r5);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r4.close();	 Catch:{ IOException -> 0x00c7 }
        goto L_0x0020;
    L_0x00c7:
        r1 = move-exception;
        goto L_0x0020;
    L_0x00ca:
        r0 = 0;
        r0 = r5[r0];	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r0 = java.lang.Integer.parseInt(r0);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        if (r0 == 0) goto L_0x00f9;
    L_0x00d3:
        r2 = TAG;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r5 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r5.<init>();	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r6 = "Unsuccessful status code found: ";
        r5.append(r6);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r5.append(r0);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r0 = r5.toString();	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        com.sonymobile.customizationselector.CSLog.d(r2, r0);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r0 = new com.sonymobile.customizationselector.ModemSwitcher$ModemStatus;	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r2 = 0;
        r5 = "default";
        r0.<init>(r2, r5);	 Catch:{ IOException -> 0x012d, all -> 0x0130 }
        r4.close();	 Catch:{ IOException -> 0x00f6 }
        goto L_0x0020;
    L_0x00f6:
        r1 = move-exception;
        goto L_0x0020;
    L_0x00f9:
        r0 = r5[r2];
        r4.close();	 Catch:{ IOException -> 0x0136 }
        r1 = r0;
    L_0x00ff:
        r0 = new com.sonymobile.customizationselector.ModemSwitcher$ModemStatus;
        r0.<init>(r2, r1);
        goto L_0x0020;
    L_0x0106:
        r0 = move-exception;
        r2 = r5;
    L_0x0108:
        r4 = TAG;	 Catch:{ all -> 0x0133 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0133 }
        r5.<init>();	 Catch:{ all -> 0x0133 }
        r6 = "Failed to read FOTA STATUS() ";
        r5.append(r6);	 Catch:{ all -> 0x0133 }
        r5.append(r0);	 Catch:{ all -> 0x0133 }
        r0 = r5.toString();	 Catch:{ all -> 0x0133 }
        com.sonymobile.customizationselector.CSLog.e(r4, r0);	 Catch:{ all -> 0x0133 }
        if (r2 == 0) goto L_0x013c;
    L_0x0120:
        r2.close();	 Catch:{ IOException -> 0x0139 }
        r2 = r3;
        goto L_0x00ff;
    L_0x0125:
        r0 = move-exception;
        r1 = r5;
    L_0x0127:
        if (r1 == 0) goto L_0x012c;
    L_0x0129:
        r1.close();	 Catch:{ IOException -> 0x013e }
    L_0x012c:
        throw r0;
    L_0x012d:
        r0 = move-exception;
        r2 = r4;
        goto L_0x0108;
    L_0x0130:
        r0 = move-exception;
        r1 = r4;
        goto L_0x0127;
    L_0x0133:
        r0 = move-exception;
        r1 = r2;
        goto L_0x0127;
    L_0x0136:
        r1 = move-exception;
        r1 = r0;
        goto L_0x00ff;
    L_0x0139:
        r0 = move-exception;
        r2 = r3;
        goto L_0x00ff;
    L_0x013c:
        r2 = r3;
        goto L_0x00ff;
    L_0x013e:
        r1 = move-exception;
        goto L_0x012c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sonymobile.customizationselector.ModemSwitcher.readModemAndStatus():com.sonymobile.customizationselector.ModemSwitcher$ModemStatus");
    }

    public String[] getAvailableModemConfigurations() {
        int i = 0;
        if (this.mCachedModemConfigurations == null) {
            File file = new File(MODEM_FS_PATH);
            if (!file.isDirectory()) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Could not open directory: ");
                stringBuilder.append(MODEM_FS_PATH);
                CSLog.e(str, stringBuilder.toString());
                this.mCachedModemConfigurations = new String[]{SINGLE_MODEM_FS};
            }
            if (this.mCachedModemConfigurations == null) {
                String[] strArr;
                String[] list = file.list(new ModemFilter(lookupSymlinkTarget(RESET_MODEM_ST1), lookupSymlinkTarget(RESET_MODEM_ST2)));
                if (list == null || list.length <= 0) {
                    CSLog.e(TAG, "Could not get list of available modem filesystems");
                    strArr = new String[]{SINGLE_MODEM_FS};
                } else {
                    while (i < list.length) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(MODEM_FS_PATH);
                        stringBuilder2.append(list[i]);
                        list[i] = stringBuilder2.toString();
                        i++;
                    }
                    strArr = list;
                }
                this.mCachedModemConfigurations = strArr;
            }
            Arrays.sort(this.mCachedModemConfigurations, new Comparator<String>() {
                public int compare(String str, String str2) {
                    return str.length() - str2.length();
                }
            });
        }
        return (String[]) Arrays.copyOf(this.mCachedModemConfigurations, this.mCachedModemConfigurations.length);
    }

    public String getCurrentModemConfig() throws IOException {
        ModemStatus readModemAndStatus = readModemAndStatus();
        if (readModemAndStatus.modemStatusSuccessful) {
            String lookupSymlinkTarget = lookupSymlinkTarget(readModemAndStatus.currentModem);
            if (new File(MODEM_FS_PATH, lookupSymlinkTarget).exists()) {
                if (DEFAULT_MODEM.equals(lookupSymlinkTarget)) {
                    CSLog.e(TAG, "Default modem is a file node that does not point to valid modem fs");
                    throw new IOException("Default modem is a file node that does not point to valid modem fs");
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MODEM_FS_PATH);
                stringBuilder.append(lookupSymlinkTarget);
                return stringBuilder.toString();
            } else if (DEFAULT_MODEM.equals(lookupSymlinkTarget)) {
                CSLog.d(TAG, "No modem filesystems exists, return SINGLE_MODEM_FS");
                return SINGLE_MODEM_FS;
            } else {
                String str = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Current modem configuration is set to an invalid value: ");
                stringBuilder2.append(lookupSymlinkTarget);
                stringBuilder2.append(". Returning ''");
                CSLog.w(str, stringBuilder2.toString());
                return "";
            }
        }
        throw new IOException("Current modem configuration could not be read due to error ");
    }

    public boolean isModemStatusSuccess() {
        return readModemAndStatus().modemStatusSuccessful;
    }

    public boolean setModemConfiguration(String str) {
        if (this.mConfigurationSet) {
            CSLog.e(TAG, "A configuration has already been set, phone needs to reboot");
            return false;
        }
        boolean z;
        for (String equals : getAvailableModemConfigurations()) {
            if (equals.equals(str)) {
                z = true;
                break;
            }
        }
        z = false;
        if (z) {
            String str2;
            StringBuilder stringBuilder;
            try {
                if (getCurrentModemConfig().equals(str)) {
                    str2 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Selected modem configuration is already active!: ");
                    stringBuilder.append(str);
                    CSLog.e(str2, stringBuilder.toString());
                    return false;
                }
            } catch (IOException e) {
                CSLog.w(TAG, "Unable to read out current configuration");
            }
            str2 = new File(str).getName();
            if (writeModemToMiscTA(str2)) {
                String str3 = TAG;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Modem set '");
                stringBuilder.append(str2);
                stringBuilder.append("'");
                CSLog.d(str3, stringBuilder.toString());
                this.mConfigurationSet = true;
                return true;
            }
            CSLog.e(TAG, "Failed to write selected configuration to miscta");
            return false;
        }
        String str4 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Modem name '");
        stringBuilder2.append(str);
        stringBuilder2.append("' is not valid.");
        CSLog.e(str4, stringBuilder2.toString());
        return false;
    }

    public boolean writeModemToMiscTA(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        byte[] bArr = new byte[(bytes.length + 3)];
        bArr[0] = (byte) ((byte) -16);
        bArr[1] = (byte) ((byte) 122);
        bArr[2] = (byte) ((byte) 1);
        System.arraycopy(bytes, 0, bArr, 3, bytes.length);
        try {
            MiscTA.write(MODEM_COMMAND_UNIT, bArr);
            return true;
        } catch (MiscTaException e) {
            String str2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to write to miscta:");
            stringBuilder.append(e);
            CSLog.e(str2, stringBuilder.toString());
            return false;
        }
    }
}
