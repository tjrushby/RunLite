package com.tjrushby.runlite.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.adapters.RunModelAdapter;
import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.injection.modules.MainActivityModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.Activity {
    @Inject
    public Intent intentRunActivity;
    @Inject
    public LinearLayoutManager layoutManager;
    @Inject
    public MainContract.Presenter presenter;
    @Inject
    public RunModelAdapter adapter;

    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
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

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.fabStartRun)
    public void fabClicked() {
        presenter.onFabStartRunPressed();
    }

    @Override
    public void startRunActivity() {
        intentRunActivity.setClass(this, RunActivity.class);
        startActivity(intentRunActivity);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
