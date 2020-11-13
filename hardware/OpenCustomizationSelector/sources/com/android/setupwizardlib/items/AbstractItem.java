package com.android.setupwizardlib.items;

import android.content.Context;
import android.util.AttributeSet;

public abstract class AbstractItem extends AbstractItemHierarchy implements IItem {
    public AbstractItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ItemHierarchy findItemById(int i) {
        return i == getId() ? this : null;
    }

    public int getCount() {
        return 1;
    }

    public IItem getItemAt(int i) {
        return this;
    }

    public void notifyItemChanged() {
        notifyItemRangeChanged(0, 1);
    }
}
