package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.data.RunDataSource;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.math.BigDecimal;

import timber.log.Timber;

public class DetailsPresenter implements DetailsContract.Presenter {
    private boolean changed;

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
        view.displayProgressBar(true);

        runRepository.getRunById(Long.parseLong(runId), new RunDataSource.GetRunCallback() {
            @Override
            public void onRunLoaded(RunWithLatLng run) {
                runWithLatLng = run;

                double averagePace = run.run.getTimeElapsed() / run.run.getDistanceTravelled();

                view.setTextViews(
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
    public void onBackPressed() {
        if(changed) {
            view.displayExitAlertDialog();
        } else {
            view.endActivity();
        }
    }

    @Override
    public void onButtonDeleteClicked() {
        // display an AlertDialog to confirm user action
        view.displayDeleteRunAlertDialog();
    }

    @Override
    public void onButtonDoneClicked() {
        view.endActivity();
    }

    @Override
    public void onButtonUpdateClicked() {
        updateRun();
        view.endActivity();
    }

    @Override
    public void onDeleteRunAlertDialogYes() {
        // delete run from database
        runRepository.deleteRun(runWithLatLng.run);
        view.endActivity();
    }

    @Override
    public void onExitAlertDialogYes() {
        view.endActivity();
    }

    @Override
    public void onEditTextDistanceChanged() {
        view.validateEditTextDistance();
    }

    @Override
    public void onEditTextDistanceEmpty() {
        view.displayEditTextDistanceEmptyError();
    }

    @Override
    public void onEditTextDistanceNoNumbers() {
        view.displayEditTextDistanceNoNumbersError();
    }

    @Override
    public void onEditTextDistanceZero() {
        view.displayEditTextDistanceZeroError();
    }

    @Override
    public void onEditTextDistanceValid() {
        double distance = Double.parseDouble(view.getEditTextDistance());

        BigDecimal roundedDistance = new BigDecimal(distance)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        // calculate new average pace value from the values in the TextViews, using rounded distance
        long averagePace = calculateAveragePace(
                formatter.minutesSecondsToLong(view.getEditTextTimeElapsed()),
                roundedDistance.doubleValue()
        );

        // if the user input isn't rounded to two decimal places then do so
        if(distance != roundedDistance.doubleValue()) {
            view.setEditTextDistance(roundedDistance.toString());
        }

        // update TextView for average pace, clear error messages (if any) and enable button for
        // saving changes
        view.setTextViewAveragePace(formatter.longToMinutesSeconds(averagePace));
        view.clearEditTextDistanceError();

        // check if the TextViews contain different values to the model
        determineDataChanged();
    }

    @Override
    public void onEditTextTimeElapsedClicked() {
        view.displayTimePickerDialog();
    }

    @Override
    public void onEditTextTimeElapsedUpdated(long timeElapsed) {
        // calculate average pace using new timeElapsed value and the current value in the EditText
        // for distance
        long averagePace = calculateAveragePace(
                timeElapsed,
                Double.parseDouble(view.getEditTextDistance().toString())
        );

        // update the TextViews for time and average pace
        view.setTextViewAveragePace(formatter.longToMinutesSeconds((long) averagePace));
        view.setEditTextTimeElapsed(formatter.longToMinutesSeconds(timeElapsed));

        // check if the TextViews contain different values to the model
        determineDataChanged();
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
            view.displayProgressBar(false);
            view.moveMapCamera();
        }
    }

    private long calculateAveragePace(double timeElapsed, double distance) {
        return (long) (timeElapsed / distance);
    }

    private void determineDataChanged() {
        long timeElapsed = formatter.minutesSecondsToLong(view.getEditTextTimeElapsed());
        double distanceTravelled = Double.parseDouble(view.getEditTextDistance());
        double cachedDistanceTravelled = Double.parseDouble(
                formatter.doubleToDistanceString(runWithLatLng.run.getDistanceTravelled())
        );

        if(timeElapsed != runWithLatLng.run.getTimeElapsed()
                || distanceTravelled != cachedDistanceTravelled) {
            changed = true;
            view.hideButtonDone();
            view.showButtonUpdate();
        } else {
            changed = false;
            view.hideButtonUpdate();
            view.showButtonDone();
        }
    }

    private void updateRun() {
        runWithLatLng.run.setDistanceTravelled(Double.parseDouble(view.getEditTextDistance()));
        runWithLatLng.run.setTimeElapsed(formatter.minutesSecondsToLong(view.getEditTextTimeElapsed()));

        // save run to database if it has been changed
        runRepository.updateRun(runWithLatLng);
    }
}
