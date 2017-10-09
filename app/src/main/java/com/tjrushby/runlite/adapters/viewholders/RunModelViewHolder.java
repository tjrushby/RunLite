package com.tjrushby.runlite.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.auto.factory.AutoFactory;
import com.tjrushby.runlite.R;

import butterknife.BindView;
import butterknife.ButterKnife;

@AutoFactory(implementing = ModelViewHolderFactory.class)
public class RunModelViewHolder extends RecyclerView.ViewHolder implements ModelViewHolder {
    @BindView(R.id.tvDateTime)
    public TextView tvDateTime;
    @BindView(R.id.tvTimeElapsed)
    public TextView tvTimeElapsed;
    @BindView(R.id.tvDistance)
    public TextView tvDistance;
    @BindView(R.id.tvAveragePace)
    public TextView tvAveragePace;

    public RunModelViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_run, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setDateTime(String dateTime) {
        tvDateTime.setText(dateTime);
    }

    @Override
    public void setTimeElapsed(String timeElapsed) {
        tvTimeElapsed.setText(timeElapsed);
    }

    @Override
    public void setDistance(String distance) {
        tvDistance.setText(distance);
    }

    @Override
    public void setAveragePace(String averagePace) {
        tvAveragePace.setText(averagePace);
    }
}
