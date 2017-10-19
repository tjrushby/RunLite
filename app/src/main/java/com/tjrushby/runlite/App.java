package com.tjrushby.runlite;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.tjrushby.runlite.data.AppDatabase;
import com.tjrushby.runlite.injection.components.AppComponent;
import com.tjrushby.runlite.injection.components.DaggerAppComponent;

import timber.log.Timber;

public class App extends Application {
    public static final int GPS_ACCURACY_BAD_THRESHOLD = 20;
    public static final int GPS_ACCURACY_GOOD_THRESHOLD = 10;

    private static AppComponent appComponent;
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        appComponent = DaggerAppComponent.builder().build();
        database = Room.databaseBuilder(this, AppDatabase.class, "RunLite").build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}
