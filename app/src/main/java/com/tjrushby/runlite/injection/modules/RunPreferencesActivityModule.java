package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.RunPreferencesContract;
import com.tjrushby.runlite.injection.scopes.RunPreferencesScope;

import dagger.Module;
import dagger.Provides;

@Module
public class RunPreferencesActivityModule {
    private RunPreferencesContract.Activity activity;

    public RunPreferencesActivityModule(RunPreferencesContract.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @RunPreferencesScope
    RunPreferencesContract.Activity provideActivity() {
        return activity;
    }
}
