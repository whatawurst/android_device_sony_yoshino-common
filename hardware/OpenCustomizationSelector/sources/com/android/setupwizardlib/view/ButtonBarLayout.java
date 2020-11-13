package com.android.setupwizardlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.setupwizardlib.R;

public class ButtonBarLayout extends LinearLayout {
    private int mOriginalPaddingLeft;
    private int mOriginalPaddingRight;
    private boolean mStacked = false;

    public ButtonBarLayout(Context context) {
        super(context);
    }

    public ButtonBarLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void setStacked(boolean z) {
        if (this.mStacked != z) {
            int i;
            this.mStacked = z;
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = getChildAt(i2);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (z) {
                    childAt.setTag(R.id.suw_original_weight, Float.valueOf(layoutParams.weight));
                    layoutParams.weight = 0.0f;
                } else {
                    Float f = (Float) childAt.getTag(R.id.suw_original_weight);
                    if (f != null) {
                        layoutParams.weight = f.floatValue();
                    }
                }
                childAt.setLayoutParams(layoutParams);
            }
            setOrientation(z);
            for (i = childCount - 1; i >= 0; i--) {
                bringChildToFront(getChildAt(i));
            }
            if (z) {
                this.mOriginalPaddingLeft = getPaddingLeft();
                this.mOriginalPaddingRight = getPaddingRight();
                i = Math.max(this.mOriginalPaddingLeft, this.mOriginalPaddingRight);
                setPadding(i, getPaddingTop(), i, getPaddingBottom());
                return;
            }
            setPadding(this.mOriginalPaddingLeft, getPaddingTop(), this.mOriginalPaddingRight, getPaddingBottom());
        }
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        int makeMeasureSpec;
        boolean z = true;
        boolean z2 = false;
        int size = MeasureSpec.getSize(i);
        setStacked(false);
        if (MeasureSpec.getMode(i) == 1073741824) {
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
            z2 = true;
        } else {
            makeMeasureSpec = i;
        }
        super.onMeasure(makeMeasureSpec, i2);
        if (getMeasuredWidth() > size) {
            setStacked(true);
        } else {
            z = z2;
        }
        if (z) {
            super.onMeasure(i, i2);
        }
    }
}
