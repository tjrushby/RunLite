package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.List;

import javax.inject.Inject;

public class RunPresenter implements RunContract.Presenter {
    public int GPS_ACCURACY_BAD_THRESHOLD;
    public int GPS_ACCURACY_GOOD_THRESHOLD;

    private boolean isAudioCueEnabled;
    private double audioCueDistanceInterval;
    private int audioCueTimeInterval;

    private double averagePace;
    private double distanceTravelled;
    private double prevMod;

    private int timeElapsed;

    private String audioCueType;

    private RunContract.Activity view;
    private Run model;
    private RunContract.RunService runService;
    private RunRepository runRepository;
    private List<RunLatLng> runLatLngList;

    private StringFormatter formatter;

    @Inject
    public RunPresenter(RunContract.Activity view,
                        Run model,
                        RunContract.RunService runService,
                        RunRepository runRepository,
                        List<RunLatLng> runLatLngList,
                        StringFormatter formatter) {
        this.view = view;
        this.model = model;
        this.runService = runService;
        this.runRepository = runRepository;
        this.runLatLngList = runLatLngList;
        this.formatter = formatter;

        timeElapsed = 0;
        prevMod = 0;

        GPS_ACCURACY_BAD_THRESHOLD = view.getGPSAccuracyBadThreshold();
        GPS_ACCURACY_GOOD_THRESHOLD = view.getGPSAccuracyGoodThreshold();
    }

    @Override
    public void onActivityCreated() {
        view.disableSeekBar();
        view.setTextViewsDistanceUnit(formatter.getDistanceUnitsString());

        // start the timer
        view.nextTick();

        // get audio cue information
        isAudioCueEnabled = view.isAudioCueEnabled();

        if(isAudioCueEnabled) {
            audioCueType = view.getAudioCueType();
            audioCueDistanceInterval = view.getAudioCueDistanceInterval();
            audioCueTimeInterval = view.getAudioCueTimeInterval();
        }
    }

    @Override
    public void onActivityResumed() {
        view.setColorAccentTypedValue();
    }

    @Override
    public void havePermissions() {
        view.startServices();
        runService.startLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        if(runService.isRunning() || timeElapsed > 0) {
            view.displayDialogCancelRun();
        } else {
            runService.stopLocationUpdates();
            view.removeNotification();
            view.endActivity();
        }
    }

    // update the view with the latest data
    @Override
    public void onTick() {
        // update the gps icon
        determineGPSIconTint(model.getCurrentAccuracy());

        if(runService.isRunning()) {
            // increment the timer
            timeElapsed += 1;

            averagePace = 0;
            distanceTravelled = model.getDistanceTravelled();

            // calculate the average pace
            if(model.getDistanceTravelled() > 0) {
                averagePace = timeElapsed / distanceTravelled / formatter.getDistanceUnits();
            }

            // update the TextView elements
            if(averagePace == 0) {
                view.setTextViewPaceDefaultText();
            } else {
                view.setTextViewPace(formatter.intToMinutesSeconds((int) averagePace));
            }

            view.setTextViewTime(formatter.intToMinutesSeconds(timeElapsed));
            view.setTextViewDistance(formatter.doubleToDistanceString(distanceTravelled));

            // update notification
            view.setNotificationContent(
                    formatter.intToMinutesSeconds(timeElapsed) + " · "
                            + formatter.doubleToDistanceString(distanceTravelled)
                            + formatter.getDistanceUnitsString()
            );

            // audio cue
            if(isAudioCueEnabled) {
                if(audioCueType.equals("distance")) {
                    // distance-based audio cue
                    if(distanceTravelled > 0) {
                        // only perform mod calculations if distanceTravelled > 0 to avoid
                        // constantly calling audioCueService

                        // divide distanceTravelled by distance units to account for km or miles
                        double currentMod = (distanceTravelled/formatter.getDistanceUnits())
                                % audioCueDistanceInterval;

                        if(currentMod == 0 || currentMod < prevMod) {
                            // if currentMod is zero or less than prevMod it is either the interval
                            // defined in audioCueDistanceInterval or the closest location to it
                            speakRunDetails();
                        }

                        prevMod = currentMod;
                    }
                } else {
                    // time-based audio cue
                    if(timeElapsed % audioCueTimeInterval == 0) {
                        speakRunDetails();
                    }
                }
            }
        }

        view.nextTick();
    }

    // tell model to start requesting location updates and the view to start a Runnable
    @Override
    public void onButtonStartPressed() {
        if(timeElapsed == 0) {
            // announce the run starting
            view.speak("Run started");

            // display a notification
            view.displayNotification();
        } else {
            // if timeElapsed > 0 then the run has been paused and is now being resumed

            // announce the run resuming
            view.speak("Run resumed");

            // update the notification action so the user can now pause the run from it
            view.setNotificationActionPause();

            // set notification text to reflect the current state, i.e. running
            view.setNotificationContentTitle("Running");
        }

        // tint the unlock icon
        view.tintIconUnlock();

        // enable the SeekBar
        view.enableSeekBar();

        // enabled the stop button
        view.enableButtonsStartPauseStop();

        // let the runService know the user is currently running
        runService.setRunning(true);

        // hide the start button and display the pause button, disable the stop button
        view.hideButtonStart();
        view.showButtonPause();
    }

