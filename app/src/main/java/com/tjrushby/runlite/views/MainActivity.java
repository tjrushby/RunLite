package com.tjrushby.runlite.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainContract.Activity {
    @Inject
    public Intent intentRunActivity;
    @Inject
    public LinearLayoutManager layoutManager;
    @Inject
    public MainContract.Presenter presenter;
    @Inject
    public RunModelAdapter adapter;

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
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        App.getAppComponent()
                .plus(new MainActivityModule(this))
                .inject(this);

        setSupportActionBar(toolbar);

        presenter.onActivityCreated();

        recyclerView.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        Timber.d("layoutManager.stackFromEnd: " + layoutManager.getStackFromEnd());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @OnClick(R.id.fabStartRun)
    public void fabClicked() {
        presenter.onFabStartRunPressed();
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
    public void startRunActivity() {
        intentRunActivity.setClass(this, RunActivity.class);
        startActivity(intentRunActivity);
    }

    @Override
    public void setRunTotals(String totalRuns, String totalDistance, String totalTime) {
        tvTotalDistance.setText(totalDistance);
        tvTotalRuns.setText(totalRuns);
        tvTotalTime.setText(totalTime);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
