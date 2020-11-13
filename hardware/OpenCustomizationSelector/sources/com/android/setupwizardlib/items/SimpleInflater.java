package com.android.setupwizardlib.items;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class SimpleInflater<T> {
    private static final boolean DEBUG = false;
    private static final String TAG = "SimpleInflater";
    protected final Resources mResources;

    protected SimpleInflater(@NonNull Resources resources) {
        this.mResources = resources;
    }

    private T createItemFromTag(String str, AttributeSet attributeSet) {
        try {
            return onCreateItem(str, attributeSet);
        } catch (InflateException e) {
            throw e;
        } catch (Exception e2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(attributeSet.getPositionDescription());
            stringBuilder.append(": Error inflating class ");
            stringBuilder.append(str);
            throw new InflateException(stringBuilder.toString(), e2);
        }
    }

    private void rInflate(XmlPullParser xmlPullParser, T t, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return;
            }
            if (next == 2 && !onInterceptCreateItem(xmlPullParser, t, attributeSet)) {
                Object createItemFromTag = createItemFromTag(xmlPullParser.getName(), attributeSet);
                onAddChildItem(t, createItemFromTag);
                rInflate(xmlPullParser, createItemFromTag, attributeSet);
            }
        }
    }

    public Resources getResources() {
        return this.mResources;
    }

    public T inflate(int i) {
        XmlPullParser xml = getResources().getXml(i);
        try {
            T inflate = inflate(xml);
            return inflate;
        } finally {
            xml.close();
        }
    }

    public T inflate(XmlPullParser xmlPullParser) {
        int next;
        StringBuilder stringBuilder;
        AttributeSet asAttributeSet = Xml.asAttributeSet(xmlPullParser);
        do {
            try {
                next = xmlPullParser.next();
                if (next == 2) {
                    break;
                }
            } catch (XmlPullParserException e) {
                throw new InflateException(e.getMessage(), e);
            } catch (IOException e2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(xmlPullParser.getPositionDescription());
                stringBuilder.append(": ");
                stringBuilder.append(e2.getMessage());
                throw new InflateException(stringBuilder.toString(), e2);
            }
        } while (next != 1);
        if (next == 2) {
            Object createItemFromTag = createItemFromTag(xmlPullParser.getName(), asAttributeSet);
            rInflate(xmlPullParser, createItemFromTag, asAttributeSet);
            return createItemFromTag;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(xmlPullParser.getPositionDescription());
        stringBuilder.append(": No start tag found!");
        throw new InflateException(stringBuilder.toString());
    }

    public abstract void onAddChildItem(T t, T t2);

    public abstract T onCreateItem(String str, AttributeSet attributeSet);

    /* Access modifiers changed, original: protected */
    public boolean onInterceptCreateItem(XmlPullParser xmlPullParser, T t, AttributeSet attributeSet) throws XmlPullParserException {
        return DEBUG;
    }
}
