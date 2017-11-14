package com.tjrushby.runlite.injection.modules;


import android.arch.persistence.room.Room;
import android.content.Context;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.data.AppDatabase;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Named("app_context")
    @Singleton
    Context providesAppContext() {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    AppDatabase providesAppDatabase(@Named("app_context") Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "RunLite").build();
    }
}
