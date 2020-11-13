package com.android.setupwizardlib.template;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.TemplateLayout;

public class ColoredHeaderMixin extends HeaderMixin {
    public ColoredHeaderMixin(TemplateLayout templateLayout, AttributeSet attributeSet, int i) {
        super(templateLayout, attributeSet, i);
        TypedArray obtainStyledAttributes = templateLayout.getContext().obtainStyledAttributes(attributeSet, R.styleable.SuwColoredHeaderMixin, i, 0);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R.styleable.SuwColoredHeaderMixin_suwHeaderColor);
        if (colorStateList != null) {
            setColor(colorStateList);
        }
        obtainStyledAttributes.recycle();
    }

    public ColorStateList getColor() {
        TextView textView = getTextView();
        return textView != null ? textView.getTextColors() : null;
    }

    public void setColor(ColorStateList colorStateList) {
        TextView textView = getTextView();
        if (textView != null) {
            textView.setTextColor(colorStateList);
        }
    }
}
