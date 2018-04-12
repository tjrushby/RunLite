package com.tjrushby.runlite.contracts;

import android.content.Context;

public interface MainContract {
    interface Activity {
        void openDrawerMenu();

        void displayProgressBar(boolean display);

        void displayNoRuns(boolean display);

        void refreshRecyclerView();

        void startRunActivity();

        void startRunPreferencesActivity();

        void setRunTotals(String totalRuns, String totalDistance, String totalTime);

        Context getContext();
    }

    interface Presenter {
        void onActivityCreated();

        void onActivityResumed();

        void onHomeOptionsItemSelected();

        void onNavItemSettingsSelected();

        void onFabStartRunPressed();

        void onDataAvailable();

        void onDataNotAvailable();
    }
}
