package com.android.setupwizardlib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.setupwizardlib.template.HeaderMixin;
import com.android.setupwizardlib.template.NavigationBarMixin;
import com.android.setupwizardlib.template.ProgressBarMixin;
import com.android.setupwizardlib.template.RequireScrollMixin;
import com.android.setupwizardlib.template.ScrollViewScrollHandlingDelegate;
import com.android.setupwizardlib.view.Illustration;
import com.android.setupwizardlib.view.NavigationBar;

public class SetupWizardLayout extends TemplateLayout {
    private static final String TAG = "SetupWizardLayout";

    protected static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean mIsProgressBarShown = false;

        public SavedState(Parcel parcel) {
            boolean z = false;
            super(parcel);
            if (parcel.readInt() != 0) {
                z = true;
            }
            this.mIsProgressBarShown = z;
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mIsProgressBarShown);
        }
    }

    public SetupWizardLayout(Context context) {
        super(context, 0, 0);
        init(null, R.attr.suwLayoutTheme);
    }

    public SetupWizardLayout(Context context, int i) {
        this(context, i, 0);
    }

    public SetupWizardLayout(Context context, int i, int i2) {
        super(context, i, i2);
        init(null, R.attr.suwLayoutTheme);
    }

    public SetupWizardLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, R.attr.suwLayoutTheme);
    }

    @TargetApi(11)
    public SetupWizardLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private Drawable getIllustration(int i, int i2) {
        Context context = getContext();
        return getIllustration(context.getResources().getDrawable(i), context.getResources().getDrawable(i2));
    }

    @SuppressLint({"RtlHardcoded"})
    private Drawable getIllustration(Drawable drawable, Drawable drawable2) {
        if (getContext().getResources().getBoolean(R.bool.suwUseTabletLayout)) {
            if (drawable2 instanceof BitmapDrawable) {
                ((BitmapDrawable) drawable2).setTileModeX(TileMode.REPEAT);
                ((BitmapDrawable) drawable2).setGravity(48);
            }
            if (drawable instanceof BitmapDrawable) {
                ((BitmapDrawable) drawable).setGravity(51);
            }
            Drawable layerDrawable = new LayerDrawable(new Drawable[]{drawable2, drawable});
            if (VERSION.SDK_INT >= 19) {
                layerDrawable.setAutoMirrored(true);
            }
            return layerDrawable;
        } else if (VERSION.SDK_INT < 19) {
            return drawable;
        } else {
            drawable.setAutoMirrored(true);
            return drawable;
        }
    }

    private void init(AttributeSet attributeSet, int i) {
        registerMixin(HeaderMixin.class, new HeaderMixin(this, attributeSet, i));
        registerMixin(ProgressBarMixin.class, new ProgressBarMixin(this));
        registerMixin(NavigationBarMixin.class, new NavigationBarMixin(this));
        RequireScrollMixin requireScrollMixin = new RequireScrollMixin(this);
        registerMixin(RequireScrollMixin.class, requireScrollMixin);
        ScrollView scrollView = getScrollView();
        if (scrollView != null) {
            requireScrollMixin.setScrollHandlingDelegate(new ScrollViewScrollHandlingDelegate(requireScrollMixin, scrollView));
        }
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.SuwSetupWizardLayout, i, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.SuwSetupWizardLayout_suwBackground);
        if (drawable != null) {
            setLayoutBackground(drawable);
        } else {
            drawable = obtainStyledAttributes.getDrawable(R.styleable.SuwSetupWizardLayout_suwBackgroundTile);
            if (drawable != null) {
                setBackgroundTile(drawable);
            }
        }
        drawable = obtainStyledAttributes.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustration);
        if (drawable != null) {
            setIllustration(drawable);
        } else {
            drawable = obtainStyledAttributes.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustrationImage);
            Drawable drawable2 = obtainStyledAttributes.getDrawable(R.styleable.SuwSetupWizardLayout_suwIllustrationHorizontalTile);
            if (!(drawable == null || drawable2 == null)) {
                setIllustration(drawable, drawable2);
            }
        }
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwSetupWizardLayout_suwDecorPaddingTop, -1);
        if (dimensionPixelSize == -1) {
            dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.suw_decor_padding_top);
        }
        setDecorPaddingTop(dimensionPixelSize);
        float f = obtainStyledAttributes.getFloat(R.styleable.SuwSetupWizardLayout_suwIllustrationAspectRatio, -1.0f);
        if (f == -1.0f) {
            TypedValue typedValue = new TypedValue();
            getResources().getValue(R.dimen.suw_illustration_aspect_ratio, typedValue, true);
            f = typedValue.getFloat();
        }
        setIllustrationAspectRatio(f);
        obtainStyledAttributes.recycle();
    }

    private void setBackgroundTile(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            ((BitmapDrawable) drawable).setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        }
        setLayoutBackground(drawable);
    }

    private void setIllustration(Drawable drawable, Drawable drawable2) {
        View findManagedViewById = findManagedViewById(R.id.suw_layout_decor);
        if (findManagedViewById instanceof Illustration) {
            ((Illustration) findManagedViewById).setIllustration(getIllustration(drawable, drawable2));
        }
    }

    /* Access modifiers changed, original: protected */
    public ViewGroup findContainer(int i) {
        if (i == 0) {
            i = R.id.suw_layout_content;
        }
        return super.findContainer(i);
    }

    public CharSequence getHeaderText() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getText();
    }

    public TextView getHeaderTextView() {
        return ((HeaderMixin) getMixin(HeaderMixin.class)).getTextView();
    }

    public NavigationBar getNavigationBar() {
        return ((NavigationBarMixin) getMixin(NavigationBarMixin.class)).getNavigationBar();
    }

    public ColorStateList getProgressBarColor() {
        return ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).getColor();
    }

    public ScrollView getScrollView() {
        View findManagedViewById = findManagedViewById(R.id.suw_bottom_scroll_view);
        return findManagedViewById instanceof ScrollView ? (ScrollView) findManagedViewById : null;
    }

    @Deprecated
    public void hideProgressBar() {
        setProgressBarShown(false);
    }

    public boolean isProgressBarShown() {
        return ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).isShown();
    }

    /* Access modifiers changed, original: protected */
    public View onInflateTemplate(LayoutInflater layoutInflater, int i) {
        if (i == 0) {
            i = R.layout.suw_template;
        }
        return inflateTemplate(layoutInflater, R.style.SuwThemeMaterial_Light, i);
    }

    /* Access modifiers changed, original: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            setProgressBarShown(savedState.mIsProgressBarShown);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ignoring restore instance state ");
        stringBuilder.append(parcelable);
        Log.w(TAG, stringBuilder.toString());
        super.onRestoreInstanceState(parcelable);
    }

    /* Access modifiers changed, original: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mIsProgressBarShown = isProgressBarShown();
        return savedState;
    }

    public void requireScrollToBottom() {
        RequireScrollMixin requireScrollMixin = (RequireScrollMixin) getMixin(RequireScrollMixin.class);
        NavigationBar navigationBar = getNavigationBar();
        if (navigationBar != null) {
            requireScrollMixin.requireScrollWithNavigationBar(navigationBar);
        } else {
            Log.e(TAG, "Cannot require scroll. Navigation bar is null.");
        }
    }

    public void setBackgroundTile(int i) {
        setBackgroundTile(getContext().getResources().getDrawable(i));
    }

    public void setDecorPaddingTop(int i) {
        View findManagedViewById = findManagedViewById(R.id.suw_layout_decor);
        if (findManagedViewById != null) {
            findManagedViewById.setPadding(findManagedViewById.getPaddingLeft(), i, findManagedViewById.getPaddingRight(), findManagedViewById.getPaddingBottom());
        }
    }

    public void setHeaderText(int i) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(i);
    }

    public void setHeaderText(CharSequence charSequence) {
        ((HeaderMixin) getMixin(HeaderMixin.class)).setText(charSequence);
    }

    public void setIllustration(int i, int i2) {
        View findManagedViewById = findManagedViewById(R.id.suw_layout_decor);
        if (findManagedViewById instanceof Illustration) {
            ((Illustration) findManagedViewById).setIllustration(getIllustration(i, i2));
        }
    }

    public void setIllustration(Drawable drawable) {
        View findManagedViewById = findManagedViewById(R.id.suw_layout_decor);
        if (findManagedViewById instanceof Illustration) {
            ((Illustration) findManagedViewById).setIllustration(drawable);
        }
    }

    public void setIllustrationAspectRatio(float f) {
        View findManagedViewById = findManagedViewById(R.id.suw_layout_decor);
        if (findManagedViewById instanceof Illustration) {
            ((Illustration) findManagedViewById).setAspectRatio(f);
        }
    }

    public void setLayoutBackground(Drawable drawable) {
        View findManagedViewById = findManagedViewById(R.id.suw_layout_decor);
        if (findManagedViewById != null) {
            findManagedViewById.setBackgroundDrawable(drawable);
        }
    }

    public void setProgressBarColor(ColorStateList colorStateList) {
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setColor(colorStateList);
    }

    public void setProgressBarShown(boolean z) {
        ((ProgressBarMixin) getMixin(ProgressBarMixin.class)).setShown(z);
    }

    @Deprecated
    public void showProgressBar() {
        setProgressBarShown(true);
    }
}
