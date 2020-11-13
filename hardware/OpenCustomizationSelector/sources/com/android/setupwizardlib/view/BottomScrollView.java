package com.android.setupwizardlib.view;

import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class BottomScrollView extends ScrollView {
    private final Runnable mCheckScrollRunnable = new Runnable() {
        public void run() {
            BottomScrollView.this.checkScroll();
        }
    };
    private BottomScrollListener mListener;
    private boolean mRequiringScroll = false;
    private int mScrollThreshold;

    public interface BottomScrollListener {
        void onRequiresScroll();

        void onScrolledToBottom();
    }

    public BottomScrollView(Context context) {
        super(context);
    }

    public BottomScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BottomScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void checkScroll() {
        if (this.mListener == null) {
            return;
        }
        if (getScrollY() >= this.mScrollThreshold) {
            this.mListener.onScrolledToBottom();
        } else if (!this.mRequiringScroll) {
            this.mRequiringScroll = true;
            this.mListener.onRequiresScroll();
        }
    }

    @VisibleForTesting
    public int getScrollThreshold() {
        return this.mScrollThreshold;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        View childAt = getChildAt(0);
        if (childAt != null) {
            this.mScrollThreshold = Math.max(0, ((childAt.getMeasuredHeight() - i4) + i2) - getPaddingBottom());
        }
        if (i4 - i2 > 0) {
            post(this.mCheckScrollRunnable);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (i4 != i2) {
            checkScroll();
        }
    }

    public void setBottomScrollListener(BottomScrollListener bottomScrollListener) {
        this.mListener = bottomScrollListener;
    }
}
