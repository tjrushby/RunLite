package com.tjrushby.runlite.contracts;

import android.content.Context;

public interface MainContract {
    interface Activity {
        void openDrawerMenu();

        void displayProgressBar(boolean display);

        void displayNoRuns(boolean display);

        void refreshRecyclerView();

        void startRunActivity();

        void setRunTotals(String totalRuns, String totalDistance, String totalTime);

        Context getContext();
    }

    interface Presenter {
        void onActivityCreated();

        void onActivityResumed();

        void onHomeOptionsItemSelected();

        void onFabStartRunPressed();

        void onDataAvailable();

        void onDataNotAvailable();
    }
}
