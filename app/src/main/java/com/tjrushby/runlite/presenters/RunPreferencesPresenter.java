package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.RunPreferencesContract;

public class RunPreferencesPresenter implements RunPreferencesContract.Presenter {
    private RunPreferencesContract.Activity activity;

    public RunPreferencesPresenter(RunPreferencesContract.Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityResumed() {
        activity.registerSharedPreferencesListener();
    }

    @Override
    public void onActivityPaused() {
        activity.unregisterSharedPreferencesListener();
    }

    @Override
    public void onThemeChanged() {
        activity.restartActivity();
    }
}
