package com.tjrushby.runlite.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.adapters.RunModelAdapter;
import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.injection.modules.MainActivityModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements MainContract.Activity, CompoundButton.OnCheckedChangeListener {
    @Inject
    public Intent intent;
    @Inject
    public LinearLayoutManager layoutManager;
    @Inject
    public MainContract.Presenter presenter;
    @Inject
    public RunModelAdapter adapter;

    @BindView(R.id.appBarLayout)
    protected AppBarLayout appBarLayout;
    @BindView(R.id.drawerLayout)
    protected DrawerLayout drawerLayout;
    @BindView(R.id.navView)
    protected NavigationView navView;
    @BindView(R.id.progressBar)
    public ProgressBar progressBar;
    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.tvNoRuns)
    protected TextView tvNoRuns;
    @BindView(R.id.tvTotalDistance)
    protected TextView tvTotalDistance;
    @BindView(R.id.tvTotalRuns)
    protected TextView tvTotalRuns;
    @BindView(R.id.tvTotalTime)
    protected TextView tvTotalTime;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private SwitchCompat switchAudioCue;
    private SwitchCompat switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getAppComponent()
                .plus(new MainActivityModule(this))
                .inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter.onActivityCreated();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        recyclerView.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        navView.setNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.nav_settings:
                    presenter.onNavItemSettingsSelected();
            }

            return true;
        });

        if(navView.getMenu().findItem(R.id.nav_dark_mode).getActionView() instanceof SwitchCompat) {
            switchDarkMode =
                    (SwitchCompat) navView.getMenu().findItem(R.id.nav_dark_mode).getActionView();
            switchDarkMode.setOnCheckedChangeListener(this);
        }

        if(navView.getMenu().findItem(R.id.nav_audio_cues).getActionView() instanceof SwitchCompat) {
            switchAudioCue =
                    (SwitchCompat) navView.getMenu().findItem(R.id.nav_audio_cues).getActionView();
            switchAudioCue.setOnCheckedChangeListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.hasExtra("UPDATED")) {
            presenter.onRunUpdated();
        } else if(intent.hasExtra("UNITS_CHANGED")) {
            presenter.onDistanceUnitsChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.onHomeOptionsItemSelected();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @OnClick(R.id.fabStartRun)
    public void fabClicked() {
        presenter.onFabStartRunPressed();
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void restartActivityWithFadeInOut() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent.setClass(this, this.getClass()));
        this.finish();
    }

    @Override
    public void startRunActivity() {
        intent.setClass(this, RunActivity.class);
        startActivity(intent);
    }

    @Override
    public void startRunPreferencesActivity() {
        intent.setClass(this, RunPreferencesActivity.class);
        startActivity(intent);
    }

    @Override
    public void displayProgressBar(boolean display) {
        if(display) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void expandToolbar() {
        appBarLayout.setExpanded(true);
    }

    @Override
    public void refreshRecyclerView() {
        adapter.loadRuns(this);
    }

    @Override
    public void scrollToTop() {
        // scroll to itemCount - 1 as recyclerView.layoutManager uses reverse layout
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    @Override
    public void displayNoRuns(boolean display) {
        if(display) {
            tvNoRuns.setVisibility(View.VISIBLE);
        } else {
            tvNoRuns.setVisibility(View.GONE);
        }
    }

    @Override
    public void displayRunDeletedToast() {
        Toast.makeText(this, R.string.toast_run_deleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openDrawerMenu() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void closeDrawerMenu() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void calculateRunTotals() {
        adapter.calculateRunTotals(this);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean isAudioCueEnabled() {
        return super.sharedPrefs.getBoolean(getString(R.string.pref_audio_cue_enabled_key), true);
    }

    @Override
    public boolean getDarkThemeEnabled() {
        return super.getDarkThemeEnabled();
    }

    @Override
    public boolean getDrawerVisible() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean getThemeChanged() {
        return super.changedTheme;
    }

    @Override
    public void setSwitchAudioCueChecked(boolean checked) {
        switchAudioCue.setChecked(checked);
    }

    @Override
    public void setSwitchDarkModeChecked(boolean checked) {
        // manually find the view instead of using the variable as this needs to be called in
        // onCreate before instantiating the variable and setting onCheckedChangedListener to
        // avoid a loop that restarts the Activity
        if(navView.getMenu().findItem(R.id.nav_dark_mode).getActionView() instanceof SwitchCompat) {
            ((SwitchCompat) navView.getMenu().findItem(R.id.nav_dark_mode).getActionView())
                    .setChecked(checked);
        }
    }

    @Override
    public void setRunTotals(String totalRuns, String totalDistance, String totalTime) {
        tvTotalDistance.setText(totalDistance);
        tvTotalRuns.setText(totalRuns);
        tvTotalTime.setText(totalTime);
    }

    @Override
    public void setSharedPrefsAudioCueEnabled() {
        super.sharedPrefs.edit()
                .putBoolean(
                        getString(R.string.pref_audio_cue_enabled_key),
                        switchAudioCue.isChecked())
                .apply();
    }

    @Override
    public void setSharedPrefsDarkMode() {
        super.sharedPrefs.edit()
                .putBoolean(
                        getString(R.string.pref_dark_mode_key),
                        switchDarkMode.isChecked())
                .apply();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.nav_dark_mode:
                presenter.onNavItemDarkModeChecked();
                break;

            case R.id.nav_audio_cues:
                presenter.onNavItemAudioCueChecked();
                break;
        }
    }
}
