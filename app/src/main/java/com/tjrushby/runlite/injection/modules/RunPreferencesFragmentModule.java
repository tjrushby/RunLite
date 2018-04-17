package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.RunPreferencesFragmentContract;
import com.tjrushby.runlite.injection.scopes.RunPreferencesFragmentScope;
import com.tjrushby.runlite.presenters.RunPreferencesFragmentPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class RunPreferencesFragmentModule {
    private RunPreferencesFragmentContract.Fragment fragment;

    public RunPreferencesFragmentModule(RunPreferencesFragmentContract.Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @RunPreferencesFragmentScope
    RunPreferencesFragmentContract.Fragment provideFragment() {
        return fragment;
    }

    @Provides
    @RunPreferencesFragmentScope
    RunPreferencesFragmentContract.Presenter providePresenter(RunPreferencesFragmentContract.Fragment fragment) {
        return new RunPreferencesFragmentPresenter(fragment);
    }
}
