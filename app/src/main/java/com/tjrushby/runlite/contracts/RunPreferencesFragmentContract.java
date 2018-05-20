package com.tjrushby.runlite.contracts;

public interface RunPreferencesFragmentContract {
    interface Fragment {
        void registerSharedPreferencesListener();

        void unregisterSharedPreferencesListener();

        void disableAudioCueDistancePref();

        void enableAudioCueDistancePref();

        void disableAudioCueTimePref();

        void enableAudioCueTimePref();

        String getAudioCueType();

        void setPrefAudioCueFrequencyDistanceUnits();

        void updateStringFormatterDistanceUnits();
    }

    interface Presenter {
        void onFragmentCreated();

        void onFragmentResumed();

        void onFragmentPaused();

        void onAudioCueTypeChanged();

        void onDistanceUnitsChanged();
    }
}
