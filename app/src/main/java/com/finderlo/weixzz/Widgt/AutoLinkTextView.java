package com.finderlo.weixzz.widgt;

/**
 * Created by Finderlo on 2016/8/6.
 */
import android.content.Context;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.finderlo.weixzz.base.BaseTextView;


public class AutoLinkTextView extends BaseTextView {
    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public AutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public AutoLinkTextView(Context context) {
        super(context);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);


        SpannableString span = new SpannableString(getText());
        ClickableSpan[] links = span.getSpans(getSelectionStart(),
                getSelectionEnd(), ClickableSpan.class);
        if (links.length != 0) {
            return true;
        }
        return false;

    }
}
