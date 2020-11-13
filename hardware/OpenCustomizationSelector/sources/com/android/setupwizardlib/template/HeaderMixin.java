package com.android.setupwizardlib.template;

import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.TemplateLayout;

public class HeaderMixin implements Mixin {
    private TemplateLayout mTemplateLayout;

    public HeaderMixin(@NonNull TemplateLayout templateLayout, @Nullable AttributeSet attributeSet, @AttrRes int i) {
        this.mTemplateLayout = templateLayout;
        TypedArray obtainStyledAttributes = templateLayout.getContext().obtainStyledAttributes(attributeSet, R.styleable.SuwHeaderMixin, i, 0);
        CharSequence text = obtainStyledAttributes.getText(R.styleable.SuwHeaderMixin_suwHeaderText);
        if (text != null) {
            setText(text);
        }
        obtainStyledAttributes.recycle();
    }

    public CharSequence getText() {
        TextView textView = getTextView();
        return textView != null ? textView.getText() : null;
    }

    public TextView getTextView() {
        return (TextView) this.mTemplateLayout.findManagedViewById(R.id.suw_layout_title);
    }

    public void setText(int i) {
        TextView textView = getTextView();
        if (textView != null) {
            textView.setText(i);
        }
    }

    public void setText(CharSequence charSequence) {
        TextView textView = getTextView();
        if (textView != null) {
            textView.setText(charSequence);
        }
    }
}
