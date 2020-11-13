package com.android.setupwizardlib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build.VERSION;
import android.view.View;

public class DrawableLayoutDirectionHelper {
    @SuppressLint({"InlinedApi"})
    public static InsetDrawable createRelativeInsetDrawable(Drawable drawable, int i, int i2, int i3, int i4, int i5) {
        boolean z = true;
        if (i5 != 1) {
            z = false;
        }
        return createRelativeInsetDrawable(drawable, i, i2, i3, i4, z);
    }

    @SuppressLint({"InlinedApi"})
    public static InsetDrawable createRelativeInsetDrawable(Drawable drawable, int i, int i2, int i3, int i4, Context context) {
        boolean z = false;
        if (VERSION.SDK_INT >= 17 && context.getResources().getConfiguration().getLayoutDirection() == 1) {
            z = true;
        }
        return createRelativeInsetDrawable(drawable, i, i2, i3, i4, z);
    }

    @SuppressLint({"InlinedApi"})
    public static InsetDrawable createRelativeInsetDrawable(Drawable drawable, int i, int i2, int i3, int i4, View view) {
        boolean z = true;
        if (VERSION.SDK_INT < 17 || view.getLayoutDirection() != 1) {
            z = false;
        }
        return createRelativeInsetDrawable(drawable, i, i2, i3, i4, z);
    }

    private static InsetDrawable createRelativeInsetDrawable(Drawable drawable, int i, int i2, int i3, int i4, boolean z) {
        return z ? new InsetDrawable(drawable, i3, i2, i, i4) : new InsetDrawable(drawable, i, i2, i3, i4);
    }
}
