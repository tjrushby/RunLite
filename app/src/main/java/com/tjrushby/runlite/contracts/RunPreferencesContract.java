package com.tjrushby.runlite.contracts;


public interface RunPreferencesContract {
    interface Activity {
        void restartActivity();

        void endActivity();

        void endActivityWithIntent();

        void registerSharedPreferencesListener();

        void unregisterSharedPreferencesListener();
    }

    interface Presenter {
        void onActivityResumed();

        void onActivityPaused();

        void onBackPressed();

        void onDistanceUnitsChanged();

        void onThemeChanged();
    }
}
