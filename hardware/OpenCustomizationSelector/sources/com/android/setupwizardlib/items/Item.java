package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.setupwizardlib.R;

public class Item extends AbstractItem {
    private boolean mEnabled;
    private Drawable mIcon;
    private int mLayoutRes;
    private CharSequence mSummary;
    private CharSequence mTitle;
    private boolean mVisible;

    public Item() {
        this.mEnabled = true;
        this.mVisible = true;
        this.mLayoutRes = getDefaultLayoutResource();
    }

    public Item(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEnabled = true;
        this.mVisible = true;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwItem);
        this.mEnabled = obtainStyledAttributes.getBoolean(R.styleable.SuwItem_android_enabled, true);
        this.mIcon = obtainStyledAttributes.getDrawable(R.styleable.SuwItem_android_icon);
        this.mTitle = obtainStyledAttributes.getText(R.styleable.SuwItem_android_title);
        this.mSummary = obtainStyledAttributes.getText(R.styleable.SuwItem_android_summary);
        this.mLayoutRes = obtainStyledAttributes.getResourceId(R.styleable.SuwItem_android_layout, getDefaultLayoutResource());
        this.mVisible = obtainStyledAttributes.getBoolean(R.styleable.SuwItem_android_visible, true);
        obtainStyledAttributes.recycle();
    }

    public int getCount() {
        return isVisible();
    }

    /* Access modifiers changed, original: protected */
    public int getDefaultLayoutResource() {
        return R.layout.suw_items_default;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public int getLayoutResource() {
        return this.mLayoutRes;
    }

    public CharSequence getSummary() {
        return this.mSummary;
    }

    public CharSequence getTitle() {
        return this.mTitle;
    }

    public int getViewId() {
        return getId();
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void onBindView(View view) {
        ((TextView) view.findViewById(R.id.suw_items_title)).setText(getTitle());
        TextView textView = (TextView) view.findViewById(R.id.suw_items_summary);
        CharSequence summary = getSummary();
        if (summary == null || summary.length() <= 0) {
            textView.setVisibility(8);
        } else {
            textView.setText(summary);
            textView.setVisibility(0);
        }
        View findViewById = view.findViewById(R.id.suw_items_icon_container);
        Drawable icon = getIcon();
        if (icon != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.suw_items_icon);
            imageView.setImageDrawable(null);
            onMergeIconStateAndLevels(imageView, icon);
            imageView.setImageDrawable(icon);
            findViewById.setVisibility(0);
        } else {
            findViewById.setVisibility(8);
        }
        view.setId(getViewId());
    }

    /* Access modifiers changed, original: protected */
    public void onMergeIconStateAndLevels(ImageView imageView, Drawable drawable) {
        imageView.setImageState(drawable.getState(), false);
        imageView.setImageLevel(drawable.getLevel());
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
        notifyItemChanged();
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        notifyItemChanged();
    }

    public void setLayoutResource(int i) {
        this.mLayoutRes = i;
        notifyItemChanged();
    }

    public void setSummary(CharSequence charSequence) {
        this.mSummary = charSequence;
        notifyItemChanged();
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        notifyItemChanged();
    }

    public void setVisible(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            if (z) {
                notifyItemRangeInserted(0, 1);
            } else {
                notifyItemRangeRemoved(0, 1);
            }
        }
    }
}
