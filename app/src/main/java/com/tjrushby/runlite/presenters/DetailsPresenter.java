package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class DetailsPresenter implements DetailsContract.Presenter {
    private DetailsContract.Activity view;
    private RunContract.Model run;
    private List<RunLatLng> runLatLngList;
    private StringFormatter formatter;

    @Inject
    public DetailsPresenter(DetailsContract.Activity view,
                            RunContract.Model run,
                            List<RunLatLng> runLatLngList,
                            StringFormatter formatter) {
        this.view = view;
        this.run = run;
        this.runLatLngList = runLatLngList;
        this.formatter = formatter;
    }

    @Override
    public void onViewCreated() {
        double averagePace = run.getTimeElapsed() / run.getDistanceTravelled();

        view.updateTextViews(
                formatter.longToMinutesSeconds(run.getTimeElapsed()),
                formatter.doubleToDistanceString(run.getDistanceTravelled()),
                formatter.longToMinutesSeconds((long) averagePace)
        );
    }

    @Override
    public void onButtonDoneClicked() {
        // save run to database
        new Thread(() -> {
            long runId = App.getDatabase().runDAO().insert((Run) run);
            Timber.d("id: " + runId);

            for (RunLatLng runLatLng : runLatLngList) {
                runLatLng.setRunId(runId);
            }

            App.getDatabase().runLatLngDAO().insertAll(runLatLngList);
        }).start();

        view.endActivity();
    }

    @Override
    public void onButtonDeleteClicked() {
        // delete run from database
        new Thread(() -> App.getDatabase().runDAO().delete((Run) run)).start();

        view.endActivity();
    }

    @Override
    public void onMapFragmentReady() {
        // calculate the bounds for the map and plot a line representing the run route
        if (!runLatLngList.isEmpty()) {
            view.calculateMapBounds(runLatLngList);
            view.calculateMapPolyline(runLatLngList);
        }
    }

    @Override
    public void onMapLoaded() {
        // add start and end markers for the run, move the map to show what has been drawn
        if (!runLatLngList.isEmpty()) {
            view.addMapMarkers(runLatLngList);
            view.moveMapCamera();
        }
    }
}
