package com.tjrushby.runlite.util;


import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.SeekBar;

public class SeekBarAnimation extends Animation {
    private SeekBar seekBar;
    private float from;
    private float to;

    public SeekBarAnimation(SeekBar seekBar, float from, float to) {
        this.seekBar = seekBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        seekBar.setProgress((int) value);
    }
}
