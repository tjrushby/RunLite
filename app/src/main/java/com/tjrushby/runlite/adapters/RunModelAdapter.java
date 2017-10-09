package com.tjrushby.runlite.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolder;
import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolderFactory;
import com.tjrushby.runlite.contracts.MainContract;

public class RunModelAdapter extends RecyclerView.Adapter<RunModelViewHolder> {
    private MainContract.ListPresenter presenter;
    private RunModelViewHolderFactory factory;

    public RunModelAdapter(MainContract.ListPresenter presenter, RunModelViewHolderFactory factory) {
        this.presenter = presenter;
        this.factory = factory;
    }

    @Override
    public RunModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return factory.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RunModelViewHolder holder, int position) {
        presenter.onViewHolderBoundAtPosition(holder, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getRowCount();
    }
}
