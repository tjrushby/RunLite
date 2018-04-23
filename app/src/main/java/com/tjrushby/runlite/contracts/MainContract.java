package com.tjrushby.runlite.contracts;

import android.content.Context;

public interface MainContract {
    interface Activity {
        void openDrawerMenu();

        void closeDrawerMenu();

        void displayProgressBar(boolean display);

        void displayNoRuns(boolean display);

        void refreshRecyclerView();

        void startRunActivity();

        void startRunPreferencesActivity();

        Context getContext();

        void setRunTotals(String totalRuns, String totalDistance, String totalTime);
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
