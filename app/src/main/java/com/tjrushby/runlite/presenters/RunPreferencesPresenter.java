package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.RunPreferencesContract;

public class RunPreferencesPresenter implements RunPreferencesContract.Presenter {
    private boolean changedUnits;

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
    public void onBackPressed() {
        if(!changedUnits) {
            activity.endActivity();
        } else {
            activity.endActivityWithIntent();
        }
    }

    @Override
    public void onDistanceUnitsChanged() {
        // toggle changedUnits, this way it will switch back to false if the user selects the
        // initial distance units again, preventing recalculate the run totals again for no reason
        changedUnits = !changedUnits;
    }

    @Override
    public void onThemeChanged() {
        activity.restartActivity();
    }
}
