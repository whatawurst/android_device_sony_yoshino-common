package com.android.setupwizardlib.template;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ScrollView;
import com.android.setupwizardlib.view.BottomScrollView;
import com.android.setupwizardlib.view.BottomScrollView.BottomScrollListener;

public class ScrollViewScrollHandlingDelegate implements ScrollHandlingDelegate, BottomScrollListener {
    private static final String TAG = "ScrollViewDelegate";
    @NonNull
    private final RequireScrollMixin mRequireScrollMixin;
    @Nullable
    private final BottomScrollView mScrollView;

    public ScrollViewScrollHandlingDelegate(@NonNull RequireScrollMixin requireScrollMixin, @Nullable ScrollView scrollView) {
        this.mRequireScrollMixin = requireScrollMixin;
        if (scrollView instanceof BottomScrollView) {
            this.mScrollView = (BottomScrollView) scrollView;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot set non-BottomScrollView. Found=");
        stringBuilder.append(scrollView);
        Log.w(TAG, stringBuilder.toString());
        this.mScrollView = null;
    }

    public void onRequiresScroll() {
        this.mRequireScrollMixin.notifyScrollabilityChange(true);
    }

    public void onScrolledToBottom() {
        this.mRequireScrollMixin.notifyScrollabilityChange(false);
    }

    public void pageScrollDown() {
        if (this.mScrollView != null) {
            this.mScrollView.pageScroll(130);
        }
    }

    public void startListening() {
        if (this.mScrollView != null) {
            this.mScrollView.setBottomScrollListener(this);
        } else {
            Log.w(TAG, "Cannot require scroll. Scroll view is null.");
        }
    }
}
