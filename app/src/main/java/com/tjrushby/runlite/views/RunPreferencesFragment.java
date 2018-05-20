package com.tjrushby.runlite.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.RunPreferencesFragmentContract;
import com.tjrushby.runlite.injection.modules.RunPreferencesFragmentModule;
import com.tjrushby.runlite.util.StringFormatter;

import javax.inject.Inject;

public class RunPreferencesFragment extends PreferenceFragment
        implements RunPreferencesFragmentContract.Fragment,
        SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    protected RunPreferencesFragmentContract.Presenter presenter;
    @Inject
    protected StringFormatter formatter;

    private SharedPreferences sharedPrefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        findPreference(getString(R.string.pref_audio_cue_distance_distance_key)).setEnabled(false);
    }

    @Override
    public void enableAudioCueDistancePref() {
        findPreference(getString(R.string.pref_audio_cue_distance_distance_key)).setEnabled(true);
    }

    @Override
    public void disableAudioCueTimePref() {
        findPreference(getString(R.string.pref_audio_cue_distance_time_key)).setEnabled(false);
    }

    @Override
    public void enableAudioCueTimePref() {
        findPreference(getString(R.string.pref_audio_cue_distance_time_key)).setEnabled(true);
    }

    @Override
    public String getAudioCueType() {
        return sharedPrefs.getString(getString(R.string.pref_audio_cue_type_key), "distance");
    }

    @Override
    public void setPrefAudioCueFrequencyDistanceUnits() {
        CharSequence[] entries =
                getResources().getStringArray(R.array.audio_cue_distance_distance_options);

        for (int i = 0; i < entries.length; i++) {
            entries[i] = entries[i] + " " + formatter.getDistanceUnitsString();
        }

        ((ListPreference) findPreference(getString(R.string.pref_audio_cue_distance_distance_key)))
                .setEntries(entries);
    }

    @Override
    public void updateStringFormatterDistanceUnits() {
        formatter.getDistanceUnitsFromSharedPreferences();
    }
}
