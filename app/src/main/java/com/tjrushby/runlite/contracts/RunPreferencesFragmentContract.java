package com.tjrushby.runlite.contracts;

public interface RunPreferencesFragmentContract {
    interface Fragment {
        void registerSharedPreferencesListener();

        void unregisterSharedPreferencesListener();

        void updateStringFormatterDistanceUnits();
    }

    interface Presenter {
        void onFragmentResumed();

        void onFragmentPaused();

        void onDistanceUnitsChanged();
    }
}
