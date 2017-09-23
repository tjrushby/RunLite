package com.tjrushby.runlite.views;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RunningActivityIntrumentedTest {
    @Test
    public void getContext() throws Exception {
        Context mockContext = InstrumentationRegistry.getTargetContext();
        assertEquals("runlite.tjrushby.com.runlite", mockContext.getPackageName());
    }

    @Test
    public void getComponent() throws Exception {

    }

    @Test
    public void hideButtonStart() throws Exception {
    }

    @Test
    public void showButtonStart() throws Exception {
    }

    @Test
    public void updateButtonStartText() throws Exception {
    }

    @Test
    public void hideButtonPause() throws Exception {
    }

    @Test
    public void showButtonPause() throws Exception {
    }

    @Test
    public void updateGPSIconAverage() throws Exception {
    }

    @Test
    public void updateGPSIconBad() throws Exception {
    }

    @Test
    public void updateGPSIconGood() throws Exception {
    }

    @Test
    public void updateTextViewTime() throws Exception {
    }

    @Test
    public void updateTextViewDistance() throws Exception {
    }

    @Test
    public void updateTextViewPace() throws Exception {
    }

    @Test
    public void setTextViewPaceDefaultText() throws Exception {
    }

}