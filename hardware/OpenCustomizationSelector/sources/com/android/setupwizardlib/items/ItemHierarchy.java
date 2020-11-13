package com.android.setupwizardlib.items;

public interface ItemHierarchy {

    public interface Observer {
        void onChanged(ItemHierarchy itemHierarchy);

        void onItemRangeChanged(ItemHierarchy itemHierarchy, int i, int i2);

        void onItemRangeInserted(ItemHierarchy itemHierarchy, int i, int i2);

        void onItemRangeMoved(ItemHierarchy itemHierarchy, int i, int i2, int i3);

        void onItemRangeRemoved(ItemHierarchy itemHierarchy, int i, int i2);
    }

    ItemHierarchy findItemById(int i);

    int getCount();

    IItem getItemAt(int i);

    void registerObserver(Observer observer);

    void unregisterObserver(Observer observer);
}
