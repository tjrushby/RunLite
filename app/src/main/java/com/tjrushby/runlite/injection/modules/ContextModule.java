package com.tjrushby.runlite.injection.modules;


import android.content.Context;

import dagger.Module;
import dagger.Provides;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;

@Module
public class ContextModule {
    private final Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @RunningActivityScope
    Context providesContext() {
        return context;
    }
}
