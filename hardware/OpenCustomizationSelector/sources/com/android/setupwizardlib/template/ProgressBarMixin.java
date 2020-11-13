package com.android.setupwizardlib.template;

import android.content.res.ColorStateList;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.TemplateLayout;

public class ProgressBarMixin implements Mixin {
    @Nullable
    private ColorStateList mColor;
    private TemplateLayout mTemplateLayout;

    public ProgressBarMixin(TemplateLayout templateLayout) {
        this.mTemplateLayout = templateLayout;
    }

    private ProgressBar getProgressBar() {
        if (peekProgressBar() == null) {
            ViewStub viewStub = (ViewStub) this.mTemplateLayout.findManagedViewById(R.id.suw_layout_progress_stub);
            if (viewStub != null) {
                viewStub.inflate();
            }
            setColor(this.mColor);
        }
        return peekProgressBar();
    }

    @Nullable
    public ColorStateList getColor() {
        return this.mColor;
    }

    public boolean isShown() {
        View findManagedViewById = this.mTemplateLayout.findManagedViewById(R.id.suw_layout_progress);
        return findManagedViewById != null && findManagedViewById.getVisibility() == 0;
    }

    public ProgressBar peekProgressBar() {
        return (ProgressBar) this.mTemplateLayout.findManagedViewById(R.id.suw_layout_progress);
    }

    public void setColor(@Nullable ColorStateList colorStateList) {
        this.mColor = colorStateList;
        if (VERSION.SDK_INT >= 21) {
            ProgressBar peekProgressBar = peekProgressBar();
            if (peekProgressBar != null) {
                peekProgressBar.setIndeterminateTintList(colorStateList);
                if (VERSION.SDK_INT >= 23 || colorStateList != null) {
                    peekProgressBar.setProgressBackgroundTintList(colorStateList);
                }
            }
        }
    }

    public void setShown(boolean z) {
        ProgressBar progressBar;
        if (z) {
            progressBar = getProgressBar();
            if (progressBar != null) {
                progressBar.setVisibility(0);
                return;
            }
            return;
        }
        progressBar = peekProgressBar();
        if (progressBar != null) {
            progressBar.setVisibility(8);
        }
    }
}
