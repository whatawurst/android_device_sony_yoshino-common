package com.android.setupwizardlib.template;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.android.setupwizardlib.TemplateLayout;
import com.android.setupwizardlib.view.NavigationBar;

public class RequireScrollMixin implements Mixin {
    private ScrollHandlingDelegate mDelegate;
    private boolean mEverScrolledToBottom = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @Nullable
    private OnRequireScrollStateChangedListener mListener;
    private boolean mRequiringScrollToBottom = false;
    @NonNull
    private final TemplateLayout mTemplateLayout;

    interface ScrollHandlingDelegate {
        void pageScrollDown();

        void startListening();
    }

    public interface OnRequireScrollStateChangedListener {
        void onRequireScrollStateChanged(boolean z);
    }

    public RequireScrollMixin(@NonNull TemplateLayout templateLayout) {
        this.mTemplateLayout = templateLayout;
    }

    private void postScrollStateChange(final boolean z) {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (RequireScrollMixin.this.mListener != null) {
                    RequireScrollMixin.this.mListener.onRequireScrollStateChanged(z);
                }
            }
        });
    }

    public OnClickListener createOnClickListener(@Nullable final OnClickListener onClickListener) {
        return new OnClickListener() {
            public void onClick(View view) {
                if (RequireScrollMixin.this.mRequiringScrollToBottom) {
                    RequireScrollMixin.this.mDelegate.pageScrollDown();
                } else if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
            }
        };
    }

    public OnRequireScrollStateChangedListener getOnRequireScrollStateChangedListener() {
        return this.mListener;
    }

    public boolean isScrollingRequired() {
        return this.mRequiringScrollToBottom;
    }

    /* Access modifiers changed, original: 0000 */
    public void notifyScrollabilityChange(boolean z) {
        if (z != this.mRequiringScrollToBottom) {
            if (!z) {
                postScrollStateChange(false);
                this.mRequiringScrollToBottom = false;
                this.mEverScrolledToBottom = true;
            } else if (!this.mEverScrolledToBottom) {
                postScrollStateChange(true);
                this.mRequiringScrollToBottom = true;
            }
        }
    }

    public void requireScroll() {
        this.mDelegate.startListening();
    }

    public void requireScrollWithButton(@NonNull Button button, @StringRes int i, @Nullable OnClickListener onClickListener) {
        requireScrollWithButton(button, button.getContext().getText(i), onClickListener);
    }

    public void requireScrollWithButton(@NonNull final Button button, final CharSequence charSequence, @Nullable OnClickListener onClickListener) {
        final CharSequence text = button.getText();
        button.setOnClickListener(createOnClickListener(onClickListener));
        setOnRequireScrollStateChangedListener(new OnRequireScrollStateChangedListener() {
            public void onRequireScrollStateChanged(boolean z) {
                button.setText(z ? charSequence : text);
            }
        });
        requireScroll();
    }

    public void requireScrollWithNavigationBar(@NonNull final NavigationBar navigationBar) {
        setOnRequireScrollStateChangedListener(new OnRequireScrollStateChangedListener() {
            public void onRequireScrollStateChanged(boolean z) {
                int i = 8;
                navigationBar.getMoreButton().setVisibility(z ? 0 : 8);
                Button nextButton = navigationBar.getNextButton();
                if (!z) {
                    i = 0;
                }
                nextButton.setVisibility(i);
            }
        });
        navigationBar.getMoreButton().setOnClickListener(createOnClickListener(null));
        requireScroll();
    }

    public void setOnRequireScrollStateChangedListener(@Nullable OnRequireScrollStateChangedListener onRequireScrollStateChangedListener) {
        this.mListener = onRequireScrollStateChangedListener;
    }

    public void setScrollHandlingDelegate(@NonNull ScrollHandlingDelegate scrollHandlingDelegate) {
        this.mDelegate = scrollHandlingDelegate;
    }
}
