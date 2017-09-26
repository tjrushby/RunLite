package com.tjrushby.runlite.injection.modules;


import android.content.Context;

import com.tjrushby.runlite.injection.scopes.RunningActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class RunActivityContextModule {
    private final Context context;

    public RunActivityContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @RunningActivityScope
    Context providesContext() {
        return context;
    }
}
