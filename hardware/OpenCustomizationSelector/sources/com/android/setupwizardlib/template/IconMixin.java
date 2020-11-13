package com.android.setupwizardlib.template;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.TemplateLayout;

public class IconMixin implements Mixin {
    private TemplateLayout mTemplateLayout;

    public IconMixin(TemplateLayout templateLayout, AttributeSet attributeSet, int i) {
        this.mTemplateLayout = templateLayout;
        TypedArray obtainStyledAttributes = templateLayout.getContext().obtainStyledAttributes(attributeSet, R.styleable.SuwIconMixin, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.SuwIconMixin_android_icon, 0);
        if (resourceId != 0) {
            setIcon(resourceId);
        }
        obtainStyledAttributes.recycle();
    }

    public CharSequence getContentDescription() {
        ImageView view = getView();
        return view != null ? view.getContentDescription() : null;
    }

    public Drawable getIcon() {
        ImageView view = getView();
        return view != null ? view.getDrawable() : null;
    }

    /* Access modifiers changed, original: protected */
    public ImageView getView() {
        return (ImageView) this.mTemplateLayout.findManagedViewById(R.id.suw_layout_icon);
    }

    public void setContentDescription(CharSequence charSequence) {
        ImageView view = getView();
        if (view != null) {
            view.setContentDescription(charSequence);
        }
    }

    public void setIcon(@DrawableRes int i) {
        ImageView view = getView();
        if (view != null) {
            view.setImageResource(i);
            view.setVisibility(i != 0 ? 0 : 8);
        }
    }

    public void setIcon(Drawable drawable) {
        ImageView view = getView();
        if (view != null) {
            view.setImageDrawable(drawable);
            view.setVisibility(drawable != null ? 0 : 8);
        }
    }
}
