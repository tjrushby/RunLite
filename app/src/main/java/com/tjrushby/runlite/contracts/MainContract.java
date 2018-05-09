package com.tjrushby.runlite.contracts;

import android.content.Context;

public interface MainContract {
    interface Activity {
        void endActivity();

        void restartActivityWithFadeInOut();

        void startRunActivity();

        void startRunPreferencesActivity();

        void displayProgressBar(boolean display);

        void expandToolbar();

        void refreshRecyclerView();

        void scrollToTop();

        void displayNoRuns(boolean display);

        void openDrawerMenu();

        void closeDrawerMenu();

        Context getContext();

        boolean getDarkThemeEnabled();

        boolean getDrawerVisible();

        boolean getThemeChanged();

        void setSwitchDarkModeChecked(boolean checked);

        void setRunTotals(String totalRuns, String totalDistance, String totalTime);

        void setSharedPrefsDarkMode();
    }

    interface Presenter {
        void onActivityCreated();

        void onActivityResumed();

        void onHomeOptionsItemSelected();

        void onBackPressed();

        void onNavItemDarkModeChecked();

        void onNavItemSettingsSelected();

        void onFabStartRunPressed();

        void onDataAvailable(boolean newData);

        void onDataNotAvailable();
    }
}
