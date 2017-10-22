package com.tjrushby.runlite.presenters;


import com.tjrushby.runlite.contracts.MainContract;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.Activity activity;

    public MainPresenter(MainContract.Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityResumed() {
        activity.refreshRecyclerView();
    }

    @Override
    public void onFabStartRunPressed() {
        activity.startRunActivity();
    }
}
