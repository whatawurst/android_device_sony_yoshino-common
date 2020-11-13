package com.android.setupwizardlib.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.setupwizardlib.R;

public class ButtonItem extends AbstractItem implements android.view.View.OnClickListener {
    private Button mButton;
    private boolean mEnabled = true;
    private OnClickListener mListener;
    private CharSequence mText;
    private int mTheme = R.style.SuwButtonItem;

    public interface OnClickListener {
        void onClick(ButtonItem buttonItem);
    }

    public ButtonItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwButtonItem);
        this.mEnabled = obtainStyledAttributes.getBoolean(R.styleable.SuwButtonItem_android_enabled, true);
        this.mText = obtainStyledAttributes.getText(R.styleable.SuwButtonItem_android_text);
        this.mTheme = obtainStyledAttributes.getResourceId(R.styleable.SuwButtonItem_android_theme, R.style.SuwButtonItem);
        obtainStyledAttributes.recycle();
    }

    @SuppressLint({"InflateParams"})
    private Button createButton(Context context) {
        return (Button) LayoutInflater.from(context).inflate(R.layout.suw_button, null, false);
    }

    /* Access modifiers changed, original: protected */
    public Button createButton(ViewGroup viewGroup) {
        if (this.mButton == null) {
            Context context = viewGroup.getContext();
            this.mButton = createButton(this.mTheme != 0 ? new ContextThemeWrapper(context, this.mTheme) : context);
            this.mButton.setOnClickListener(this);
        } else if (this.mButton.getParent() instanceof ViewGroup) {
            ((ViewGroup) this.mButton.getParent()).removeView(this.mButton);
        }
        this.mButton.setEnabled(this.mEnabled);
        this.mButton.setText(this.mText);
        this.mButton.setId(getViewId());
        return this.mButton;
    }

    public int getCount() {
        return 0;
    }

    public int getLayoutResource() {
        return 0;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public int getTheme() {
        return this.mTheme;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public final void onBindView(View view) {
        throw new UnsupportedOperationException("Cannot bind to ButtonItem's view");
    }

    public void onClick(View view) {
        if (this.mListener != null) {
            this.mListener.onClick(this);
        }
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }

    public void setText(CharSequence charSequence) {
        this.mText = charSequence;
    }

    public void setTheme(int i) {
        this.mTheme = i;
        this.mButton = null;
    }
}
