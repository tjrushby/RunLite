package com.tjrushby.runlite.injection.modules;

import android.content.Intent;

import com.tjrushby.runlite.contracts.RunPreferencesContract;
import com.tjrushby.runlite.injection.scopes.RunPreferencesScope;
import com.tjrushby.runlite.presenters.RunPreferencesPresenter;

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

    @Provides
    @RunPreferencesScope
    RunPreferencesContract.Presenter providePresenter(RunPreferencesContract.Activity activity) {
        return new RunPreferencesPresenter(activity);
    }

    @Provides
    @RunPreferencesScope
    Intent providesIntent() {
        return new Intent();
    }
}
