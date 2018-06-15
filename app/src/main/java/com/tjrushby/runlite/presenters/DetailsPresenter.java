package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.data.RunDataSource;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.math.BigDecimal;
import java.util.List;

public class DetailsPresenter implements DetailsContract.Presenter {
    private boolean changed;
    private boolean mapFullscreen;

    private DetailsContract.Activity view;
    private List<RunLatLng> mapMarkerCoordinates;
    private RunRepository runRepository;
    private RunWithLatLng runWithLatLng;
    private StringFormatter formatter;

    public DetailsPresenter(DetailsContract.Activity view,
                            List<RunLatLng> mapMarkerCoordinates,
                            RunRepository runRepository,
                            RunWithLatLng runWithLatLng,
                            StringFormatter formatter) {
        this.view = view;
        this.mapMarkerCoordinates = mapMarkerCoordinates;
        this.runRepository = runRepository;
        this.runWithLatLng = runWithLatLng;
        this.formatter = formatter;
    }

    @Override
    public void onViewCreated(String runId) {
        view.displayProgressBar(true);
        view.setDistanceUnits(formatter.getDistanceUnits());
        view.setTextViewsDistanceUnit(formatter.getDistanceUnitsString());

        mapFullscreen = false;

        runRepository.getRunById(Long.parseLong(runId), new RunDataSource.GetRunCallback() {
            @Override
            public void onRunLoaded(RunWithLatLng run) {
                runWithLatLng = run;

                view.setTextViews(
                        formatter.intToMinutesSeconds(run.run.getTimeElapsed()),
                        formatter.doubleToDistanceString(run.run.getDistanceTravelled()),
                        formatter.intToMinutesSeconds((int) run.run.getAveragePace())
                );

                view.setToolbarTitle("Run on " + formatter.dateToString(run.run.getDateTime()));

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
        if(mapFullscreen) {
            view.displaySmallMap();
            mapFullscreen = false;
        } else if(changed) {
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
        view.displayRunUpdatedToast();
        view.endActivity();
    }

    @Override
    public void onDeleteRunAlertDialogYes() {
        // delete run from database
        runRepository.deleteRun(runWithLatLng.run, () -> {
            view.displayRunDeletedToast();
            view.endActivity();

        });
    }

    @Override
    public void onExitAlertDialogYes() {
        view.endActivity();
    }

    @Override
    public void onEditTextDistanceChanged() {
        String distanceString = view.getEditTextDistance();

        // validate user input
        if(distanceString.isEmpty()) {
            view.displayEditTextDistanceEmptyError();
        } else if(distanceString.equals(".")) {
            view.displayEditTextDistanceNoNumbersError();
        } else if(Double.parseDouble(distanceString) == 0) {
            view.displayEditTextDistanceZeroError();
        } else {
            double distanceDouble = Double.parseDouble(distanceString);

            BigDecimal roundedDistance = new BigDecimal(distanceDouble)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            // calculate new average pace value from the values in the TextViews, using rounded distance
            double averagePace = calculateAveragePace(
                    formatter.minutesSecondsToInt(view.getEditTextTimeElapsed()),
                    roundedDistance.doubleValue()
            );

            // if the user input isn't rounded to two decimal places then do so
            if(distanceDouble != roundedDistance.doubleValue()) {
                view.setEditTextDistance(roundedDistance.toString());
            }

            // update TextView for average pace, clear error messages (if any) and enable button for
            // saving changes
            view.setTextViewAveragePace(formatter.intToMinutesSeconds((int) averagePace));
            view.clearEditTextDistanceError();

            // check if the TextViews contain different values to the model
            determineDataChanged();
        }
    }

    @Override
    public void onEditTextTimeElapsedClicked() {
        view.displayTimePickerDialog();
    }

    @Override
    public void onEditTextTimeElapsedUpdated(int timeElapsed) {
        // calculate average pace using new timeElapsed value and the current value in the EditText
        // for distance
        double averagePace = calculateAveragePace(
                timeElapsed,
                Double.parseDouble(view.getEditTextDistance().toString())
        );

        // update the TextViews for time and average pace
        view.setTextViewAveragePace(formatter.intToMinutesSeconds((int) averagePace));
        view.setEditTextTimeElapsed(formatter.intToMinutesSeconds(timeElapsed));

        // check if the TextViews contain different values to the model
        determineDataChanged();
    }

    @Override
    public void onImageViewFullscreenClicked() {
        view.displayFullscreenMap();
        view.displayMinimizeIcon();
        mapFullscreen = true;
    }

    @Override
    public void onImageViewMinimizeClicked() {
        view.displaySmallMap();
        view.displayFullscreenIcon();
        mapFullscreen = false;
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
            double prevMod = 0;

            for (RunLatLng runLatLng : runWithLatLng.runLatLngs) {
                double currentMod = runLatLng.getDistanceInRun() % 0.5;

                // if the currentMod is less than the prevMod then it is approximately an interval
                // of 0.5km so add it to the list
                if(currentMod == 0 || currentMod < prevMod) {
                    mapMarkerCoordinates.add(runLatLng);
                }

                prevMod = currentMod;
            }

            // add the last coordinate as a map marker
            mapMarkerCoordinates.add(
                    runWithLatLng.runLatLngs.get(runWithLatLng.runLatLngs.size() - 1)
            );

            view.addMapMarkers(mapMarkerCoordinates);
            view.displayProgressBar(false);
            view.moveMapCamera();
        }
    }

    private double calculateAveragePace(double timeElapsed, double distance) {
        return (timeElapsed / (distance / formatter.getDistanceUnits()));
    }

    private void determineDataChanged() {
        long timeElapsed = formatter.minutesSecondsToInt(view.getEditTextTimeElapsed());
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
        runWithLatLng.run.setTimeElapsed(formatter.minutesSecondsToInt(view.getEditTextTimeElapsed()));

        int timeElapsed = formatter.minutesSecondsToInt(view.getEditTextTimeElapsed());
        double distanceTravelled = Double.parseDouble(view.getEditTextDistance());

        double averagePace = calculateAveragePace(timeElapsed, distanceTravelled);

        runWithLatLng.run.setAveragePace(averagePace);

        // save run to database if it has been changed
        runRepository.updateRun(runWithLatLng);
    }
}
