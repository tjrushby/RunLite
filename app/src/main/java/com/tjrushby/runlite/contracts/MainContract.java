package com.tjrushby.runlite.contracts;

import android.content.Context;

import com.tjrushby.runlite.models.RunWithLatLng;

public interface MainContract {
    interface Activity {
        void endActivity();

        void restartActivityWithFadeInOut();

        void startRunActivity();

        void startRunPreferencesActivity();

        void expandToolbar();

        void refreshRecyclerView();

        void scrollToTop();

        void displayNoRuns(boolean display);

        void displaySnackBarRunDeleted(int position, RunWithLatLng swipedRun);

        void openDrawerMenu();

        void closeDrawerMenu();

        void showLoadingOverlay();

        void hideLoadingOverlay();

        void calculateRunTotals();

        void removeRunFromDatabase(RunWithLatLng run);

        void removeRunFromList(int position);

        void restoreRunToList(int position, RunWithLatLng run);

        Context getContext();

        boolean isAudioCueEnabled();

        boolean getDarkThemeEnabled();

        boolean getDrawerVisible();

        boolean getThemeChanged();

        void setSwitchAudioCueChecked(boolean checked);

        void setSwitchDarkModeChecked(boolean checked);

        void setRunTotals(String totalRuns, String totalDistance, String totalTime);

        void setSharedPrefsAudioCueEnabled();

        void setSharedPrefsDarkMode();
    }

    interface Presenter {
        void onActivityCreated();

        void onActivityResumed();

        void onHomeOptionsItemSelected();

        void onBackPressed();

        void onNavItemAudioCueChecked();

        void onNavItemDarkModeChecked();

        void onNavItemSettingsSelected();

        void onFabStartRunPressed();

        void onDataAvailable(boolean newData);

        void onDataNotAvailable();

        void onDistanceUnitsChanged();

        void onRunDeleted();

        void onRunSwiped(int position, RunWithLatLng run);

        void onRunUpdated();

        void onUndoAction(int position, RunWithLatLng run);

        void onUndoDismissed(RunWithLatLng run);
    }
}
