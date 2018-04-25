package com.tjrushby.runlite.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.adapters.RunModelAdapter;
import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.injection.modules.MainActivityModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainContract.Activity {
    @Inject
    public Intent intent;
    @Inject
    public LinearLayoutManager layoutManager;
    @Inject
    public MainContract.Presenter presenter;
    @Inject
    public RunModelAdapter adapter;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.onHomeOptionsItemSelected();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fabStartRun)
    public void fabClicked() {
        presenter.onFabStartRunPressed();
    }

    @Override
    public void openDrawerMenu() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void closeDrawerMenu() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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
    public void displayNoRuns(boolean display) {
        if(display) {
            tvNoRuns.setVisibility(View.VISIBLE);
        } else {
            tvNoRuns.setVisibility(View.GONE);
        }
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
    public void startRunPreferencesActivity() {
        drawerLayout.closeDrawers();
        intent.setClass(this, RunPreferencesActivity.class);
        startActivity(intent);
    }

    @Override
    public void startRunActivity() {
        intent.setClass(this, RunActivity.class);
        startActivity(intent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setRunTotals(String totalRuns, String totalDistance, String totalTime) {
        tvTotalDistance.setText(totalDistance);
        tvTotalRuns.setText(totalRuns);
        tvTotalTime.setText(totalTime);
    }
}
