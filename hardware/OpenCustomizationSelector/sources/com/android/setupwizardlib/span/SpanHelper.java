package com.android.setupwizardlib.span;

import android.text.Spannable;

public class SpanHelper {
    public static void replaceSpan(Spannable spannable, Object obj, Object obj2) {
        int spanStart = spannable.getSpanStart(obj);
        int spanEnd = spannable.getSpanEnd(obj);
        spannable.removeSpan(obj);
        spannable.setSpan(obj2, spanStart, spanEnd, 0);
    }
}
