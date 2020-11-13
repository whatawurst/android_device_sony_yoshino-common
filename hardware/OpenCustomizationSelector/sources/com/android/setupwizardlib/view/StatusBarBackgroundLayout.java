package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import com.android.setupwizardlib.R;

public class StatusBarBackgroundLayout extends FrameLayout {
    private Object mLastInsets;
    private Drawable mStatusBarBackground;

    public StatusBarBackgroundLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public StatusBarBackgroundLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet, 0);
    }

    @TargetApi(11)
    public StatusBarBackgroundLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwStatusBarBackgroundLayout, i, 0);
        setStatusBarBackground(obtainStyledAttributes.getDrawable(R.styleable.SuwStatusBarBackgroundLayout_suwStatusBarBackground));
        obtainStyledAttributes.recycle();
    }

    public Drawable getStatusBarBackground() {
        return this.mStatusBarBackground;
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        this.mLastInsets = windowInsets;
        return super.onApplyWindowInsets(windowInsets);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (VERSION.SDK_INT >= 21 && this.mLastInsets == null) {
            requestApplyInsets();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (VERSION.SDK_INT >= 21 && this.mLastInsets != null) {
            int systemWindowInsetTop = ((WindowInsets) this.mLastInsets).getSystemWindowInsetTop();
            if (systemWindowInsetTop > 0) {
                this.mStatusBarBackground.setBounds(0, 0, getWidth(), systemWindowInsetTop);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }

    public void setStatusBarBackground(Drawable drawable) {
        boolean z = true;
        this.mStatusBarBackground = drawable;
        if (VERSION.SDK_INT >= 21) {
            setWillNotDraw(drawable == null);
            if (drawable == null) {
                z = false;
            }
            setFitsSystemWindows(z);
            invalidate();
        }
    }
}
