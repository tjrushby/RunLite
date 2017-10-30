package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.data.RunDataSource;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.util.StringFormatter;

public class DetailsPresenter implements DetailsContract.Presenter {
    private boolean edited;

    private DetailsContract.Activity view;
    private RunRepository runRepository;
    private RunWithLatLng runWithLatLng;
    private StringFormatter formatter;

    public DetailsPresenter(DetailsContract.Activity view,
                            RunRepository runRepository,
                            RunWithLatLng runWithLatLng,
                            StringFormatter formatter) {
        this.view = view;
        this.runRepository = runRepository;
        this.runWithLatLng = runWithLatLng;
        this.formatter = formatter;
    }

    @Override
    public void onViewCreated(String runId) {
        runRepository.getRunById(Long.parseLong(runId), new RunDataSource.GetRunCallback() {
            @Override
            public void onRunLoaded(RunWithLatLng run) {
                runWithLatLng = run;

                double averagePace = run.run.getTimeElapsed() / run.run.getDistanceTravelled();

                view.updateTextViews(
                        formatter.longToMinutesSeconds(run.run.getTimeElapsed()),
                        formatter.doubleToDistanceString(run.run.getDistanceTravelled()),
                        formatter.longToMinutesSeconds((long) averagePace)
                );

                view.getMapFragment();
            }

            @Override
            public void onDataNotAvailable() {
                view.displayNotFoundErrorToast();
            }
        });
    }

    @Override
    public void onButtonDoneClicked() {
        if(edited) {
            // save run to database if it has been changed
            runRepository.updateRun(runWithLatLng.run, runWithLatLng.runLatLngs);
        } else if(!edited) {
            view.endActivity();
        }
    }

    @Override
    public void onButtonDeleteClicked() {
        // display an AlertDialog to confirm user action
        view.displayDeleteRunAlertDialog();
    }

    @Override
    public void onDeleteRunAlertDialogYes() {
        // delete run from database
        runRepository.deleteRun(runWithLatLng.run);
        view.endActivity();
    }

    @Override
    public void onMapFragmentReady() {
        // calculate the bounds for the map and plot a line representing the run route
        if(!runWithLatLng.runLatLngs.isEmpty()) {
            view.calculateMapBounds(runWithLatLng.runLatLngs);
            view.calculateMapPolyline(runWithLatLng.runLatLngs);
        }
    }

    @Override
    public void onMapLoaded() {
        // add start and end markers for the run, move the map to show what has been drawn
        if(!runWithLatLng.runLatLngs.isEmpty()) {
            view.addMapMarkers(runWithLatLng.runLatLngs);
            view.moveMapCamera();
        }
    }
}
