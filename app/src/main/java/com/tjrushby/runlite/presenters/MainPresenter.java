package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.models.RunWithLatLng;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.Activity activity;

    public MainPresenter(MainContract.Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityCreated() {
        activity.displayProgressBar(true);

        if(activity.getThemeChanged()) {
            activity.setSwitchDarkModeChecked(activity.getDarkThemeEnabled());
        }
    }

    @Override
    public void onActivityResumed() {
        if(activity.getDrawerVisible()) {
            activity.closeDrawerMenu();
        }

        if(activity.getThemeChanged()) {
            activity.setSwitchDarkModeChecked(activity.getDarkThemeEnabled());
        }

        activity.setSwitchAudioCueChecked(activity.isAudioCueEnabled());

        activity.refreshRecyclerView();
    }

    @Override
    public void onHomeOptionsItemSelected() {
        activity.openDrawerMenu();
    }

    @Override
    public void onBackPressed() {
        if(activity.getDrawerVisible()) {
            activity.closeDrawerMenu();
        } else {
            activity.endActivity();
        }
    }

    @Override
    public void onNavItemAudioCueChecked() {
        activity.setSharedPrefsAudioCueEnabled();
    }

    @Override
    public void onNavItemDarkModeChecked() {
        activity.closeDrawerMenu();
        activity.setSharedPrefsDarkMode();
        activity.restartActivityWithFadeInOut();
    }

    @Override
    public void onNavItemSettingsSelected() {
        activity.closeDrawerMenu();
        activity.startRunPreferencesActivity();
    }

    @Override
    public void onFabStartRunPressed() {
        activity.startRunActivity();
    }

    @Override
    public void onDataAvailable(boolean newData) {
        activity.displayNoRuns(false);
        activity.displayProgressBar(false);

        if(newData) {
            activity.scrollToTop();
            activity.expandToolbar();
        }
    }

    @Override
    public void onDataNotAvailable() {
        activity.displayNoRuns(true);
        activity.displayProgressBar(false);
    }

    @Override
    public void onDistanceUnitsChanged() {
        activity.calculateRunTotals();
    }

    @Override
    public void onRunDeleted() {
    }

    @Override
    public void onRunSwiped(int position, RunWithLatLng run) {
        activity.removeRunFromList(position);
        activity.displaySnackBarRunDeleted(position, run);
    }

    @Override
    public void onRunUpdated() {
        activity.calculateRunTotals();
    }

    @Override
    public void onUndoAction(int position, RunWithLatLng run) {
        activity.restoreRunToList(position, run);
    }

    @Override
    public void onUndoDismissed(RunWithLatLng run) {
        activity.removeRunFromDatabase(run);
    }
}
