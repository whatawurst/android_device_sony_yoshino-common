package com.android.setupwizardlib.view;

import android.content.Context;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.android.setupwizardlib.span.LinkSpan;
import com.android.setupwizardlib.span.LinkSpan.OnLinkClickListener;
import com.android.setupwizardlib.span.SpanHelper;
import com.android.setupwizardlib.view.TouchableMovementMethod.TouchableLinkMovementMethod;

public class RichTextView extends TextView implements OnLinkClickListener {
    private static final String ANNOTATION_LINK = "link";
    private static final String ANNOTATION_TEXT_APPEARANCE = "textAppearance";
    private static final String TAG = "RichTextView";
    private OnLinkClickListener mOnLinkClickListener;

    public RichTextView(Context context) {
        super(context);
    }

    public RichTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public static CharSequence getRichText(Context context, CharSequence charSequence) {
        int i = 0;
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        CharSequence spannableString = new SpannableString(charSequence);
        Annotation[] annotationArr = (Annotation[]) spannableString.getSpans(0, spannableString.length(), Annotation.class);
        int length = annotationArr.length;
        while (i < length) {
            Annotation annotation = annotationArr[i];
            String key = annotation.getKey();
            if (ANNOTATION_TEXT_APPEARANCE.equals(key)) {
                int identifier = context.getResources().getIdentifier(annotation.getValue(), "style", context.getPackageName());
                if (identifier == 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Cannot find resource: ");
                    stringBuilder.append(identifier);
                    Log.w(TAG, stringBuilder.toString());
                }
                SpanHelper.replaceSpan(spannableString, annotation, new TextAppearanceSpan(context, identifier));
            } else if (ANNOTATION_LINK.equals(key)) {
                SpanHelper.replaceSpan(spannableString, annotation, new LinkSpan(annotation.getValue()));
            }
            i++;
        }
        return spannableString;
    }

    private boolean hasLinks(CharSequence charSequence) {
        return charSequence instanceof Spanned ? ((ClickableSpan[]) ((Spanned) charSequence).getSpans(0, charSequence.length(), ClickableSpan.class)).length > 0 : false;
    }

    public OnLinkClickListener getOnLinkClickListener() {
        return this.mOnLinkClickListener;
    }

    public boolean onLinkClick(LinkSpan linkSpan) {
        return this.mOnLinkClickListener != null ? this.mOnLinkClickListener.onLinkClick(linkSpan) : false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        MovementMethod movementMethod = getMovementMethod();
        if (movementMethod instanceof TouchableMovementMethod) {
            TouchableMovementMethod touchableMovementMethod = (TouchableMovementMethod) movementMethod;
            if (touchableMovementMethod.getLastTouchEvent() == motionEvent) {
                return touchableMovementMethod.isLastTouchEventHandled();
            }
        }
        return onTouchEvent;
    }

    public void setOnLinkClickListener(OnLinkClickListener onLinkClickListener) {
        this.mOnLinkClickListener = onLinkClickListener;
    }

    public void setText(CharSequence charSequence, BufferType bufferType) {
        CharSequence richText = getRichText(getContext(), charSequence);
        super.setText(richText, bufferType);
        boolean hasLinks = hasLinks(richText);
        if (hasLinks) {
            setMovementMethod(TouchableLinkMovementMethod.getInstance());
        } else {
            setMovementMethod(null);
        }
        setFocusable(hasLinks);
        setRevealOnFocusHint(false);
        setFocusableInTouchMode(hasLinks);
    }
}
