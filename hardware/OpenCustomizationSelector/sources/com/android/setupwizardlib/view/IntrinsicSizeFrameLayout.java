package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.android.setupwizardlib.R;

public class IntrinsicSizeFrameLayout extends FrameLayout {
    private int mIntrinsicHeight = 0;
    private int mIntrinsicWidth = 0;

    public IntrinsicSizeFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public IntrinsicSizeFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet, 0);
    }

    @TargetApi(11)
    public IntrinsicSizeFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i);
    }

    private int getIntrinsicMeasureSpec(int i, int i2) {
        if (i2 <= 0) {
            return i;
        }
        int mode = MeasureSpec.getMode(i);
        return mode == 0 ? MeasureSpec.makeMeasureSpec(this.mIntrinsicHeight, 1073741824) : mode == Integer.MIN_VALUE ? MeasureSpec.makeMeasureSpec(Math.min(MeasureSpec.getSize(i), this.mIntrinsicHeight), 1073741824) : i;
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwIntrinsicSizeFrameLayout, i, 0);
        this.mIntrinsicHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwIntrinsicSizeFrameLayout_android_height, 0);
        this.mIntrinsicWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwIntrinsicSizeFrameLayout_android_width, 0);
        obtainStyledAttributes.recycle();
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(getIntrinsicMeasureSpec(i, this.mIntrinsicWidth), getIntrinsicMeasureSpec(i2, this.mIntrinsicHeight));
    }
}
