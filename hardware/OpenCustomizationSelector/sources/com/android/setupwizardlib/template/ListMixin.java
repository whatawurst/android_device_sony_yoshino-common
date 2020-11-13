package com.android.setupwizardlib.template;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.TemplateLayout;
import com.android.setupwizardlib.items.ItemAdapter;
import com.android.setupwizardlib.items.ItemGroup;
import com.android.setupwizardlib.items.ItemInflater;
import com.android.setupwizardlib.util.DrawableLayoutDirectionHelper;

public class ListMixin implements Mixin {
    private Drawable mDefaultDivider;
    private Drawable mDivider;
    private int mDividerInsetEnd;
    private int mDividerInsetStart;
    @Nullable
    private ListView mListView;
    private TemplateLayout mTemplateLayout;

    public ListMixin(@NonNull TemplateLayout templateLayout, @Nullable AttributeSet attributeSet, @AttrRes int i) {
        this.mTemplateLayout = templateLayout;
        Context context = templateLayout.getContext();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwListMixin, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.SuwListMixin_android_entries, 0);
        if (resourceId != 0) {
            setAdapter(new ItemAdapter((ItemGroup) new ItemInflater(context).inflate(resourceId)));
        }
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwListMixin_suwDividerInset, -1);
        if (dimensionPixelSize != -1) {
            setDividerInset(dimensionPixelSize);
        } else {
            setDividerInsets(obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwListMixin_suwDividerInsetStart, 0), obtainStyledAttributes.getDimensionPixelSize(R.styleable.SuwListMixin_suwDividerInsetEnd, 0));
        }
        obtainStyledAttributes.recycle();
    }

    @Nullable
    private ListView getListViewInternal() {
        if (this.mListView == null) {
            View findManagedViewById = this.mTemplateLayout.findManagedViewById(16908298);
            if (findManagedViewById instanceof ListView) {
                this.mListView = (ListView) findManagedViewById;
            }
        }
        return this.mListView;
    }

    private void updateDivider() {
        ListView listViewInternal = getListViewInternal();
        if (listViewInternal != null) {
            boolean z = true;
            if (VERSION.SDK_INT >= 19) {
                z = this.mTemplateLayout.isLayoutDirectionResolved();
            }
            if (z) {
                if (this.mDefaultDivider == null) {
                    this.mDefaultDivider = listViewInternal.getDivider();
                }
                this.mDivider = DrawableLayoutDirectionHelper.createRelativeInsetDrawable(this.mDefaultDivider, this.mDividerInsetStart, 0, this.mDividerInsetEnd, 0, this.mTemplateLayout);
                listViewInternal.setDivider(this.mDivider);
            }
        }
    }

    public ListAdapter getAdapter() {
        ListView listViewInternal = getListViewInternal();
        if (listViewInternal == null) {
            return null;
        }
        ListAdapter adapter = listViewInternal.getAdapter();
        return adapter instanceof HeaderViewListAdapter ? ((HeaderViewListAdapter) adapter).getWrappedAdapter() : adapter;
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    @Deprecated
    public int getDividerInset() {
        return getDividerInsetStart();
    }

    public int getDividerInsetEnd() {
        return this.mDividerInsetEnd;
    }

    public int getDividerInsetStart() {
        return this.mDividerInsetStart;
    }

    public ListView getListView() {
        return getListViewInternal();
    }

    public void onLayout() {
        if (this.mDivider == null) {
            updateDivider();
        }
    }

    public void setAdapter(ListAdapter listAdapter) {
        ListView listViewInternal = getListViewInternal();
        if (listViewInternal != null) {
            listViewInternal.setAdapter(listAdapter);
        }
    }

    @Deprecated
    public void setDividerInset(int i) {
        setDividerInsets(i, 0);
    }

    public void setDividerInsets(int i, int i2) {
        this.mDividerInsetStart = i;
        this.mDividerInsetEnd = i2;
        updateDivider();
    }
}
