package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.RunPreferencesFragmentContract;

public class RunPreferencesFragmentPresenter implements RunPreferencesFragmentContract.Presenter {
    private RunPreferencesFragmentContract.Fragment fragment;

    public RunPreferencesFragmentPresenter(RunPreferencesFragmentContract.Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onFragmentResumed() {
        fragment.registerSharedPreferencesListener();
    }

    @Override
    public void onFragmentPaused() {
        fragment.unregisterSharedPreferencesListener();
    }

    @Override
    public void onDistanceUnitsChanged() {
        fragment.updateStringFormatterDistanceUnits();
    }
}
