package com.tjrushby.runlite.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.util.StringFormatter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailsActivity extends AppCompatActivity implements DetailsContract.Activity {
    @Inject
    public DetailsContract.Presenter presenter;
    @Inject
    public RunContract.Model model;
    @Inject
    public StringFormatter formatter;

    @BindView(R.id.buttonDelete)
    protected Button buttonDelete;
    @BindView(R.id.buttonEdit)
    protected Button buttonEdit;
    @BindView(R.id.tvAveragePace)
    protected TextView tvAveragePace;
    @BindView(R.id.tvDistance)
    protected TextView tvDistance;
    @BindView(R.id.tvTimeElapsed)
    protected TextView tvTimeElapsed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        // inject dependencies
        App.getAppComponent().plus().inject(this);

        Timber.d(presenter.toString());
        Timber.d(model.toString());
    }
}