    // tell model to displayDialogEndRun requesting location updates and the view to not post another Runnable
    @Override
    public void onButtonPausePressed() {
        // let the runService know the run is paused
        runService.setRunning(false);

        // announce the run pausing
        view.speak("Run paused");

        view.setNotificationActionResume();

        // disable the SeekBar and fade associated ImageView objects
        view.disableSeekBar();
        view.fadeIconLock();
        view.fadeIconUnlock();

        // hide the pause button and display the start button, setting the start button text to resume
        view.hideButtonPause();
        view.showButtonStart();
        view.setButtonStartText();

        // set notification text to reflect the current state
        view.setNotificationContentTitle("Paused");
    }

    // end the view Activity
    @Override
    public void onButtonStopPressed() {
        // let the runService know the run is paused
        runService.setRunning(false);

        // announce the run stopping
        view.speak("Stopping run. ");

        view.setNotificationActionResume();

        // disable the SeekBar and fade associated ImageView objects
        view.disableSeekBar();
        view.fadeIconLock();
        view.fadeIconUnlock();

        // hide the pause button and display the start button, setting the start button text to resume
        view.hideButtonPause();
        view.showButtonStart();
        view.setButtonStartText();

        // set notification text to reflect the current state
        view.setNotificationContentTitle("Paused");

        view.displayDialogEndRun();
    }

    @Override
    public void onDialogEnableHighAccuracyNo() {
        view.displayToastGPSMode();
        view.endActivity();
    }

    @Override
    public void onDialogEndRunNo() {
        onButtonStartPressed();
    }

    @Override
    public void onDialogEndRunYes() {
        // announce the run ending
        view.speak("Run completed. ");

        model.setTimeElapsed(timeElapsed);

        if(model.getDistanceTravelled() > 0.01) {
            // set averagePace in the model now as we're not going to be updating it every tick now.
            // storing averagePace in the database saves having to calculate it again when
            // retrieving the run (i.e., loading run list in MainActivity).
            // recalculate averagePace now to ensure it is accurate now that the run has stopped
            model.setAveragePace(timeElapsed / distanceTravelled);

            runRepository.saveRun(
                    (Run) model, runLatLngList,
                    runId -> view.startDetailsActivity(Long.toString(runId))
            );

            view.displayToastRunSaved();
        } else {
            view.displayToastRunNotSaved();
            view.pauseTick();
            view.endActivity();
        }

        view.removeNotification();
        runService.stopLocationUpdates();
    }

    @Override
    public void onDialogCancelRunYes() {
        // announce the run being cancelled
        view.speak("Run cancelled. ");

        view.pauseTick();
        view.endActivity();
        view.removeNotification();
        runService.stopLocationUpdates();
    }

    @Override
    public void onSeekBarChanged() {
        if(view.getSeekBarProgress() > 65) {
            // set the progress to 100
            view.setSeekBarProgress(100);

            // update icons to reflect SeekBar state
            view.tintIconLock();
            view.fadeIconUnlock();

            // lock the controls, keep screen turned on
            view.disableButtonsStartPauseStop();
            view.setScreenTimeoutNone();
        } else {
            // set the progress to 0
            view.setSeekBarProgress(0);

            // update icons to reflect SeekBar state
            view.fadeIconLock();
            view.tintIconUnlock();

            // unlock the controls, reset screen timeout to default
            view.enableButtonsStartPauseStop();
            view.setScreenTimeoutDefault();
        }
    }

    // determines what color to tint the GPS icon based on the value of currentAccuracy
    private void determineGPSIconTint(double currentAccuracy) {
        if(currentAccuracy > GPS_ACCURACY_BAD_THRESHOLD) {
            // bad GPS accuracy
            view.tintIconGPSBad();
        } else if(currentAccuracy > GPS_ACCURACY_GOOD_THRESHOLD &&
                currentAccuracy <= GPS_ACCURACY_BAD_THRESHOLD) {
            // average GPS accuracy
            view.tintIconGPSAverage();
        } else if(currentAccuracy <= GPS_ACCURACY_GOOD_THRESHOLD) {
            // good GPS accuracy
            view.tintIconGPSGood();
        }
    }

    private void speakRunDetails() {
        view.speak(
                formatter.doubleToDistanceStringWithUnits(distanceTravelled) +
                        " in " + formatter.intToMinutesSecondsAudioCueString(timeElapsed) +
                        ". Average pace " +
                        formatter.doubleToAveragePaceAudioCueString((long) averagePace) + ". "
        );
    }
}
