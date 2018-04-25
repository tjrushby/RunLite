package com.tjrushby.runlite.presenters;


import com.tjrushby.runlite.contracts.MainContract;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.Activity activity;

    public MainPresenter(MainContract.Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityCreated() {
        activity.displayProgressBar(true);
    }

    @Override
    public void onActivityResumed() {
        if(activity.getDrawerVisible()) {
            activity.closeDrawerMenu();
        }

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
    public void onNavItemSettingsSelected() {
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
}
