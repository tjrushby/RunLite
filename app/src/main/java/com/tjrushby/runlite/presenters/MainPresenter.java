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
        activity.refreshRecyclerView();
        activity.displayProgressBar(false);
    }

    @Override
    public void onFabStartRunPressed() {
        activity.startRunActivity();
    }
}
