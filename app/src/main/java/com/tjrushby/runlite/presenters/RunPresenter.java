package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class RunPresenter implements RunContract.Presenter {
    private RunContract.Activity view;
    private RunContract.Model model;
    private RunContract.Service service;
    private RunRepository runRepository;
    private List<RunLatLng> runLatLngList;

    private StringFormatter formatter;

    private long timeElapsed;

    @Inject
    public RunPresenter(RunContract.Activity view,
                        RunContract.Model model,
                        RunContract.Service service,
                        RunRepository runRepository,
                        List<RunLatLng> runLatLngList,
                        StringFormatter formatter) {
        this.view = view;
        this.model = model;
        this.service = service;
        this.runRepository = runRepository;
        this.runLatLngList = runLatLngList;
        this.formatter = formatter;

        timeElapsed = 0;
    }

    @Override
    public void onActivityCreated() {
        view.disableSeekBar();
        view.setTextViewsDistanceUnit(formatter.getDistanceUnitsString());

        // start the timer
        view.nextTick();
    }

    @Override
    public void onActivityResumed() {
        view.updateColorAccentTypedValue();
    }

    @Override
    public void havePermissions() {
        view.startService();
        service.startLocationUpdates();
    }

    @Override
    public void onBackPressed() {
        if(timeElapsed > 0) {
            view.displayExitAlertDialog();
        } else {
            service.stopLocationUpdates();
            view.removeNotification();
            view.endActivity();
        }
    }

    // update the view with the latest data
    @Override
    public void onTick() {
        // update the gps icon
        determineGPSIcon(model.getCurrentAccuracy());

        if(service.isRunning()) {
            // increment the timer
            timeElapsed += 1;

            // calculate the average pace
            double averagePace = 0;

            if(model.getDistanceTravelled() > 0) {
                averagePace = timeElapsed / (model.getDistanceTravelled() / formatter.getDistanceUnits());
            }

            // update the TextView elements
            if(averagePace == 0) {
                view.setTextViewPaceDefaultText();
            } else {
                view.updateTextViewPace(formatter.longToMinutesSeconds((long) averagePace));
            }

            // update notification
            view.setNotificationContent(
                    formatter.longToMinutesSeconds(timeElapsed) + " · "
                            + formatter.doubleToDistanceString(model.getDistanceTravelled())
                            + formatter.getDistanceUnitsString()
            );

            view.updateTextViewTime(formatter.longToMinutesSeconds(timeElapsed));
            view.updateTextViewDistance(formatter.doubleToDistanceString(model.getDistanceTravelled()));
        }

        view.nextTick();
    }

    // tell model to start requesting location updates and the view to start a Runnable
    @Override
    public void onButtonStartPressed() {
        if(timeElapsed == 0) {
            // display a notification
            view.displayNotification();
        } else {
            // if timeElapsed > 0 then the run has been paused and is now being resumed

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

        // let the service know the user is currently running
        service.setRunning(true);

        // hide the start button and display the pause button, disable the stop button
        view.hideButtonStart();
        view.showButtonPause();
    }

    // tell model to displayEndRunAlertDialog requesting location updates and the view to not post another Runnable
    @Override
    public void onButtonPausePressed() {
        // let the service know the run is paused
        service.setRunning(false);

        view.setNotificationActionResume();

        // disable the SeekBar and fade associated ImageView objects
        view.disableSeekBar();
        view.fadeIconLock();
        view.fadeIconUnlock();

        // hide the pause button and display the start button, setting the start button text to resume
        view.hideButtonPause();
        view.showButtonStart();
        view.updateButtonStartText();
        view.setTextViewPaceDefaultText();

        // set notification text to reflect the current state
        view.setNotificationContentTitle("Paused");
    }

    // end the view Activity
    @Override
    public void onButtonStopPressed() {
        onButtonPausePressed();
        view.displayEndRunAlertDialog();
    }

    @Override
    public void enableHighAccuracyDialogNo() {
        view.displayGPSModeToast();
        view.endActivity();
    }

    @Override
    public void endRunAlertDialogNo() {
        onButtonStartPressed();
    }

    @Override
    public void endRunAlertDialogYes() {
        model.setTimeElapsed(timeElapsed);

        if(model.getDistanceTravelled() > 0.01) {
            runRepository.saveRun(
                    (Run) model, runLatLngList,
                    runId -> view.startDetailsActivity(Long.toString(runId))
            );

            view.displaySaveToast();
        } else {
            view.displayNoSaveToast();
            view.pauseTick();
            view.endActivity();
        }

        view.removeNotification();
        service.stopLocationUpdates();
    }

    @Override
    public void exitRunAlertDialogYes() {
        view.pauseTick();
        view.endActivity();
        view.removeNotification();
        service.stopLocationUpdates();
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
            view.noScreenTimeout();
        } else {
            // set the progress to 0
            view.setSeekBarProgress(0);

            // update icons to reflect SeekBar state
            view.fadeIconLock();
            view.tintIconUnlock();

            // unlock the controls, reset screen timeout to default
            view.enableButtonsStartPauseStop();
            view.defaultScreenTimeout();
        }
    }

    // determines what color to tint the GPS icon based on the value of currentAccuracy
    private void determineGPSIcon(double currentAccuracy) {
        if(currentAccuracy > App.GPS_ACCURACY_BAD_THRESHOLD) {
            // bad GPS accuracy
            view.updateGPSIconBad();
        } else if(currentAccuracy > App.GPS_ACCURACY_GOOD_THRESHOLD &&
                currentAccuracy <= App.GPS_ACCURACY_BAD_THRESHOLD) {
            // average GPS accuracy
            view.updateGPSIconAverage();
        } else if(currentAccuracy <= App.GPS_ACCURACY_GOOD_THRESHOLD) {
            // good GPS accuracy
            view.updateGPSIconGood();
        }
    }
}
