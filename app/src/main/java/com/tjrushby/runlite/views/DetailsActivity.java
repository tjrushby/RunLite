package com.tjrushby.runlite.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.DetailsContract;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsActivity extends AppCompatActivity implements DetailsContract.Activity {
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
    }
}
