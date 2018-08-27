package com.tjrushby.runlite.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.RunPreferencesFragmentContract;
import com.tjrushby.runlite.injection.modules.RunPreferencesFragmentModule;
import com.tjrushby.runlite.util.StringFormatter;

import javax.inject.Inject;

public class RunPreferencesFragment extends PreferenceFragmentCompat
        implements RunPreferencesFragmentContract.Fragment,
        SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    protected RunPreferencesFragmentContract.Presenter presenter;
    @Inject
    protected StringFormatter formatter;

    private SharedPreferences sharedPrefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_run_preferences);
        sharedPrefs = getPreferenceManager().getSharedPreferences();
        presenter.onFragmentCreated();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onFragmentResumed();
    }

    @Override
    public void onPause() {
        presenter.onFragmentPaused();
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        App.getAppComponent()
                .plus(new RunPreferencesFragmentModule(this))
                .inject(this);

        super.onAttach(context);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_distance_units_key))) {
            presenter.onDistanceUnitsChanged();
        } else if(key.equals(getString(R.string.pref_audio_cue_type_key))) {
            presenter.onAudioCueTypeChanged();
        }
    }

    @Override
    public void registerSharedPreferencesListener() {
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void unregisterSharedPreferencesListener() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void disableAudioCueDistancePref() {
        findPreference(getString(R.string.pref_audio_cue_interval_distance_key)).setEnabled(false);
    }

    @Override
    public void enableAudioCueDistancePref() {
        findPreference(getString(R.string.pref_audio_cue_interval_distance_key)).setEnabled(true);
    }

    @Override
    public void disableAudioCueTimePref() {
        findPreference(getString(R.string.pref_audio_cue_interval_time_key)).setEnabled(false);
    }

    @Override
    public void enableAudioCueTimePref() {
        findPreference(getString(R.string.pref_audio_cue_interval_time_key)).setEnabled(true);
    }

    @Override
    public String getAudioCueType() {
        return sharedPrefs.getString(getString(R.string.pref_audio_cue_type_key), "distance");
    }

    @Override
    public void setPrefAudioCueFrequencyDistanceUnits() {
        // get the distance intervals
        CharSequence[] entries =
                getResources().getStringArray(R.array.audio_cue_interval_distance_options);

        // add the distance units as a suffix
        for (int i = 0; i < entries.length; i++) {
            entries[i] = entries[i] + " " + formatter.getDistanceUnitsString();
        }

        // set the entry options
        android.support.v7.preference.ListPreference lp =
                ((android.support.v7.preference.ListPreference)
                        findPreference(getString(R.string.pref_audio_cue_interval_distance_key)));

        lp.setEntries(entries);

        // set the summary to reflect the change
        lp.setSummary(lp.getValue() + " " + formatter.getDistanceUnitsString());
    }

    @Override
    public void updateStringFormatterDistanceUnits() {
        formatter.getDistanceUnitsFromSharedPreferences();
    }
}
