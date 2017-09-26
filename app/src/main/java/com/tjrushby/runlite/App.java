package com.tjrushby.runlite;

import android.app.Application;

import com.tjrushby.runlite.injection.components.AppComponent;
import com.tjrushby.runlite.injection.components.DaggerAppComponent;

import timber.log.Timber;

public class App extends Application {
    public static final int GPS_ACCURACY_BAD_THRESHOLD = 20;
    public static final int GPS_ACCURACY_GOOD_THRESHOLD = 10;

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        appComponent = DaggerAppComponent.builder().build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
