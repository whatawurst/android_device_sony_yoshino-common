package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private boolean mChecked = false;

    public CheckableLinearLayout(Context context) {
        super(context);
        setFocusable(true);
    }

    public CheckableLinearLayout(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        setFocusable(true);
    }

    @TargetApi(11)
    public CheckableLinearLayout(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setFocusable(true);
    }

    @TargetApi(21)
    public CheckableLinearLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setFocusable(true);
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    /* Access modifiers changed, original: protected */
    public int[] onCreateDrawableState(int i) {
        if (!this.mChecked) {
            return super.onCreateDrawableState(i);
        }
        return mergeDrawableStates(super.onCreateDrawableState(i + 1), new int[]{16842912});
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        refreshDrawableState();
    }

    public void toggle() {
        setChecked(isChecked() ^ 1);
    }
}
