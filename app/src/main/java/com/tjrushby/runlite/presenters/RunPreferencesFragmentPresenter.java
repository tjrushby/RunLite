package com.tjrushby.runlite.presenters;


import com.tjrushby.runlite.contracts.RunPreferencesContract;

public class RunPreferencesPresenter implements RunPreferencesContract.Presenter {
    private RunPreferencesContract.Activity activity;
    private RunPreferencesContract.Fragment fragment;

    public RunPreferencesPresenter(RunPreferencesContract.Activity activity, RunPreferencesContract.Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    public void onDistanceUnitsChanged() {
        fragment.updateStringFormatterDistanceUnits();
    }
}
