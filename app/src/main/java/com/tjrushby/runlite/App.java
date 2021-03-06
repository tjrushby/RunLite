package com.tjrushby.runlite;

import android.app.Application;

import com.google.android.gms.maps.MapsInitializer;
import com.tjrushby.runlite.injection.components.AppComponent;
import com.tjrushby.runlite.injection.components.DaggerAppComponent;
import com.tjrushby.runlite.injection.modules.AppModule;

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

        // initialize Google Maps API classes here to reduce load time when selecting
        // first DetailsActivity
        MapsInitializer.initialize(this);

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
