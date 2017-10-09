package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.adapters.viewholders.ModelViewHolder;
import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.List;

public class ListPresenter implements MainContract.ListPresenter {
    private List<RunContract.Model> runsList;
    private StringFormatter formatter;

    public ListPresenter(List<RunContract.Model> runsList, StringFormatter formatter) {
        this.runsList = runsList;
        this.formatter = formatter;

        // todo populate runsList from db here
    }

    @Override
    public void onViewHolderBoundAtPosition(ModelViewHolder viewHolder, int position) {
        RunContract.Model run = runsList.get(position);

        // todo need average pace stored in model class?
        viewHolder.setDateTime(run.getDateTime().toString());
        viewHolder.setTimeElapsed(formatter.longToMinutesSeconds(run.getTimeElapsed()));
        viewHolder.setDistance(formatter.doubleToDistanceString(run.getDistanceTravelled()));
    }

    @Override
    public int getRowCount() {
        return runsList.size();
    }
}
