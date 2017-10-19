package com.tjrushby.runlite.injection.modules;


import android.os.Handler;

import dagger.Module;
import dagger.Provides;

@Module
public class HandlerModule {
    @Provides
    Handler providesHandler() {
        return new Handler();
    }
}
