package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.RunPreferencesFragmentContract;

public class RunPreferencesFragmentPresenter implements RunPreferencesFragmentContract.Presenter {
    private RunPreferencesFragmentContract.Fragment fragment;

    public RunPreferencesFragmentPresenter(RunPreferencesFragmentContract.Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onFragmentCreated() {
        setCorrectAudioCueFrequencyPrefEnabled();
        fragment.setPrefAudioCueFrequencyDistanceUnits();
    }

    @Override
    public void onFragmentResumed() {
        fragment.registerSharedPreferencesListener();
    }

    @Override
    public void onFragmentPaused() {
        fragment.unregisterSharedPreferencesListener();
    }

    @Override
    public void onDistanceUnitsChanged() {
        fragment.updateStringFormatterDistanceUnits();
        fragment.setPrefAudioCueFrequencyDistanceUnits();
    }

    @Override
    public void onAudioCueTypeChanged() {
        setCorrectAudioCueFrequencyPrefEnabled();
    }

    private void setCorrectAudioCueFrequencyPrefEnabled() {
        if(fragment.getAudioCueType().equals("distance")) {
            // enable distance interval pref, disable audio interval pref
            fragment.enableAudioCueDistancePref();
            fragment.disableAudioCueTimePref();
        } else {
            // enable time interval pref, disable distance interval pref
            fragment.enableAudioCueTimePref();
            fragment.disableAudioCueDistancePref();
        }
    }
}
