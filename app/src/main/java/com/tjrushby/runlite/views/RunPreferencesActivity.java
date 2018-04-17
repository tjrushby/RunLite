package com.tjrushby.runlite.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tjrushby.runlite.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunPreferencesActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_run_preferences);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(this.getTitle());

        getFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new RunPreferencesFragment())
                .commit();
    }
}