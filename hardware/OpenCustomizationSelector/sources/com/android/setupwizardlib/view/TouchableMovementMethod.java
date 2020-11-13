package com.android.setupwizardlib.view;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public interface TouchableMovementMethod {

    public static class TouchableLinkMovementMethod extends LinkMovementMethod implements TouchableMovementMethod {
        MotionEvent mLastEvent;
        boolean mLastEventResult = false;

        public static TouchableLinkMovementMethod getInstance() {
            return new TouchableLinkMovementMethod();
        }

        public MotionEvent getLastTouchEvent() {
            return this.mLastEvent;
        }

        public boolean isLastTouchEventHandled() {
            return this.mLastEventResult;
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            this.mLastEvent = motionEvent;
            boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
            if (motionEvent.getAction() == 0) {
                this.mLastEventResult = Selection.getSelectionStart(spannable) != -1;
            } else {
                this.mLastEventResult = onTouchEvent;
            }
            return onTouchEvent;
        }
    }

    MotionEvent getLastTouchEvent();

    boolean isLastTouchEventHandled();
}
