package com.tjrushby.runlite.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolder;
import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolderFactory;
import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.data.RunDataSource;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.util.StringFormatter;
import com.tjrushby.runlite.views.DetailsActivity;
import com.tjrushby.runlite.views.MainActivity;

import java.util.List;

import timber.log.Timber;

public class RunModelAdapter extends RecyclerView.Adapter<RunModelViewHolder> {
    private List<RunWithLatLng> runsList;
    private MainContract.Presenter presenter;
    private RunModelViewHolderFactory factory;
    private RunRepository runRepository;
    private StringFormatter formatter;

    public RunModelAdapter(List<RunWithLatLng> runsList,
                           MainContract.Presenter presenter,
                           RunModelViewHolderFactory factory,
                           RunRepository runRepository,
                           StringFormatter formatter) {
        this.runsList = runsList;
        this.presenter = presenter;
        this.factory = factory;
        this.runRepository = runRepository;
        this.formatter = formatter;
    }

    @Override
    public RunModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return factory.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RunModelViewHolder holder, int position) {
        RunWithLatLng run = runsList.get(position);

        holder.setDateTime(formatter.dateToString(run.run.getDateTime()));
        holder.setTimeElapsed(formatter.secondsToTimeString(run.run.getTimeElapsed()));
        holder.setDistance(formatter.distanceToStringWithUnits(run.run.getDistanceTravelled()));
        holder.setAveragePace(formatter.averagePaceToTimeStringWithLabel(run.run.getAveragePace()));

        holder.itemView.setOnClickListener((View view) -> {
            if(view.getContext() instanceof MainActivity) {
                ((MainActivity) view.getContext()).displayProgressBar(true);
            }

            Intent intent = new Intent();
            intent.setClass(view.getContext(), DetailsActivity.class);
            intent.putExtra("runId", Long.toString(run.run.getId()));
            ((Activity) view.getContext()).startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return runsList.size();
    }

    public RunWithLatLng getItem(int position) {
        return runsList.get(position);
    }

    public void removeItem(int position) {
        runsList.remove(position);
        notifyItemRemoved(position);

        if(runsList.size() == 0) {
            presenter.onDataNotAvailable();
        }
    }

    public void restoreItem(int position, RunWithLatLng run) {
        if(runsList.size() == 0) {
            presenter.onDataAvailable(false);
        }

        runsList.add(position, run);
        notifyItemInserted(position);
    }

    public void loadRuns(Context context) {
        runRepository.getRuns(new RunDataSource.LoadRunsCallback() {
            @Override
            public void onRunsLoaded(List<RunWithLatLng> runs) {
                if(!runsList.equals(runs)) {
                    // clear the current contents of runsList and then repopulate using new records
                    // from the database
                    runsList.clear();
                    runsList.addAll(runs);

                    calculateRunTotals(context);

                    // notify presenter that there is new data available
                    presenter.onDataAvailable(true);
                } else {
                    // notify presenter that there is no new data available
                    presenter.onDataAvailable(false);
                }

                notifyDataSetChanged();
            }

            @Override
            public void onDataNotAvailable() {
                // set run totals toolbar
                if(context instanceof MainActivity) {
                    ((MainActivity) context).setRunTotals(
                            Integer.toString(0),
                            formatter.distanceToStringWithUnits(0),
                            formatter.secondsToTimeString(0)
                    );
                }

                runsList.clear();
                presenter.onDataNotAvailable();
                notifyDataSetChanged();
            }
        });
    }

    public void deleteRun(RunWithLatLng run) {
        runRepository.deleteRun(run.run, () -> {
            presenter.onRunDeleted();
        });
    }

    public void calculateRunTotals(Context context) {
        // calculate new run totals from data set
        double totalDistance = 0;
        int totalTime = 0;

        for (RunWithLatLng run : runsList) {
            totalDistance += run.run.getDistanceTravelled();
            totalTime += run.run.getTimeElapsed();
        }

        // set run totals toolbar
        if(context instanceof MainActivity) {
            ((MainActivity) context).setRunTotals(
                    Integer.toString(runsList.size()),
                    formatter.distanceToStringWithUnits(totalDistance),
                    formatter.secondsToTimeString(totalTime)
            );
        }
    }
}
