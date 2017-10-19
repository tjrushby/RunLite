package com.tjrushby.runlite.injection.modules;


import android.arch.persistence.room.Room;
import android.content.Context;

import com.tjrushby.runlite.data.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    AppDatabase providesAppDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "RunLite").build();
    }
}
