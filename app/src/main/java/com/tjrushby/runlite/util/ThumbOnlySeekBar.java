package com.tjrushby.runlite.util;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class ThumbOnlySeekBar extends AppCompatSeekBar {
    private boolean isDragging;

    public ThumbOnlySeekBar(Context context) {
        super(context);
    }

    public ThumbOnlySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbOnlySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isWithinThumb = calculateWithinThumb(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // initial press down, check it is within thumb bounds
                if(isWithinThumb) {
                    isDragging = true;
                    return super.onTouchEvent(event);
                } else {
                    return true;
                }

            case MotionEvent.ACTION_UP:
                // release touch, check the user was still on the thumb
                if(!isDragging) {
                    return true;
                } else {
                    isDragging = false;
                    return super.onTouchEvent(event);
                }

            case MotionEvent.ACTION_MOVE:
                // dragging motion
                if(!isDragging) {
                    return true;
                }

                break;

            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                break;
        }

        return super.onTouchEvent(event);
    }

    private boolean calculateWithinThumb(MotionEvent event) {
        return getThumb().getBounds().contains((int) event.getX(), (int) event.getY());
    }
}
