package com.sonymobile.customizationselector.Parser;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import com.sonymobile.customizationselector.CSLog;
import com.sonymobile.customizationselector.R;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import static com.sonymobile.customizationselector.Parser.XmlConstants.*;

public class DynamicConfigParser {

    private static final String TAG = DynamicConfigParser.class.getSimpleName();

    private static final int RESOURCE_XML = R.xml.configuration_selectors;

    public static HashMap<String, String> getConfiguration(Context context) {
        String tag, configID = "";

        HashMap<String, String> hashMap = new HashMap<>();
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
                                if (CONFIGURATION.equals(tag)) {
                                    configID = fix(xml.getAttributeValue(null, CONFIG_ID));
                                }
                                if (SIM_CONFIG_ID.equalsIgnoreCase(tag)) {
                                    String value = fix(xml.nextText());
                                    if (!TextUtils.isEmpty(configID)) {
                                        hashMap.put(value, configID);
                                    }
                                }
                                if (ANY_SIM.equalsIgnoreCase(tag) && !TextUtils.isEmpty(configID)) {
                                    hashMap.put(ANY_SIM, configID);
                                }
                            }
                        }
                    } catch (IOException | XmlPullParserException e2) {
                        CSLog.e(TAG, "XML parsing failed.");
                    }
                }
                CSLog.d(TAG, "Configurations: " + hashMap.toString());
            }
        }
        return hashMap;
    }

    private static String fix(String str) {
        if (str == null) {
            str = "";
        }
        return str.replace("\n", "").replace("\t", "").trim();
    }
}
