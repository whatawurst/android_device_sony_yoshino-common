package com.android.setupwizardlib.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;

public class SystemBarHelper {
    @SuppressLint({"InlinedApi"})
    private static final int DEFAULT_IMMERSIVE_FLAGS = 5634;
    @SuppressLint({"InlinedApi"})
    private static final int DIALOG_IMMERSIVE_FLAGS = 4098;
    private static final int PEEK_DECOR_VIEW_RETRIES = 3;
    private static final int STATUS_BAR_DISABLE_BACK = 4194304;
    private static final String TAG = "SystemBarHelper";

    private interface OnDecorViewInstalledListener {
        void onDecorViewInstalled(View view);
    }

    private static class DecorViewFinder {
        private OnDecorViewInstalledListener mCallback;
        private Runnable mCheckDecorViewRunnable;
        private final Handler mHandler;
        private int mRetries;
        private Window mWindow;

        private DecorViewFinder() {
            this.mHandler = new Handler();
            this.mCheckDecorViewRunnable = new Runnable() {
                public void run() {
                    View peekDecorView = DecorViewFinder.this.mWindow.peekDecorView();
                    if (peekDecorView != null) {
                        DecorViewFinder.this.mCallback.onDecorViewInstalled(peekDecorView);
                        return;
                    }
                    DecorViewFinder.this.mRetries = DecorViewFinder.this.mRetries - 1;
                    if (DecorViewFinder.this.mRetries >= 0) {
                        DecorViewFinder.this.mHandler.post(DecorViewFinder.this.mCheckDecorViewRunnable);
                        return;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Cannot get decor view of window: ");
                    stringBuilder.append(DecorViewFinder.this.mWindow);
                    Log.w(SystemBarHelper.TAG, stringBuilder.toString());
                }
            };
        }

        /* synthetic */ DecorViewFinder(AnonymousClass1 anonymousClass1) {
            this();
        }

        public void getDecorView(Window window, OnDecorViewInstalledListener onDecorViewInstalledListener, int i) {
            this.mWindow = window;
            this.mRetries = i;
            this.mCallback = onDecorViewInstalledListener;
            this.mCheckDecorViewRunnable.run();
        }
    }

    @TargetApi(21)
    private static class WindowInsetsListener implements OnApplyWindowInsetsListener {
        private int mBottomOffset;
        private boolean mHasCalculatedBottomOffset;

        private WindowInsetsListener() {
            this.mHasCalculatedBottomOffset = false;
        }

        /* synthetic */ WindowInsetsListener(AnonymousClass1 anonymousClass1) {
            this();
        }

