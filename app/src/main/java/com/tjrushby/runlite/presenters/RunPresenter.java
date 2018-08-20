package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class RunPresenter implements RunContract.Presenter {
    public int GPS_ACCURACY_BAD_THRESHOLD;
    public int GPS_ACCURACY_GOOD_THRESHOLD;

    private boolean isAudioCueEnabled, isLocked;
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
        // start the timer
        view.nextTick();

        // get audio cue information
        isAudioCueEnabled = view.isAudioCueEnabled();
        audioCueType = view.getAudioCueType();
        audioCueDistanceInterval = view.getAudioCueDistanceInterval();
        audioCueTimeInterval = view.getAudioCueTimeInterval();

        if(isAudioCueEnabled) {
            view.hideButtonAudioEnable();
            view.showButtonAudioDisable();
        } else {
            view.hideButtonAudioDisable();
            view.showButtonAudioEnable();
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
        // only handle back presses if the view isn't locked
        if(!isLocked) {
            if(runService.isRunning() || timeElapsed > 0) {
                // if the run is currently in progress then confirm the user wants to cancel it
                view.displayDialogCancelRun();
            } else {
                onDialogCancelRunYes();
            }
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
                averagePace = timeElapsed / distanceTravelled;
            }

            // update the TextView elements
            if(averagePace == 0) {
                view.setTextViewPaceDefaultText();
            } else {
                view.setTextViewAveragePace(
                        formatter.averagePaceToTimeStringWithLabel(averagePace)
                );
            }

            view.setTextViewTime(formatter.secondsToTimeString(timeElapsed));
            view.setTextViewDistance(formatter.distanceToStringWithUnits(distanceTravelled));

            // update notification
            view.setNotificationContent(
                    formatter.secondsToTimeString(timeElapsed) + " · " +
                    formatter.distanceToString(distanceTravelled) +
                    formatter.getDistanceUnitsString()
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

    @Override
    public void onButtonAudioDisablePressed() {
        // disable audio cues
        isAudioCueEnabled = false;

        // hide disable button, show enable button
        view.hideButtonAudioDisable();
        view.showButtonAudioEnable();
    }

    @Override
    public void onButtonAudioEnablePressed() {
        // enable audio cues
        isAudioCueEnabled = true;

        // hide enable button, show disable button
        view.hideButtonAudioEnable();
        view.showButtonAudioDisable();
    }

    @Override
    public void onButtonLockPressed() {
        isLocked = true;

        // hide buttons associated with controlling the run
        view.hideButtonLock();
        view.hideButtonPause();
        view.hideButtonStop();

        view.hideButtonAudioDisable();
        view.hideButtonAudioEnable();

        // display unlock button
        view.showButtonUnlock();

        // keep screen on
        view.setScreenTimeoutNone();
    }

    @Override
    public void onButtonUnlockActionDown() {
        // show the unlock circle around buttonUnlock, then up-scale buttonUnlock until receiving
        // an up action
        view.showIVUnlockCircle();
        view.animateButtonUnlockScaleUp();
    }

    @Override
    public void onButtonUnlockActionUp() {
        // hide the unlock circle around buttonUnlock, then down-scale buttonUnlock
        view.hideIVUnlockCircle();
        view.animateButtonUnlockScaleDown();
    }

    // tell model to displayDialogEndRun requesting location updates and the view to not post another Runnable
    @Override
    public void onButtonPausePressed() {
        if(isLocked) {
            // unlock the view if the pause has been called from the notification action
            onViewUnlocked();
        }

        // let the runService know the run is paused
        runService.setRunning(false);

        if(isAudioCueEnabled) {
            // announce the run pausing
            view.speak("Run paused");
        }

        // update the notification action to reflect the current run state
        view.setNotificationActionResume();

        // hide the pause and lock buttons, display the start button
        view.hideButtonLock();
        view.hideButtonPause();
        view.showButtonStart();

        // set notification text to reflect the current state
        view.setNotificationContentTitle("Paused");
    }

    // tell model to start requesting location updates and the view to start a Runnable
    @Override
    public void onButtonStartPressed() {
        // give the option to lock the view now they're running
        view.showButtonLock();

        if(timeElapsed == 0) {
            // give the option to stop the run now it is started
            view.showButtonStop();

            if(isAudioCueEnabled) {
                // announce the run starting
                view.speak("Run started");
            }

            // display a notification
            view.displayNotification();
        } else {
            // if timeElapsed > 0 then the run has been paused and is now being resumed

            if(isAudioCueEnabled) {
                // announce the run resuming
                view.speak("Run resumed");
            }

            // update the notification action so the user can now pause the run from it
            view.setNotificationActionPause();

            // set notification text to reflect the current state, i.e. running
            view.setNotificationContentTitle("Running");
        }

        // let the runService know the user is currently running
        runService.setRunning(true);

        // hide the start button and display the pause button, disable the stop button
        view.hideButtonStart();
        view.showButtonPause();
    }

    // end the view Activity
    @Override
    public void onButtonStopPressed() {
        // let the runService know the run is paused
        runService.setRunning(false);

        if(isAudioCueEnabled) {
            // announce the run stopping
            view.speak("Stopping run. ");
        }

        // update the notification action to reflect the run state
        view.setNotificationActionResume();

        // hide the pause and lock buttons, display the start button
        view.hideButtonLock();
        view.hideButtonPause();
        view.showButtonStart();

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
        runService.stopLocationUpdates();
        view.pauseTick();
        view.removeNotification();

        if(isAudioCueEnabled) {
            // announce the run ending
            view.speak("Run completed. ");
        }

        // save isAudioCueEnabled value to shared preferences
        view.saveAudioCueSharedPrefs(isAudioCueEnabled);

        if(model.getDistanceTravelled() > 0.01) {
            model.setTimeElapsed(timeElapsed);

            // round distance travelled up to two decimal places
            BigDecimal roundedDistance = new BigDecimal(model.getDistanceTravelled())
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            model.setDistanceTravelled(roundedDistance.doubleValue());

            // set averagePace in the model now as we're not going to be updating it every tick now.
            // storing averagePace in the database saves having to calculate it again when
            // retrieving the run (i.e., loading run list in MainActivity).

            // recalculate averagePace now to ensure it is accurate now that the run has stopped
            model.setAveragePace(timeElapsed / distanceTravelled);

            runRepository.saveRun(
                    (Run) model, runLatLngList,
                    runId -> {
                        view.startDetailsActivity(Long.toString(runId));
                        view.displayToastRunSaved();
                    }
            );
        } else {
            view.displayToastRunNotSaved();
            view.endActivity();
        }
    }

    @Override
    public void onDialogCancelRunYes() {
        if(isAudioCueEnabled) {
            // announce the run being cancelled
            view.speak("Run cancelled. ");
        }

        runService.stopLocationUpdates();

        view.pauseTick();
        view.saveAudioCueSharedPrefs(isAudioCueEnabled);
        view.removeNotification();
        view.endActivity();
    }

    @Override
    public void onViewUnlocked() {
        isLocked = false;

        // hide views used in unlocking
        view.hideButtonUnlock();
        view.hideIVUnlockCircle();

        // show buttons for controlling an active run
        view.showButtonPause();
        view.showButtonStop();
        view.showButtonLock();

        view.showButtonAudioDisable();
        view.showButtonAudioEnable();

        // set the scale on buttonUnlock back to default
        view.setButtonUnlockDefaultScale();

        // set the default screen timeout
        view.setScreenTimeoutDefault();
    }

    // determines what color to tint the GPS icon based on the value of currentAccuracy
    private void determineGPSIconTint(double currentAccuracy) {
        if(currentAccuracy > GPS_ACCURACY_BAD_THRESHOLD) {
            // bad GPS accuracy
            view.showIVLocationBad();
        } else if(currentAccuracy > GPS_ACCURACY_GOOD_THRESHOLD &&
                currentAccuracy <= GPS_ACCURACY_BAD_THRESHOLD) {
            // average GPS accuracy
            view.showIVLocationAverage();
        } else if(currentAccuracy <= GPS_ACCURACY_GOOD_THRESHOLD) {
            // good GPS accuracy
            view.showIVLocationGood();
        }
    }

    private void speakRunDetails() {
        view.speak(
                formatter.distanceToStringWithUnits(distanceTravelled) +
                        " in " + formatter.secondsToTimeStringAudioCue(timeElapsed) +
                        ". Average pace " +
                        formatter.averagePaceToTimeStringAudioCue(averagePace) + ". "
        );
    }
}
