package com.sonymobile.customizationselector;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

abstract class Parser {

    static class DynamicConfigParser {
        private static final int RESOURCE_XML = 2131623936;
        private static final String TAG = DynamicConfigParser.class.getSimpleName();

        DynamicConfigParser() {
        }

        static HashMap<String, String> getConfiguration(Context context) {
            String str;
            CharSequence charSequence = null;
            HashMap hashMap = new HashMap();
            if (context != null) {
                Resources resources = context.getResources();
                if (resources != null) {
                    XmlResourceParser xml;
                    try {
                        xml = resources.getXml(2131623936);
                    } catch (NotFoundException e) {
                        str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Resource not found: ");
                        stringBuilder.append(e.toString());
                        CSLog.d(str, stringBuilder.toString());
                        xml = null;
                    }
                    if (xml != null) {
                        try {
                            while (xml.next() != 1) {
                                if (xml.getEventType() == 2) {
                                    str = xml.getName();
                                    if ("configuration".equals(str)) {
                                        charSequence = Parser.fix(xml.getAttributeValue(null, "config_id"));
                                    }
                                    if ("sim_config_id".equalsIgnoreCase(str)) {
                                        String access$000 = Parser.fix(xml.nextText());
                                        if (!TextUtils.isEmpty(charSequence)) {
                                            hashMap.put(access$000, charSequence);
                                        }
                                    }
                                    if ("anysim".equalsIgnoreCase(str) && !TextUtils.isEmpty(charSequence)) {
                                        hashMap.put("anysim", charSequence);
                                    }
                                }
                            }
                        } catch (IOException | XmlPullParserException e2) {
                            CSLog.e(TAG, "XML parsing failed.");
                        }
                    }
                    String str2 = TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Configurations: ");
                    stringBuilder2.append(hashMap.toString());
                    CSLog.d(str2, stringBuilder2.toString());
                }
            }
            return hashMap;
        }
    }

    static class ModemConfParser {
        private static final String LEGACY_PATH = "/etc/customization/modem";
        private static final String MODEM_CONF = "/modem.conf";
        private static final String OEM_PATH = "/modem-config";
        private static final String TAG = ModemConfParser.class.getSimpleName();

        ModemConfParser() {
        }

        static String parseModemConf(String str) {
            String str2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("setupFilePaths - configId: ");
            stringBuilder.append(str);
            CSLog.d(str2, stringBuilder.toString());
            StringBuilder stringBuilder2 = new StringBuilder();
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(Environment.getOemDirectory());
            stringBuilder3.append(OEM_PATH);
            File file = new File(stringBuilder3.toString());
            Object obj = (file.exists() && file.isDirectory()) ? 1 : null;
            if (obj != null) {
                stringBuilder2.append(file.toString());
            } else {
                stringBuilder2.append(Environment.getRootDirectory());
                stringBuilder2.append(LEGACY_PATH);
            }
            if (!TextUtils.isEmpty(str)) {
                stringBuilder2.append("/");
                stringBuilder2.append(str);
            }
            stringBuilder2.append(MODEM_CONF);
            File file2 = new File(stringBuilder2.toString());
            if (!(file2.exists() || TextUtils.isEmpty(str))) {
                String str3 = TAG;
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append("setupFilePaths - Not found: ");
                stringBuilder4.append(file2.getAbsoluteFile());
                CSLog.d(str3, stringBuilder4.toString());
                if (obj != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(file);
                    stringBuilder.append(MODEM_CONF);
                    file2 = new File(stringBuilder.toString());
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(Environment.getRootDirectory());
                    stringBuilder.append(LEGACY_PATH);
                    stringBuilder.append(MODEM_CONF);
                    file2 = new File(stringBuilder.toString());
                }
            }
            String str4 = TAG;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("setupFilePaths - path: ");
            stringBuilder2.append(file2.getAbsoluteFile());
            CSLog.d(str4, stringBuilder2.toString());
            return parseModemFileName(file2);
        }

