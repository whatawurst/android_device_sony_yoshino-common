package com.android.setupwizardlib.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.items.ItemHierarchy.Observer;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractItemHierarchy implements ItemHierarchy {
    private static final String TAG = "AbstractItemHierarchy";
    private int mId = 0;
    private ArrayList<Observer> mObservers = new ArrayList();

    public AbstractItemHierarchy(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SuwAbstractItem);
        this.mId = obtainStyledAttributes.getResourceId(R.styleable.SuwAbstractItem_android_id, 0);
        obtainStyledAttributes.recycle();
    }

    public int getId() {
        return this.mId;
    }

    public int getViewId() {
        return getId();
    }

    public void notifyChanged() {
        Iterator it = this.mObservers.iterator();
        while (it.hasNext()) {
            ((Observer) it.next()).onChanged(this);
        }
    }

    public void notifyItemRangeChanged(int i, int i2) {
        StringBuilder stringBuilder;
        if (i < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeChanged: Invalid position=");
            stringBuilder.append(i);
            Log.w(TAG, stringBuilder.toString());
        } else if (i2 < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeChanged: Invalid itemCount=");
            stringBuilder.append(i2);
            Log.w(TAG, stringBuilder.toString());
        } else {
            Iterator it = this.mObservers.iterator();
            while (it.hasNext()) {
                ((Observer) it.next()).onItemRangeChanged(this, i, i2);
            }
        }
    }

    public void notifyItemRangeInserted(int i, int i2) {
        StringBuilder stringBuilder;
        if (i < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeInserted: Invalid position=");
            stringBuilder.append(i);
            Log.w(TAG, stringBuilder.toString());
        } else if (i2 < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeInserted: Invalid itemCount=");
            stringBuilder.append(i2);
            Log.w(TAG, stringBuilder.toString());
        } else {
            Iterator it = this.mObservers.iterator();
            while (it.hasNext()) {
                ((Observer) it.next()).onItemRangeInserted(this, i, i2);
            }
        }
    }

    public void notifyItemRangeMoved(int i, int i2, int i3) {
        StringBuilder stringBuilder;
        if (i < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeMoved: Invalid fromPosition=");
            stringBuilder.append(i);
            Log.w(TAG, stringBuilder.toString());
        } else if (i2 < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeMoved: Invalid toPosition=");
            stringBuilder.append(i2);
            Log.w(TAG, stringBuilder.toString());
        } else if (i3 < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeMoved: Invalid itemCount=");
            stringBuilder.append(i3);
            Log.w(TAG, stringBuilder.toString());
        } else {
            Iterator it = this.mObservers.iterator();
            while (it.hasNext()) {
                ((Observer) it.next()).onItemRangeMoved(this, i, i2, i3);
            }
        }
    }

    public void notifyItemRangeRemoved(int i, int i2) {
        StringBuilder stringBuilder;
        if (i < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeInserted: Invalid position=");
            stringBuilder.append(i);
            Log.w(TAG, stringBuilder.toString());
        } else if (i2 < 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("notifyItemRangeInserted: Invalid itemCount=");
            stringBuilder.append(i2);
            Log.w(TAG, stringBuilder.toString());
        } else {
            Iterator it = this.mObservers.iterator();
            while (it.hasNext()) {
                ((Observer) it.next()).onItemRangeRemoved(this, i, i2);
            }
        }
    }

    public void registerObserver(Observer observer) {
        this.mObservers.add(observer);
    }

    public void setId(int i) {
        this.mId = i;
    }

    public void unregisterObserver(Observer observer) {
        this.mObservers.remove(observer);
    }
}
