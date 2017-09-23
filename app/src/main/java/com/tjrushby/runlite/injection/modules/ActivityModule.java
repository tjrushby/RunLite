package com.tjrushby.runlite.injection.modules;

import android.content.Context;
import android.content.Intent;

import dagger.Module;
import dagger.Provides;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.services.RunService;

@Module
public class ActivityModule {
    private RunContract.Activity activity;

    public ActivityModule(RunContract.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @RunningActivityScope
    RunContract.Activity provideActivity() {
        return activity;
    }

    @Provides
    Intent provideIntent(Context context) {
        return new Intent(context, RunService.class);
    }
}
