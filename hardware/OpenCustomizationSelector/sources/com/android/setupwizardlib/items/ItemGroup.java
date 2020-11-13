package com.android.setupwizardlib.items;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.setupwizardlib.items.ItemHierarchy.Observer;
import com.android.setupwizardlib.items.ItemInflater.ItemParent;
import java.util.ArrayList;
import java.util.List;

public class ItemGroup extends AbstractItemHierarchy implements ItemParent, Observer {
    private static final String TAG = "ItemGroup";
    private List<ItemHierarchy> mChildren = new ArrayList();
    private int mCount = 0;
    private boolean mDirty = false;
    private SparseIntArray mHierarchyStart = new SparseIntArray();

    public ItemGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private static int binarySearch(SparseIntArray sparseIntArray, int i) {
        int i2 = 0;
        int size = sparseIntArray.size() - 1;
        while (i2 <= size) {
            int i3 = (i2 + size) >>> 1;
            int valueAt = sparseIntArray.valueAt(i3);
            if (valueAt < i) {
                i2 = i3 + 1;
            } else if (valueAt <= i) {
                return sparseIntArray.keyAt(i3);
            } else {
                size = i3 - 1;
            }
        }
        return sparseIntArray.keyAt(i2 - 1);
    }

    private int getChildPosition(int i) {
        updateDataIfNeeded();
        if (i == -1) {
            return -1;
        }
        int size = this.mChildren.size();
        int i2 = -1;
        while (i2 < 0 && i < size) {
            i2 = this.mHierarchyStart.get(i, -1);
            i++;
        }
        return i2 < 0 ? getCount() : i2;
    }

    private int getChildPosition(ItemHierarchy itemHierarchy) {
        return getChildPosition(identityIndexOf(this.mChildren, itemHierarchy));
    }

    private int getItemIndex(int i) {
        updateDataIfNeeded();
        if (i < 0 || i >= this.mCount) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("size=");
            stringBuilder.append(this.mCount);
            stringBuilder.append("; index=");
            stringBuilder.append(i);
            throw new IndexOutOfBoundsException(stringBuilder.toString());
        }
        int binarySearch = binarySearch(this.mHierarchyStart, i);
        if (binarySearch >= 0) {
            return binarySearch;
        }
        throw new IllegalStateException("Cannot have item start index < 0");
    }

    private static <T> int identityIndexOf(List<T> list, T t) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (list.get(i) == t) {
                return i;
            }
        }
        return -1;
    }

    private void updateDataIfNeeded() {
        if (this.mDirty) {
            this.mCount = 0;
            this.mHierarchyStart.clear();
            for (int i = 0; i < this.mChildren.size(); i++) {
                ItemHierarchy itemHierarchy = (ItemHierarchy) this.mChildren.get(i);
                if (itemHierarchy.getCount() > 0) {
                    this.mHierarchyStart.put(i, this.mCount);
                }
                this.mCount = itemHierarchy.getCount() + this.mCount;
            }
            this.mDirty = false;
        }
    }

    public void addChild(ItemHierarchy itemHierarchy) {
        this.mDirty = true;
        this.mChildren.add(itemHierarchy);
        itemHierarchy.registerObserver(this);
        int count = itemHierarchy.getCount();
        if (count > 0) {
            notifyItemRangeInserted(getChildPosition(itemHierarchy), count);
        }
    }

    public void clear() {
        if (this.mChildren.size() != 0) {
            int count = getCount();
            for (ItemHierarchy unregisterObserver : this.mChildren) {
                unregisterObserver.unregisterObserver(this);
            }
            this.mDirty = true;
            this.mChildren.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    public ItemHierarchy findItemById(int i) {
        if (i == getId()) {
            return this;
        }
        for (ItemHierarchy findItemById : this.mChildren) {
            ItemHierarchy findItemById2 = findItemById.findItemById(i);
            if (findItemById2 != null) {
                return findItemById2;
            }
        }
        return null;
    }

    public int getCount() {
        updateDataIfNeeded();
        return this.mCount;
    }

    public IItem getItemAt(int i) {
        int itemIndex = getItemIndex(i);
        return ((ItemHierarchy) this.mChildren.get(itemIndex)).getItemAt(i - this.mHierarchyStart.get(itemIndex));
    }

    public void onChanged(ItemHierarchy itemHierarchy) {
        this.mDirty = true;
        notifyChanged();
    }

    public void onItemRangeChanged(ItemHierarchy itemHierarchy, int i, int i2) {
        int childPosition = getChildPosition(itemHierarchy);
        if (childPosition >= 0) {
            notifyItemRangeChanged(childPosition + i, i2);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected child change ");
        stringBuilder.append(itemHierarchy);
        Log.e(TAG, stringBuilder.toString());
    }

    public void onItemRangeInserted(ItemHierarchy itemHierarchy, int i, int i2) {
        this.mDirty = true;
        int childPosition = getChildPosition(itemHierarchy);
        if (childPosition >= 0) {
            notifyItemRangeInserted(childPosition + i, i2);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected child insert ");
        stringBuilder.append(itemHierarchy);
        Log.e(TAG, stringBuilder.toString());
    }

    public void onItemRangeMoved(ItemHierarchy itemHierarchy, int i, int i2, int i3) {
        this.mDirty = true;
        int childPosition = getChildPosition(itemHierarchy);
        if (childPosition >= 0) {
            notifyItemRangeMoved(childPosition + i, childPosition + i2, i3);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected child move ");
        stringBuilder.append(itemHierarchy);
        Log.e(TAG, stringBuilder.toString());
    }

    public void onItemRangeRemoved(ItemHierarchy itemHierarchy, int i, int i2) {
        this.mDirty = true;
        int childPosition = getChildPosition(itemHierarchy);
        if (childPosition >= 0) {
            notifyItemRangeRemoved(childPosition + i, i2);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected child remove ");
        stringBuilder.append(itemHierarchy);
        Log.e(TAG, stringBuilder.toString());
    }

    public boolean removeChild(ItemHierarchy itemHierarchy) {
        int identityIndexOf = identityIndexOf(this.mChildren, itemHierarchy);
        int childPosition = getChildPosition(identityIndexOf);
        this.mDirty = true;
        if (identityIndexOf == -1) {
            return false;
        }
        int count = itemHierarchy.getCount();
        this.mChildren.remove(identityIndexOf);
        itemHierarchy.unregisterObserver(this);
        if (count <= 0) {
            return true;
        }
        notifyItemRangeRemoved(childPosition, count);
        return true;
    }
}
