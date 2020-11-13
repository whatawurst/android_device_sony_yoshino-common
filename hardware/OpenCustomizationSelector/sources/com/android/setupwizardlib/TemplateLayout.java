package com.android.setupwizardlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Keep;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import com.android.setupwizardlib.template.Mixin;
import com.android.setupwizardlib.util.FallbackThemeWrapper;
import java.util.HashMap;
import java.util.Map;

public class TemplateLayout extends FrameLayout {
    private ViewGroup mContainer;
    private Map<Class<? extends Mixin>, Mixin> mMixins = new HashMap();
    private OnPreDrawListener mPreDrawListener;
    private float mXFraction;

    public TemplateLayout(Context context, int i, int i2) {
        super(context);
        init(i, i2, null, R.attr.suwLayoutTheme);
    }

    public TemplateLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(0, 0, attributeSet, R.attr.suwLayoutTheme);
    }

    @TargetApi(11)
    public TemplateLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(0, 0, attributeSet, i);
    }

    private void addViewInternal(View view) {
        super.addView(view, -1, generateDefaultLayoutParams());
    }

    private void inflateTemplate(int i, int i2) {
        addViewInternal(onInflateTemplate(LayoutInflater.from(getContext()), i));
        this.mContainer = findContainer(i2);
        if (this.mContainer != null) {
            onTemplateInflated();
            return;
        }
        throw new IllegalArgumentException("Container cannot be null in TemplateLayout");
    }

    private void init(int i, int i2, AttributeSet attributeSet, int i3) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SuwTemplateLayout, i3, 0);
        if (i == 0) {
            i = obtainStyledAttributes.getResourceId(R.styleable.SuwTemplateLayout_android_layout, 0);
        }
        if (i2 == 0) {
            i2 = obtainStyledAttributes.getResourceId(R.styleable.SuwTemplateLayout_suwContainer, 0);
        }
        inflateTemplate(i, i2);
        obtainStyledAttributes.recycle();
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        this.mContainer.addView(view, i, layoutParams);
    }

    /* Access modifiers changed, original: protected */
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = getContainerId();
        }
        return (ViewGroup) findViewById(i);
    }

    public <T extends View> T findManagedViewById(int i) {
        return findViewById(i);
    }

    /* Access modifiers changed, original: protected */
    @Deprecated
    public int getContainerId() {
        return 0;
    }

    public <M extends Mixin> M getMixin(Class<M> cls) {
        return (Mixin) this.mMixins.get(cls);
    }

    @Keep
    @TargetApi(11)
    public float getXFraction() {
        return this.mXFraction;
    }

    /* Access modifiers changed, original: protected|final */
    public final View inflateTemplate(LayoutInflater layoutInflater, @StyleRes int i, @LayoutRes int i2) {
        if (i2 != 0) {
            if (i != 0) {
                layoutInflater = LayoutInflater.from(new FallbackThemeWrapper(layoutInflater.getContext(), i));
            }
            return layoutInflater.inflate(i2, this, false);
        }
        throw new IllegalArgumentException("android:layout not specified for TemplateLayout");
    }

    /* Access modifiers changed, original: protected */
    public View onInflateTemplate(LayoutInflater layoutInflater, @LayoutRes int i) {
        return inflateTemplate(layoutInflater, 0, i);
    }

    /* Access modifiers changed, original: protected */
    public void onTemplateInflated() {
    }

    /* Access modifiers changed, original: protected */
    public <M extends Mixin> void registerMixin(Class<M> cls, M m) {
        this.mMixins.put(cls, m);
    }

    @Keep
    @TargetApi(11)
    public void setXFraction(float f) {
        this.mXFraction = f;
        int width = getWidth();
        if (width != 0) {
            setTranslationX(((float) width) * f);
        } else if (this.mPreDrawListener == null) {
            this.mPreDrawListener = new OnPreDrawListener() {
                public boolean onPreDraw() {
                    TemplateLayout.this.getViewTreeObserver().removeOnPreDrawListener(TemplateLayout.this.mPreDrawListener);
                    TemplateLayout.this.setXFraction(TemplateLayout.this.mXFraction);
                    return true;
                }
            };
            getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
        }
    }
}