        /* JADX WARNING: Removed duplicated region for block: B:35:0x0097 A:{SYNTHETIC, Splitter:B:35:0x0097} */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x006f A:{SYNTHETIC, Splitter:B:23:0x006f} */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0097 A:{SYNTHETIC, Splitter:B:35:0x0097} */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x006f A:{SYNTHETIC, Splitter:B:23:0x006f} */
        private static java.lang.String parseModemFileName(java.io.File r6) {
            /*
            r2 = 0;
            if (r6 == 0) goto L_0x0009;
        L_0x0003:
            r0 = r6.exists();
            if (r0 != 0) goto L_0x000c;
        L_0x0009:
            r0 = "";
        L_0x000b:
            return r0;
        L_0x000c:
            r1 = new java.io.BufferedReader;	 Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0052, all -> 0x00b7 }
            r0 = new java.io.InputStreamReader;	 Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0052, all -> 0x00b7 }
            r3 = new java.io.FileInputStream;	 Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0052, all -> 0x00b7 }
            r3.<init>(r6);	 Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0052, all -> 0x00b7 }
            r4 = java.nio.charset.StandardCharsets.UTF_8;	 Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0052, all -> 0x00b7 }
            r0.<init>(r3, r4);	 Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0052, all -> 0x00b7 }
            r1.<init>(r0);	 Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0052, all -> 0x00b7 }
            r0 = r1.readLine();	 Catch:{ FileNotFoundException -> 0x00a4, IOException -> 0x00a8 }
            if (r0 == 0) goto L_0x0046;
        L_0x0023:
            r0 = r0.trim();	 Catch:{ FileNotFoundException -> 0x00b2, IOException -> 0x00ae }
        L_0x0027:
            r1.close();	 Catch:{ IOException -> 0x0049 }
        L_0x002a:
            r1 = TAG;
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = "Parsed modem: '";
            r2.append(r3);
            r2.append(r0);
            r3 = "'";
            r2.append(r3);
            r2 = r2.toString();
            com.sonymobile.customizationselector.CSLog.d(r1, r2);
            goto L_0x000b;
        L_0x0046:
            r0 = "";
            goto L_0x0027;
        L_0x0049:
            r1 = move-exception;
            r2 = TAG;
            r3 = "IOException: while closing reader";
            com.sonymobile.customizationselector.CSLog.e(r2, r3, r1);
            goto L_0x002a;
        L_0x0052:
            r1 = move-exception;
            r0 = "";
            r3 = r1;
            r4 = r2;
        L_0x0057:
            r1 = TAG;	 Catch:{ all -> 0x00b4 }
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00b4 }
            r2.<init>();	 Catch:{ all -> 0x00b4 }
            r5 = "IOException: ";
            r2.append(r5);	 Catch:{ all -> 0x00b4 }
            r2.append(r6);	 Catch:{ all -> 0x00b4 }
            r2 = r2.toString();	 Catch:{ all -> 0x00b4 }
            com.sonymobile.customizationselector.CSLog.e(r1, r2, r3);	 Catch:{ all -> 0x00b4 }
            if (r4 == 0) goto L_0x002a;
        L_0x006f:
            r4.close();	 Catch:{ IOException -> 0x0049 }
            goto L_0x002a;
        L_0x0073:
            r0 = move-exception;
            r0 = "";
            r1 = r2;
        L_0x0077:
            r2 = TAG;	 Catch:{ all -> 0x0093 }
            r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0093 }
            r3.<init>();	 Catch:{ all -> 0x0093 }
            r4 = "File not found: ";
            r3.append(r4);	 Catch:{ all -> 0x0093 }
            r3.append(r6);	 Catch:{ all -> 0x0093 }
            r3 = r3.toString();	 Catch:{ all -> 0x0093 }
            com.sonymobile.customizationselector.CSLog.w(r2, r3);	 Catch:{ all -> 0x0093 }
            if (r1 == 0) goto L_0x002a;
        L_0x008f:
            r1.close();	 Catch:{ IOException -> 0x0049 }
            goto L_0x002a;
        L_0x0093:
            r0 = move-exception;
        L_0x0094:
            r2 = r1;
        L_0x0095:
            if (r2 == 0) goto L_0x009a;
        L_0x0097:
            r2.close();	 Catch:{ IOException -> 0x009b }
        L_0x009a:
            throw r0;
        L_0x009b:
            r1 = move-exception;
            r2 = TAG;
            r3 = "IOException: while closing reader";
            com.sonymobile.customizationselector.CSLog.e(r2, r3, r1);
            goto L_0x009a;
        L_0x00a4:
            r0 = move-exception;
            r0 = "";
            goto L_0x0077;
        L_0x00a8:
            r2 = move-exception;
            r0 = "";
            r3 = r2;
            r4 = r1;
            goto L_0x0057;
        L_0x00ae:
            r2 = move-exception;
            r3 = r2;
            r4 = r1;
            goto L_0x0057;
        L_0x00b2:
            r2 = move-exception;
            goto L_0x0077;
        L_0x00b4:
            r0 = move-exception;
            r1 = r4;
            goto L_0x0094;
        L_0x00b7:
            r0 = move-exception;
            goto L_0x0095;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.sonymobile.customizationselector.Parser$ModemConfParser.parseModemFileName(java.io.File):java.lang.String");
        }
    }

    static class ResourceXml {
        static final String ANYSIM = "anysim";
        static final String CONFIGURATION = "configuration";
        static final String CONFIG_ID = "config_id";
        static final String DEFAULT_CONFIG = "default";
        static final String GID1 = "gid1";
        static final String GID2 = "gid2";
        static final String IMSI = "imsi";
        static final String MCC = "mcc";
        static final String MNC = "mnc";
        static final String NULL_VALUE = "null";
        static final String SERVICE_PROVIDER_SIM_CONFIG = "service_provider_sim_config";
        static final String SIM_CONFIG_ID = "sim_config_id";
        static final String SP = "sp";

        ResourceXml() {
        }
    }

    static class ServiceProvidersParser {
        private static final int RESOURCE_XML = 2131623937;
        private static final String TAG = ServiceProvidersParser.class.getSimpleName();

        ServiceProvidersParser() {
        }

        static List<SimCombination> getServiceProviders(Context context) {
            String str;
            Exception e;
            String str2;
            StringBuilder stringBuilder;
            SimCombination simCombination = null;
            ArrayList arrayList = new ArrayList();
            if (context != null) {
                Resources resources = context.getResources();
                if (resources != null) {
                    XmlResourceParser xml;
                    try {
                        xml = resources.getXml(2131623937);
                    } catch (NotFoundException e2) {
                        str = TAG;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Resource not found: ");
                        stringBuilder2.append(e2.toString());
                        CSLog.d(str, stringBuilder2.toString());
                        xml = null;
                    }
                    if (xml != null) {
                        try {
                            while (xml.next() != 1) {
                                if (xml.getEventType() == 2) {
                                    str = xml.getName();
                                    if ("service_provider_sim_config".equals(str)) {
                                        String access$000 = Parser.fix(xml.getAttributeValue(null, "sim_config_id"));
                                        if (!TextUtils.isEmpty(access$000)) {
                                            simCombination = new SimCombination();
                                            simCombination.setSimConfigId(access$000);
                                            arrayList.add(simCombination);
                                        }
                                    }
                                    if (simCombination != null) {
                                        if ("mcc".equalsIgnoreCase(str)) {
                                            str = Parser.fix(xml.nextText());
                                            if (!TextUtils.isEmpty(str)) {
                                                simCombination.setMCC(str);
                                            }
                                        } else if ("mnc".equalsIgnoreCase(str)) {
                                            str = Parser.fix(xml.nextText());
                                            if (!TextUtils.isEmpty(str)) {
                                                simCombination.setMNC(str);
                                            }
                                        } else if ("sp".equalsIgnoreCase(str)) {
                                            str = Parser.fix(xml.nextText());
                                            if (!TextUtils.isEmpty(str)) {
                                                simCombination.setServiceProvider(str);
                                            }
                                        } else if ("imsi".equalsIgnoreCase(str)) {
                                            str = Parser.fix(xml.nextText());
                                            if (!TextUtils.isEmpty(str)) {
                                                simCombination.setIMSI(str);
                                            }
                                        } else if ("gid1".equalsIgnoreCase(str)) {
                                            str = Parser.fix(xml.nextText());
                                            if (!TextUtils.isEmpty(str)) {
                                                simCombination.setGid1(str);
                                            }
                                        } else if ("gid2".equalsIgnoreCase(str)) {
                                            str = Parser.fix(xml.nextText());
                                            if (!TextUtils.isEmpty(str)) {
                                                simCombination.setGid2(str);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (XmlPullParserException e3) {
                            e = e3;
                            e.printStackTrace();
                            str2 = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Number of service providers found: ");
                            stringBuilder.append(arrayList.size());
                            CSLog.d(str2, stringBuilder.toString());
                            return arrayList;
                        } catch (IOException e4) {
                            e = e4;
                            e.printStackTrace();
                            str2 = TAG;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Number of service providers found: ");
                            stringBuilder.append(arrayList.size());
                            CSLog.d(str2, stringBuilder.toString());
                            return arrayList;
                        }
                    }
                    str2 = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Number of service providers found: ");
                    stringBuilder.append(arrayList.size());
                    CSLog.d(str2, stringBuilder.toString());
                }
            }
            return arrayList;
        }
    }

    Parser() {
    }

    private static String fix(String str) {
        if (str == null) {
            str = "";
        }
        return str.replace("\n", "").replace("\t", "").trim();
    }
}