        public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
            int i;
            if (!this.mHasCalculatedBottomOffset) {
                this.mBottomOffset = SystemBarHelper.getBottomDistance(view);
                this.mHasCalculatedBottomOffset = true;
            }
            int systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
            int max = Math.max(windowInsets.getSystemWindowInsetBottom() - this.mBottomOffset, 0);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
            if (max < marginLayoutParams.bottomMargin + view.getHeight()) {
                marginLayoutParams.setMargins(marginLayoutParams.leftMargin, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, max);
                view.setLayoutParams(marginLayoutParams);
                i = 0;
            } else {
                i = systemWindowInsetBottom;
            }
            return windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), i);
        }
    }

    @TargetApi(11)
    private static void addImmersiveFlagsToDecorView(Window window, final int i) {
        getDecorView(window, new OnDecorViewInstalledListener() {
            public void onDecorViewInstalled(View view) {
                SystemBarHelper.addVisibilityFlag(view, i);
            }
        });
    }

    public static void addVisibilityFlag(View view, int i) {
        if (VERSION.SDK_INT >= 11) {
            view.setSystemUiVisibility(view.getSystemUiVisibility() | i);
        }
    }

    public static void addVisibilityFlag(Window window, int i) {
        if (VERSION.SDK_INT >= 11) {
            LayoutParams attributes = window.getAttributes();
            attributes.systemUiVisibility |= i;
            window.setAttributes(attributes);
        }
    }

    private static int getBottomDistance(View view) {
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        return (view.getRootView().getHeight() - iArr[1]) - view.getHeight();
    }

    private static void getDecorView(Window window, OnDecorViewInstalledListener onDecorViewInstalledListener) {
        new DecorViewFinder().getDecorView(window, onDecorViewInstalledListener, 3);
    }

    public static void hideSystemBars(Dialog dialog) {
        if (VERSION.SDK_INT >= 21) {
            Window window = dialog.getWindow();
            temporarilyDisableDialogFocus(window);
            addVisibilityFlag(window, (int) DIALOG_IMMERSIVE_FLAGS);
            addImmersiveFlagsToDecorView(window, DIALOG_IMMERSIVE_FLAGS);
            window.setNavigationBarColor(0);
            window.setStatusBarColor(0);
        }
    }

    public static void hideSystemBars(Window window) {
        if (VERSION.SDK_INT >= 21) {
            addVisibilityFlag(window, (int) DEFAULT_IMMERSIVE_FLAGS);
            addImmersiveFlagsToDecorView(window, DEFAULT_IMMERSIVE_FLAGS);
            window.setNavigationBarColor(0);
            window.setStatusBarColor(0);
        }
    }

    @TargetApi(11)
    private static void removeImmersiveFlagsFromDecorView(Window window, final int i) {
        getDecorView(window, new OnDecorViewInstalledListener() {
            public void onDecorViewInstalled(View view) {
                SystemBarHelper.removeVisibilityFlag(view, i);
            }
        });
    }

    public static void removeVisibilityFlag(View view, int i) {
        if (VERSION.SDK_INT >= 11) {
            view.setSystemUiVisibility(view.getSystemUiVisibility() & i);
        }
    }

    public static void removeVisibilityFlag(Window window, int i) {
        if (VERSION.SDK_INT >= 11) {
            LayoutParams attributes = window.getAttributes();
            attributes.systemUiVisibility &= i;
            window.setAttributes(attributes);
        }
    }

    @RequiresPermission("android.permission.STATUS_BAR")
    public static void setBackButtonVisible(Window window, boolean z) {
        if (VERSION.SDK_INT < 11) {
            return;
        }
        if (z) {
            removeVisibilityFlag(window, (int) STATUS_BAR_DISABLE_BACK);
            removeImmersiveFlagsFromDecorView(window, STATUS_BAR_DISABLE_BACK);
            return;
        }
        addVisibilityFlag(window, (int) STATUS_BAR_DISABLE_BACK);
        addImmersiveFlagsToDecorView(window, STATUS_BAR_DISABLE_BACK);
    }

    public static void setImeInsetView(View view) {
        if (VERSION.SDK_INT >= 21) {
            view.setOnApplyWindowInsetsListener(new WindowInsetsListener());
        }
    }

    public static void showSystemBars(Dialog dialog, Context context) {
        showSystemBars(dialog.getWindow(), context);
    }

    public static void showSystemBars(Window window, Context context) {
        if (VERSION.SDK_INT >= 21) {
            removeVisibilityFlag(window, (int) DEFAULT_IMMERSIVE_FLAGS);
            removeImmersiveFlagsFromDecorView(window, DEFAULT_IMMERSIVE_FLAGS);
            if (context != null) {
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16843857, 16843858});
                int color = obtainStyledAttributes.getColor(0, 0);
                int color2 = obtainStyledAttributes.getColor(1, 0);
                window.setStatusBarColor(color);
                window.setNavigationBarColor(color2);
                obtainStyledAttributes.recycle();
            }
        }
    }

    private static void temporarilyDisableDialogFocus(final Window window) {
        window.setFlags(8, 8);
        window.setSoftInputMode(256);
        new Handler().post(new Runnable() {
            public void run() {
                window.clearFlags(8);
            }
        });
    }
}
