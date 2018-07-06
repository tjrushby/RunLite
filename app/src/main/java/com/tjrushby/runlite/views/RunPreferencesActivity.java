package com.tjrushby.runlite.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.RunPreferencesContract;
import com.tjrushby.runlite.injection.modules.RunPreferencesActivityModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunPreferencesActivity extends BaseActivity
        implements RunPreferencesContract.Activity,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    protected Intent intent;
    @Inject
    protected RunPreferencesContract.Presenter presenter;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getAppComponent()
                .plus(new RunPreferencesActivityModule(this))
                .inject(this);

        setContentView(R.layout.activity_run_preferences);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(this.getTitle());

        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new RunPreferencesFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @Override
    protected void onPause() {
        presenter.onActivityPaused();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_dark_mode_key))) {
            presenter.onThemeChanged();
        } else if(key.equals(getString(R.string.pref_distance_units_key))) {
            presenter.onDistanceUnitsChanged();
        }
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void endActivityWithIntent() {
        startActivity(intent
                .setClass(this, MainActivity.class)
                .putExtra("UNITS_CHANGED", true)
        );
        
        this.finish();
    }

    @Override
    public void restartActivity() {
        intent.setClass(this, RunPreferencesActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    @Override
    public void registerSharedPreferencesListener() {
        super.sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void unregisterSharedPreferencesListener() {
        super.sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}
