package com.android.setupwizardlib.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.android.setupwizardlib.R;

public class Illustration extends FrameLayout {
    private float mAspectRatio = 0.0f;
    private Drawable mBackground;
    private float mBaselineGridSize;
    private Drawable mIllustration;
    private final Rect mIllustrationBounds = new Rect();
    private float mScale = 1.0f;
    private final Rect mViewBounds = new Rect();

    public Illustration(Context context) {
        super(context);
        init(null, 0);
    }

    public Illustration(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, 0);
    }

    @TargetApi(11)
    public Illustration(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SuwIllustration, i, 0);
            this.mAspectRatio = obtainStyledAttributes.getFloat(R.styleable.SuwIllustration_suwAspectRatio, 0.0f);
            obtainStyledAttributes.recycle();
        }
        this.mBaselineGridSize = getResources().getDisplayMetrics().density * 8.0f;
        setWillNotDraw(false);
    }

    private boolean shouldMirrorDrawable(Drawable drawable, int i) {
        return i == 1 ? VERSION.SDK_INT >= 19 ? drawable.isAutoMirrored() : VERSION.SDK_INT >= 17 && (4194304 & getContext().getApplicationInfo().flags) != 0 : false;
    }

    public void onDraw(Canvas canvas) {
        if (this.mBackground != null) {
            canvas.save();
            canvas.translate(0.0f, (float) this.mIllustrationBounds.height());
            canvas.scale(this.mScale, this.mScale, 0.0f, 0.0f);
            if (VERSION.SDK_INT > 17 && shouldMirrorDrawable(this.mBackground, getLayoutDirection())) {
                canvas.scale(-1.0f, 1.0f);
                canvas.translate((float) (-this.mBackground.getBounds().width()), 0.0f);
            }
            this.mBackground.draw(canvas);
            canvas.restore();
        }
        if (this.mIllustration != null) {
            canvas.save();
            if (VERSION.SDK_INT > 17 && shouldMirrorDrawable(this.mIllustration, getLayoutDirection())) {
                canvas.scale(-1.0f, 1.0f);
                canvas.translate((float) (-this.mIllustrationBounds.width()), 0.0f);
            }
            this.mIllustration.draw(canvas);
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        if (this.mIllustration != null) {
            int intrinsicWidth = this.mIllustration.getIntrinsicWidth();
            int intrinsicHeight = this.mIllustration.getIntrinsicHeight();
            this.mViewBounds.set(0, 0, i5, i6);
            if (this.mAspectRatio != 0.0f) {
                this.mScale = ((float) i5) / ((float) intrinsicWidth);
                intrinsicHeight = (int) (((float) intrinsicHeight) * this.mScale);
                intrinsicWidth = i5;
            }
            Gravity.apply(55, intrinsicWidth, intrinsicHeight, this.mViewBounds, this.mIllustrationBounds);
            this.mIllustration.setBounds(this.mIllustrationBounds);
        }
        if (this.mBackground != null) {
            this.mBackground.setBounds(0, 0, (int) Math.ceil((double) (((float) i5) / this.mScale)), (int) Math.ceil((double) (((float) (i6 - this.mIllustrationBounds.height())) / this.mScale)));
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        if (this.mAspectRatio != 0.0f) {
            int size = (int) (((float) MeasureSpec.getSize(i)) / this.mAspectRatio);
            setPadding(0, (int) (((float) size) - (((float) size) % this.mBaselineGridSize)), 0, 0);
        }
        if (VERSION.SDK_INT >= 21) {
            setOutlineProvider(ViewOutlineProvider.BOUNDS);
        }
        super.onMeasure(i, i2);
    }

    public void setAspectRatio(float f) {
        this.mAspectRatio = f;
        invalidate();
        requestLayout();
    }

    public void setBackgroundDrawable(Drawable drawable) {
        if (drawable != this.mBackground) {
            this.mBackground = drawable;
            invalidate();
            requestLayout();
        }
    }

    @Deprecated
    public void setForeground(Drawable drawable) {
        setIllustration(drawable);
    }

    public void setIllustration(Drawable drawable) {
        if (drawable != this.mIllustration) {
            this.mIllustration = drawable;
            invalidate();
            requestLayout();
        }
    }
}
