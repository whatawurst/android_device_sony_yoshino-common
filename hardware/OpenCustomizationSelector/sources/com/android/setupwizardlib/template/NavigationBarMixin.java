package com.android.setupwizardlib.template;

import android.view.View;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.TemplateLayout;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;

public class NavigationBarMixin implements Mixin {
    private TemplateLayout mTemplateLayout;

    public NavigationBarMixin(TemplateLayout templateLayout) {
        this.mTemplateLayout = templateLayout;
    }

    public NavigationBar getNavigationBar() {
        View findManagedViewById = this.mTemplateLayout.findManagedViewById(R.id.suw_layout_navigation_bar);
        return findManagedViewById instanceof NavigationBar ? (NavigationBar) findManagedViewById : null;
    }

    public CharSequence getNextButtonText() {
        return getNavigationBar().getNextButton().getText();
    }

    public void setNavigationBarListener(NavigationBarListener navigationBarListener) {
        getNavigationBar().setNavigationBarListener(navigationBarListener);
    }

    public void setNextButtonText(int i) {
        getNavigationBar().getNextButton().setText(i);
    }

    public void setNextButtonText(CharSequence charSequence) {
        getNavigationBar().getNextButton().setText(charSequence);
    }
}
