package com.tjrushby.runlite.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
        holder.setTimeElapsed(formatter.intToHoursMinutesSeconds(run.run.getTimeElapsed()));
        holder.setDistance(formatter.doubleToDistanceStringWithUnits(run.run.getDistanceTravelled()));
        holder.setAveragePace(formatter.doubleToAveragePaceString(run.run.getAveragePace()));

        holder.itemView.setOnClickListener((View view) -> {
            if(view.getContext() instanceof MainActivity) {
                ((MainActivity) view.getContext()).displayProgressBar(true);
            }

            Intent intent = new Intent();
            intent.setClass(view.getContext(), DetailsActivity.class);
            intent.putExtra("runId", Long.toString(run.run.getId()));
            view.getContext().startActivity(intent);
        });

        holder.itemView.setLongClickable(true);

        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Delete Run?")
                    .setMessage(
                            formatter.dateToString(run.run.getDateTime()) +
                            " - " + formatter.doubleToDistanceStringWithUnits(
                                run.run.getDistanceTravelled()) +
                            "\nThis action cannot be undone")
                    .setPositiveButton("Yes", (dialog, which) ->
                            runRepository.deleteRun(run.run, () -> {
                                presenter.onRunDeleted();
                            }))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return runsList.size();
    }

    public void loadRuns(Context context) {
        runRepository.getRuns(new RunDataSource.LoadRunsCallback() {
            @Override
            public void onRunsLoaded(List<RunWithLatLng> runs) {
                if(!runsList.equals(runs)) {
                    // calculate new run totals from data set
                    double totalDistance = 0;
                    int totalTime = 0;

                    for (RunWithLatLng run : runs) {
                        totalDistance += run.run.getDistanceTravelled();
                        totalTime += run.run.getTimeElapsed();
                    }

                    // set run totals toolbar
                    if(context instanceof MainActivity) {
                        ((MainActivity) context).setRunTotals(
                                Integer.toString(runsList.size()),
                                formatter.doubleToDistanceStringWithUnits(totalDistance),
                                formatter.intToHoursMinutesSeconds(totalTime)
                        );
                    }

                    // clear the current contents of runsList and then repopulate using new records
                    // from the database
                    runsList.clear();
                    runsList.addAll(runs);

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
                            formatter.doubleToDistanceStringWithUnits(0),
                            formatter.intToHoursMinutesSeconds(0)
                    );
                }

                runsList.clear();
                presenter.onDataNotAvailable();
                notifyDataSetChanged();
            }
        });
    }
}
