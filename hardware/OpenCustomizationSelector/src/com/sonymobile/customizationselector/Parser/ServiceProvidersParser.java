package com.sonymobile.customizationselector.Parser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import com.sonymobile.customizationselector.CSLog;
import com.sonymobile.customizationselector.R;
import com.sonymobile.customizationselector.SimCombination;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sonymobile.customizationselector.Parser.XmlConstants.*;

public class ServiceProvidersParser {

    private static final String TAG = ServiceProvidersParser.class.getSimpleName();
    private static final int RESOURCE_XML = R.xml.service_providers;

    public static List<SimCombination> getServiceProviders(Context context) {
        String tag;
        SimCombination simCombination = null;
        ArrayList<SimCombination> arrayList = new ArrayList<>();

        if (context != null) {
            Resources resources = context.getResources();
            if (resources != null) {

                XmlResourceParser xml;
                try {
                    xml = resources.getXml(RESOURCE_XML);
                } catch (Resources.NotFoundException e) {
                    CSLog.e(TAG, "Resource not found: ", e);
                    xml = null;
                }

                if (xml != null) {
                    try {
                        while (xml.next() != 1) {
                            if (xml.getEventType() == 2) {
                                tag = xml.getName();
                                if (SERVICE_PROVIDER_SIM_CONFIG.equals(tag)) {
                                    String value = fix(xml.getAttributeValue(null, SIM_CONFIG_ID));
                                    if (!TextUtils.isEmpty(value)) {
                                        simCombination = new SimCombination();
                                        simCombination.setSimConfigId(value);
                                        arrayList.add(simCombination);
                                    }
                                }

                                if (simCombination != null) {
                                    if (MCC.equalsIgnoreCase(tag)) {
                                        tag = fix(xml.nextText());
                                        if (!TextUtils.isEmpty(tag)) {
                                            simCombination.setMCC(tag);
                                        }
                                    } else if (MNC.equalsIgnoreCase(tag)) {
                                        tag = fix(xml.nextText());
                                        if (!TextUtils.isEmpty(tag)) {
                                            simCombination.setMNC(tag);
                                        }
                                    } else if (SP.equalsIgnoreCase(tag)) {
                                        tag = fix(xml.nextText());
                                        if (!TextUtils.isEmpty(tag)) {
                                            simCombination.setServiceProvider(tag);
                                        }
                                    } else if (IMSI.equalsIgnoreCase(tag)) {
                                        tag = fix(xml.nextText());
                                        if (!TextUtils.isEmpty(tag)) {
                                            simCombination.setIMSI(tag);
                                        }
                                    } else if (GID1.equalsIgnoreCase(tag)) {
                                        tag = fix(xml.nextText());
                                        if (!TextUtils.isEmpty(tag)) {
                                            simCombination.setGid1(tag);
                                        }
                                    } else if (GID2.equalsIgnoreCase(tag)) {
                                        tag = fix(xml.nextText());
                                        if (!TextUtils.isEmpty(tag)) {
                                            simCombination.setGid2(tag);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        CSLog.d(TAG, "Number of service providers found: " + arrayList.size());
                        return arrayList;
                    } catch (IOException e) {
                        e.printStackTrace();
                        CSLog.d(TAG, "Number of service providers found: " + arrayList.size());
                        return arrayList;
                    }
                }
                CSLog.d(TAG, "Number of service providers found: " + arrayList.size());
            }
        }
        return arrayList;
    }

    private static String fix(String str) {
        if (str == null) {
            str = "";
        }
        return str.replace("\n", "").replace("\t", "").trim();
    }
}
