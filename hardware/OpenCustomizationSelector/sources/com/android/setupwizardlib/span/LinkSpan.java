package com.android.setupwizardlib.span;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class LinkSpan extends ClickableSpan {
    private static final String TAG = "LinkSpan";
    private static final Typeface TYPEFACE_MEDIUM = Typeface.create("sans-serif-medium", 0);
    private final String mId;

    @Deprecated
    public interface OnClickListener {
        void onClick(LinkSpan linkSpan);
    }

    public interface OnLinkClickListener {
        boolean onLinkClick(LinkSpan linkSpan);
    }

    public LinkSpan(String str) {
        this.mId = str;
    }

    private boolean dispatchClick(View view) {
        boolean z = false;
        if (view instanceof OnLinkClickListener) {
            z = ((OnLinkClickListener) view).onLinkClick(this);
        }
        if (z) {
            return z;
        }
        OnClickListener legacyListenerFromContext = getLegacyListenerFromContext(view.getContext());
        if (legacyListenerFromContext == null) {
            return z;
        }
        legacyListenerFromContext.onClick(this);
        return true;
    }

    @Nullable
    @Deprecated
    private OnClickListener getLegacyListenerFromContext(@Nullable Context context) {
        Context context2 = context;
        while (!(context2 instanceof OnClickListener)) {
            if (!(context2 instanceof ContextWrapper)) {
                return null;
            }
            context2 = ((ContextWrapper) context2).getBaseContext();
        }
        return (OnClickListener) context2;
    }

    public String getId() {
        return this.mId;
    }

    public void onClick(View view) {
        if (!dispatchClick(view)) {
            Log.w(TAG, "Dropping click event. No listener attached.");
        } else if (VERSION.SDK_INT >= 19) {
            view.cancelPendingInputEvents();
        }
        if (view instanceof TextView) {
            CharSequence text = ((TextView) view).getText();
            if (text instanceof Spannable) {
                Selection.setSelection((Spannable) text, 0);
            }
        }
    }

    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setUnderlineText(false);
        textPaint.setTypeface(TYPEFACE_MEDIUM);
    }
}
