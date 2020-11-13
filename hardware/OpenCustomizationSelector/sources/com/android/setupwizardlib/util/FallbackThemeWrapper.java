package com.android.setupwizardlib.util;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.support.annotation.StyleRes;
import android.view.ContextThemeWrapper;

public class FallbackThemeWrapper extends ContextThemeWrapper {
    public FallbackThemeWrapper(Context context, @StyleRes int i) {
        super(context, i);
    }

    /* Access modifiers changed, original: protected */
    public void onApplyThemeResource(Theme theme, int i, boolean z) {
        theme.applyStyle(i, false);
    }
}
