package com.tjrushby.runlite.adapters;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolder;
import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolderFactory;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.List;

public class RunModelAdapter extends RecyclerView.Adapter<RunModelViewHolder> {
    private List<RunWithLatLng> runsList;
    private RunModelViewHolderFactory factory;
    private StringFormatter formatter;

    public RunModelAdapter(List<RunWithLatLng> runsList,
                           RunModelViewHolderFactory factory,
                           StringFormatter formatter) {
        this.runsList = runsList;
        this.factory = factory;
        this.formatter = formatter;
    }

    @Override
    public RunModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return factory.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RunModelViewHolder holder, int position) {
        RunWithLatLng run = runsList.get(position);

        double averagePace = run.run.getTimeElapsed() / run.run.getDistanceTravelled();

        // todo need average pace stored in model class?
        holder.setDateTime(formatter.dateToString(run.run.getDateTime()));
        holder.setTimeElapsed(formatter.longToMinutesSeconds(run.run.getTimeElapsed()));
        holder.setDistance(formatter.doubleToDistanceString(run.run.getDistanceTravelled()));
        holder.setAveragePace(formatter.longToMinutesSeconds((long) averagePace));
    }

    @Override
    public int getItemCount() {
        return runsList.size();
    }

    public void getRunsFromDatabase() {
        new getRunsTask().execute();
    }

    private class getRunsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // done repopulating runsList from the database, notify the adapter that the
            // data set has changed
            notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // clear the current contents of runsList and then repopulate using records
            // from the database
            runsList.clear();
            runsList.addAll(App.getDatabase().runDAO().loadRunWithLatLng());

            return null;
        }
    }
}
