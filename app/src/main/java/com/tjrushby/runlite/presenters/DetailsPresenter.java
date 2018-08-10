package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.data.RunDataSource;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.List;

public class DetailsPresenter implements DetailsContract.Presenter {
    private boolean mapFullscreen, updated;
    private double distanceUnits;

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

        distanceUnits = formatter.getDistanceUnits();
    }

    @Override
    public void onViewCreated(String runId) {
        view.displayProgressBar(true);
        view.setDistanceUnits(distanceUnits);

        mapFullscreen = false;

        runRepository.getRunById(Long.parseLong(runId), new RunDataSource.GetRunCallback() {
            @Override
            public void onRunLoaded(RunWithLatLng run) {
                runWithLatLng = run;

                view.setTextViews(
                        formatter.secondsToTimeString(run.run.getTimeElapsed()),
                        formatter.distanceToStringWithUnits(run.run.getDistanceTravelled()),
                        formatter.averagePaceToTimeStringWithLabel(run.run.getAveragePace())
                );

                view.setTextViewRunDateTime(formatter.dateToString(run.run.getDateTime()));

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
            onImageViewMinimizeClicked();
        } else {
            if(!updated) {
                view.endActivity();
            } else {
                view.endActivityWithIntent();
            }
        }
    }

    @Override
    public void onActionDeleteSelected() {
        // display an AlertDialog to confirm user action
        view.displayDeleteRunAlertDialog();
    }

    @Override
    public void onActionEditSelected() {
        // display edit details fragment
        view.startEditActivity(
                formatter.distanceToString(runWithLatLng.run.getDistanceTravelled()));
    }

    @Override
    public void onDeleteRunAlertDialogYes() {
        // delete run from database
        runRepository.deleteRun(runWithLatLng.run, () -> {
            view.displayRunDeletedToast();

            if(!updated) {
                view.endActivity();
            } else {
                view.endActivityWithIntent();
            }
        });
    }

    @Override
    public void onImageViewFullscreenClicked() {
        view.displayLargeMap();
        view.displayMinimizeIcon();
        view.animateMapCameraForLargeMap();

        mapFullscreen = true;
    }

    @Override
    public void onImageViewMinimizeClicked() {
        view.displaySmallMap();
        view.displayFullscreenIcon();
        view.animateMapCameraForSmallMap();

        mapFullscreen = false;
    }

    @Override
    public void onMapFragmentReady() {
        // calculate the bounds for the map and plot a line representing the run route
        if(!runWithLatLng.runLatLngs.isEmpty()) {
            view.calculateMapPolyline(runWithLatLng.runLatLngs);
            view.calculateMapBounds(runWithLatLng.runLatLngs);

            // add interval markers for the run
            double prevMod = 0;

            for (RunLatLng runLatLng : runWithLatLng.runLatLngs) {
                double currentMod = (runLatLng.getDistanceInRun() * distanceUnits) % 0.5;

                // if the currentMod is less than the prevMod then it is approximately an interval
                // of 0.5 so add it to the list
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
            view.moveMapCameraForSmallMap();
        }
    }

    @Override
    public void onMapLoaded() {
        view.displayProgressBar(false);
    }

    @Override
    public void onRunDetailsChanged(String[] details) {
        if(details != null && details.length == 3) {
            // correct number of updated details supplied
            view.setTextViews(
                    details[0],
                    formatter.distanceToStringWithUnits(Double.parseDouble(details[1])),
                    details[2]
            );

            updateRun(details[0], details[1]);
            view.displayRunUpdatedToast();
        }
    }

    private double calculateAveragePace(double timeElapsed, double distance) {
        // multiply distance by distanceUnits to ensure it is converted to miles if needed
        return (timeElapsed / (distance * distanceUnits));
    }

    private void updateRun(String timeString, String distanceString) {
        int timeElapsed = formatter.timeStringToSeconds(timeString);
        String[] split = distanceString.split(" ");

        double distanceTravelled = Double.parseDouble(split[0]);
        double averagePace = calculateAveragePace(timeElapsed, distanceTravelled);

        runWithLatLng.run.setTimeElapsed(timeElapsed);

        // * by distanceUnits to ensure that we're saving the distance in meters
        runWithLatLng.run.setDistanceTravelled(distanceTravelled * distanceUnits);

        runWithLatLng.run.setAveragePace(averagePace);

        // save run to database if it has been changed
        runRepository.updateRun(runWithLatLng);

        updated = true;
    }
}
