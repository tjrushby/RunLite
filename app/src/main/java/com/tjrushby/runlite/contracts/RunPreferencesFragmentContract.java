package com.tjrushby.runlite.contracts;

public interface RunPreferencesFragmentContract {
    interface Fragment {
        void updateStringFormatterDistanceUnits();
    }

    interface Presenter {
        void onDistanceUnitsChanged();
    }
}
