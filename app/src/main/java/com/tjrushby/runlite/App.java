package com.tjrushby.runlite;

import android.app.Application;

import timber.log.Timber;

public class App extends Application {
    public static final int GPS_ACCURACY_BAD_THRESHOLD = 20;
    public static final int GPS_ACCURACY_GOOD_THRESHOLD = 10;

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
