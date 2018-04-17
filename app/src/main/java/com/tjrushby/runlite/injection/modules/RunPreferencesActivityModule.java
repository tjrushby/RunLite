package com.tjrushby.runlite.injection.modules;

import android.content.SharedPreferences;

import com.tjrushby.runlite.contracts.RunPreferencesContract;
import com.tjrushby.runlite.injection.scopes.RunPreferencesScope;

import dagger.Module;
import dagger.Provides;

@Module
public class RunPreferencesModule {
    private RunPreferencesContract.Activity activity;
    private RunPreferencesContract.Fragment fragment;

    public RunPreferencesModule(RunPreferencesContract.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @RunPreferencesScope
    RunPreferencesContract.Activity provideActivity() {
        return activity;
    }

    @Provides
    @RunPreferencesScope
    RunPreferencesContract.Fragment provideFragment() {
        return fragment;
    }
}
