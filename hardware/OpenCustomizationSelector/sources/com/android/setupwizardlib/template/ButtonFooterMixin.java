package com.android.setupwizardlib.template;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.setupwizardlib.R;
import com.android.setupwizardlib.TemplateLayout;

public class ButtonFooterMixin implements Mixin {
    private LinearLayout mButtonContainer;
    private final Context mContext;
    @Nullable
    private final ViewStub mFooterStub;

    public ButtonFooterMixin(TemplateLayout templateLayout) {
        this.mContext = templateLayout.getContext();
        this.mFooterStub = (ViewStub) templateLayout.findManagedViewById(R.id.suw_layout_footer);
    }

    @SuppressLint({"InflateParams"})
    private Button createThemedButton(Context context, @StyleRes int i) {
        return (Button) LayoutInflater.from(new ContextThemeWrapper(context, i)).inflate(R.layout.suw_button, null, false);
    }

    @NonNull
    private LinearLayout ensureFooterInflated() {
        if (this.mButtonContainer == null) {
            if (this.mFooterStub != null) {
                this.mFooterStub.setLayoutResource(R.layout.suw_glif_footer_button_bar);
                this.mButtonContainer = (LinearLayout) this.mFooterStub.inflate();
            } else {
                throw new IllegalStateException("Footer stub is not found in this template");
            }
        }
        return this.mButtonContainer;
    }

    public Button addButton(@StringRes int i, @StyleRes int i2) {
        Button createThemedButton = createThemedButton(this.mContext, i2);
        createThemedButton.setText(i);
        return addButton(createThemedButton);
    }

    public Button addButton(Button button) {
        ensureFooterInflated().addView(button);
        return button;
    }

    public Button addButton(CharSequence charSequence, @StyleRes int i) {
        Button createThemedButton = createThemedButton(this.mContext, i);
        createThemedButton.setText(charSequence);
        return addButton(createThemedButton);
    }

    public View addSpace() {
        LinearLayout ensureFooterInflated = ensureFooterInflated();
        View view = new View(ensureFooterInflated.getContext());
        view.setLayoutParams(new LayoutParams(0, 0, 1.0f));
        view.setVisibility(4);
        ensureFooterInflated.addView(view);
        return view;
    }

    public void removeAllViews() {
        if (this.mButtonContainer != null) {
            this.mButtonContainer.removeAllViews();
        }
    }

    public void removeButton(Button button) {
        if (this.mButtonContainer != null) {
            this.mButtonContainer.removeView(button);
        }
    }

    public void removeSpace(View view) {
        if (this.mButtonContainer != null) {
            this.mButtonContainer.removeView(view);
        }
    }
}
