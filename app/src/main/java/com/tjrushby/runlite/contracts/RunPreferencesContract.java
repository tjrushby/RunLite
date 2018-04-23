package com.tjrushby.runlite.contracts;


public interface RunPreferencesContract {
    interface Activity {
        void restartActivity();

        void registerSharedPreferencesListener();

        void unregisterSharedPreferencesListener();
    }

    interface Presenter {
        void onActivityResumed();

        void onActivityPaused();

        void onThemeChanged();
    }
}
