package com.android.setupwizardlib.items;

import android.content.Context;

public class ItemInflater extends ReflectionInflater<ItemHierarchy> {

    public interface ItemParent {
        void addChild(ItemHierarchy itemHierarchy);
    }

    public ItemInflater(Context context) {
        super(context);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Item.class.getPackage().getName());
        stringBuilder.append(".");
        setDefaultPackage(stringBuilder.toString());
    }

    /* Access modifiers changed, original: protected */
    public void onAddChildItem(ItemHierarchy itemHierarchy, ItemHierarchy itemHierarchy2) {
        if (itemHierarchy instanceof ItemParent) {
            ((ItemParent) itemHierarchy).addChild(itemHierarchy2);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot add child item to ");
        stringBuilder.append(itemHierarchy);
        throw new IllegalArgumentException(stringBuilder.toString());
    }
}
