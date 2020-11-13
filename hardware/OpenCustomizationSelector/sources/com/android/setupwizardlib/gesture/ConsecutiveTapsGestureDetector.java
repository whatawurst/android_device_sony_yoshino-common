package com.android.setupwizardlib.gesture;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public final class ConsecutiveTapsGestureDetector {
    private final int mConsecutiveTapTimeout;
    private final int mConsecutiveTapTouchSlopSquare;
    private int mConsecutiveTapsCounter = 0;
    private final OnConsecutiveTapsListener mListener;
    private MotionEvent mPreviousTapEvent;
    private final View mView;

    public interface OnConsecutiveTapsListener {
        void onConsecutiveTaps(int i);
    }

    public ConsecutiveTapsGestureDetector(OnConsecutiveTapsListener onConsecutiveTapsListener, View view) {
        this.mListener = onConsecutiveTapsListener;
        this.mView = view;
        int scaledDoubleTapSlop = ViewConfiguration.get(this.mView.getContext()).getScaledDoubleTapSlop();
        this.mConsecutiveTapTouchSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
        this.mConsecutiveTapTimeout = ViewConfiguration.getDoubleTapTimeout();
    }

    private boolean isConsecutiveTap(MotionEvent motionEvent) {
        if (this.mPreviousTapEvent == null) {
            return false;
        }
        double x = (double) (this.mPreviousTapEvent.getX() - motionEvent.getX());
        double y = (double) (this.mPreviousTapEvent.getY() - motionEvent.getY());
        return (x * x) + (y * y) <= ((double) this.mConsecutiveTapTouchSlopSquare) && motionEvent.getEventTime() - this.mPreviousTapEvent.getEventTime() < ((long) this.mConsecutiveTapTimeout);
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            Rect rect = new Rect();
            int[] iArr = new int[2];
            this.mView.getLocationOnScreen(iArr);
            rect.set(iArr[0], iArr[1], iArr[0] + this.mView.getWidth(), iArr[1] + this.mView.getHeight());
            if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                if (isConsecutiveTap(motionEvent)) {
                    this.mConsecutiveTapsCounter++;
                } else {
                    this.mConsecutiveTapsCounter = 1;
                }
                this.mListener.onConsecutiveTaps(this.mConsecutiveTapsCounter);
            } else {
                this.mConsecutiveTapsCounter = 0;
            }
            if (this.mPreviousTapEvent != null) {
                this.mPreviousTapEvent.recycle();
            }
            this.mPreviousTapEvent = MotionEvent.obtain(motionEvent);
        }
    }

    public void resetCounter() {
        this.mConsecutiveTapsCounter = 0;
    }
}
