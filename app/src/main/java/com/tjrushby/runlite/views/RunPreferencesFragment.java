package com.tjrushby.runlite.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
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
        if(key.equals(getString(R.string.pref_key_distance_units))) {
            presenter.onDistanceUnitsChanged();
        }
    }

    @Override
    public void updateStringFormatterDistanceUnits() {
        formatter.getDistanceUnitsFromSharedPreferences();
    }
}
