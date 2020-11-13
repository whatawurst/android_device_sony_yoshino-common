package com.android.setupwizardlib.items;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.items.ItemInflater.ItemParent;
import java.util.ArrayList;
import java.util.Iterator;

public class ButtonBarItem extends AbstractItem implements ItemParent {
    private final ArrayList<ButtonItem> mButtons = new ArrayList();
    private boolean mVisible = true;

    public ButtonBarItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void addChild(ItemHierarchy itemHierarchy) {
        if (itemHierarchy instanceof ButtonItem) {
            this.mButtons.add((ButtonItem) itemHierarchy);
            return;
        }
        throw new UnsupportedOperationException("Cannot add non-button item to Button Bar");
    }

    public ItemHierarchy findItemById(int i) {
        if (getId() == i) {
            return this;
        }
        Iterator it = this.mButtons.iterator();
        while (it.hasNext()) {
            ItemHierarchy findItemById = ((ButtonItem) it.next()).findItemById(i);
            if (findItemById != null) {
                return findItemById;
            }
        }
        return null;
    }

    public int getCount() {
        return isVisible();
    }

    public int getLayoutResource() {
        return R.layout.suw_items_button_bar;
    }

    public int getViewId() {
        return getId();
    }

    public boolean isEnabled() {
        return false;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public void onBindView(View view) {
        ViewGroup viewGroup = (LinearLayout) view;
        viewGroup.removeAllViews();
        Iterator it = this.mButtons.iterator();
        while (it.hasNext()) {
            viewGroup.addView(((ButtonItem) it.next()).createButton(viewGroup));
        }
        view.setId(getViewId());
    }

    public void setVisible(boolean z) {
        this.mVisible = z;
    }
}
