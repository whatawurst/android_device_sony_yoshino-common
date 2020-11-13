package com.android.setupwizardlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import com.android.setupwizardlib.R;

public class FillContentLayout extends FrameLayout {
    private int mMaxHeight;
    private int mMaxWidth;

    public FillContentLayout(Context context) {
        this(context, null);
    }

    public FillContentLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.suwFillContentLayoutStyle);
    }

    public FillContentLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i);
    }

    private static int getMaxSizeMeasureSpec(int i, int i2, int i3) {
        int max = Math.max(0, i - i2);
        return i3 >= 0 ? MeasureSpec.makeMeasureSpec(i3, 1073741824) : i3 == -1 ? MeasureSpec.makeMeasureSpec(max, 1073741824) : i3 == -2 ? MeasureSpec.makeMeasureSpec(max, Integer.MIN_VALUE) : 0;
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwFillContentLayout, i, 0);
        this.mMaxHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwFillContentLayout_android_maxHeight, -1);
        this.mMaxWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwFillContentLayout_android_maxWidth, -1);
        obtainStyledAttributes.recycle();
    }

    private void measureIllustrationChild(View view, int i, int i2) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        view.measure(getMaxSizeMeasureSpec(Math.min(this.mMaxWidth, i), ((getPaddingLeft() + getPaddingRight()) + marginLayoutParams.leftMargin) + marginLayoutParams.rightMargin, marginLayoutParams.width), getMaxSizeMeasureSpec(Math.min(this.mMaxHeight, i2), ((getPaddingTop() + getPaddingBottom()) + marginLayoutParams.topMargin) + marginLayoutParams.bottomMargin, marginLayoutParams.height));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), i), getDefaultSize(getSuggestedMinimumHeight(), i2));
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            measureIllustrationChild(getChildAt(i3), getMeasuredWidth(), getMeasuredHeight());
        }
    }
}
