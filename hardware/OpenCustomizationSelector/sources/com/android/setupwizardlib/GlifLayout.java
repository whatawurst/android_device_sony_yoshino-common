package com.android.setupwizardlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.setupwizardlib.template.ButtonFooterMixin;
import com.android.setupwizardlib.template.ColoredHeaderMixin;
import com.android.setupwizardlib.template.HeaderMixin;
import com.android.setupwizardlib.template.IconMixin;
import com.android.setupwizardlib.template.ProgressBarMixin;
import com.android.setupwizardlib.template.RequireScrollMixin;
import com.android.setupwizardlib.template.ScrollViewScrollHandlingDelegate;
import com.android.setupwizardlib.view.StatusBarBackgroundLayout;

public class GlifLayout extends TemplateLayout {
    private static final String TAG = "GlifLayout";
    @Nullable
    private ColorStateList mBackgroundBaseColor;
    private boolean mBackgroundPatterned;
    private boolean mLayoutFullscreen;
    private ColorStateList mPrimaryColor;

    public GlifLayout(Context context) {
        this(context, 0, 0);
    }

    public GlifLayout(Context context, int i) {
        this(context, i, 0);
    }

    public GlifLayout(Context context, int i, int i2) {
        super(context, i, i2);
        this.mBackgroundPatterned = true;
        this.mLayoutFullscreen = true;
        init(null, R.attr.suwLayoutTheme);
    }

    public GlifLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBackgroundPatterned = true;
        this.mLayoutFullscreen = true;
        init(attributeSet, R.attr.suwLayoutTheme);
    }

    @TargetApi(11)
    public GlifLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBackgroundPatterned = true;
        this.mLayoutFullscreen = true;
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        registerMixin(HeaderMixin.class, new ColoredHeaderMixin(this, attributeSet, i));
        registerMixin(IconMixin.class, new IconMixin(this, attributeSet, i));
        registerMixin(ProgressBarMixin.class, new ProgressBarMixin(this));
        registerMixin(ButtonFooterMixin.class, new ButtonFooterMixin(this));
        RequireScrollMixin requireScrollMixin = new RequireScrollMixin(this);
        registerMixin(RequireScrollMixin.class, requireScrollMixin);
        ScrollView scrollView = getScrollView();
        if (scrollView != null) {
            requireScrollMixin.setScrollHandlingDelegate(new ScrollViewScrollHandlingDelegate(requireScrollMixin, scrollView));
        }
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SuwGlifLayout, i, 0);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R.styleable.SuwGlifLayout_suwColorPrimary);
        if (colorStateList != null) {
            setPrimaryColor(colorStateList);
        }
        setBackgroundBaseColor(obtainStyledAttributes.getColorStateList(R.styleable.SuwGlifLayout_suwBackgroundBaseColor));
        setBackgroundPatterned(obtainStyledAttributes.getBoolean(R.styleable.SuwGlifLayout_suwBackgroundPatterned, true));
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.SuwGlifLayout_suwFooter, 0);
        if (resourceId != 0) {
            inflateFooter(resourceId);
        }
        resourceId = obtainStyledAttributes.getResourceId(R.styleable.SuwGlifLayout_suwStickyHeader, 0);
        if (resourceId != 0) {
            inflateStickyHeader(resourceId);
        }
        this.mLayoutFullscreen = obtainStyledAttributes.getBoolean(R.styleable.SuwGlifLayout_suwLayoutFullscreen, true);
        obtainStyledAttributes.recycle();
        if (VERSION.SDK_INT >= 21 && this.mLayoutFullscreen) {
            setSystemUiVisibility(1024);
        }
    }

    private void updateBackground() {
        View findManagedViewById = findManagedViewById(R.id.suw_pattern_bg);
        if (findManagedViewById != null) {
            int i = 0;
            if (this.mBackgroundBaseColor != null) {
                i = this.mBackgroundBaseColor.getDefaultColor();
            } else if (this.mPrimaryColor != null) {
                i = this.mPrimaryColor.getDefaultColor();
            }
            Drawable glifPatternDrawable = this.mBackgroundPatterned ? new GlifPatternDrawable(i) : new ColorDrawable(i);
            if (findManagedViewById instanceof StatusBarBackgroundLayout) {
                ((StatusBarBackgroundLayout) findManagedViewById).setStatusBarBackground(glifPatternDrawable);
            } else {
                findManagedViewById.setBackgroundDrawable(glifPatternDrawable);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R.id.suw_layout_content;
        }
        return super.findContainer(i);
    }

    @Nullable
    public ColorStateList getBackgroundBaseColor() {
        return this.mBackgroundBaseColor;
    }

    public ColorStateList getHeaderColor() {
        return ((ColoredHeaderMixin) getMixin(HeaderMixin.class)).getColor();
    }

    public CharSequence getHeaderText() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getText();
    }

    public TextView getHeaderTextView() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getTextView();
    }

    public Drawable getIcon() {
        return ((IconMixin) getMixin(IconMixin.class)).getIcon();
    }

    public ColorStateList getPrimaryColor() {
        return this.mPrimaryColor;
    }

    public ScrollView getScrollView() {
        View findManagedViewById = findManagedViewById(R.id.suw_scroll_view);
        return findManagedViewById instanceof ScrollView ? (ScrollView) findManagedViewById : null;
    }

    public View inflateFooter(@LayoutRes int i) {
        ViewStub viewStub = (ViewStub) findManagedViewById(R.id.suw_layout_footer);
        viewStub.setLayoutResource(i);
        return viewStub.inflate();
    }

    public View inflateStickyHeader(@LayoutRes int i) {
        ViewStub viewStub = (ViewStub) findManagedViewById(R.id.suw_layout_sticky_header);
        viewStub.setLayoutResource(i);
        return viewStub.inflate();
    }

    public boolean isBackgroundPatterned() {
        return this.mBackgroundPatterned;
    }

    public boolean isProgressBarShown() {
        return ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).isShown();
    }

    /* Access modifiers changed, original: protected */
    public View onInflateTemplate(LayoutInflater layoutInflater, @LayoutRes int i) {
        if (i == 0) {
            i = R.layout.suw_glif_template;
        }
        return inflateTemplate(layoutInflater, R.style.SuwThemeGlif_Light, i);
    }

    public ProgressBar peekProgressBar() {
        return ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).peekProgressBar();
    }

    public void setBackgroundBaseColor(@Nullable ColorStateList colorStateList) {
        this.mBackgroundBaseColor = colorStateList;
        updateBackground();
    }

    public void setBackgroundPatterned(boolean z) {
        this.mBackgroundPatterned = z;
        updateBackground();
    }

    public void setHeaderColor(ColorStateList colorStateList) {
        ((ColoredHeaderMixin) getMixin(HeaderMixin.class)).setColor(colorStateList);
    }

    public void setHeaderText(int i) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(i);
    }

    public void setHeaderText(CharSequence charSequence) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(charSequence);
    }

    public void setIcon(Drawable drawable) {
        ((IconMixin) getMixin(IconMixin.class)).setIcon(drawable);
    }

    public void setPrimaryColor(@NonNull ColorStateList colorStateList) {
        this.mPrimaryColor = colorStateList;
        updateBackground();
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setColor(colorStateList);
    }

    public void setProgressBarShown(boolean z) {
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setShown(z);
    }
}
