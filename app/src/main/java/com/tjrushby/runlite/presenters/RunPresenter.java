package com.tjrushby.runlite.presenters;

import javax.inject.Inject;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.util.StringFormatter;

import timber.log.Timber;

public class RunPresenter implements RunContract.Presenter {
    private RunContract.Activity view;
    private RunContract.Model model;
    private RunContract.Service service;

    private StringFormatter formatter;

    private long timeElapsed;

    @Inject
    public RunPresenter(RunContract.Activity view,
                        RunContract.Model model,
                        RunContract.Service service,
                        StringFormatter formatter) {
        this.view = view;
        this.model = model;
        this.service = service;
        this.formatter = formatter;

        Timber.d("view: " + view);
        Timber.d("presenter: " + this);
        Timber.d("model: " + model);
        Timber.d("service: " + service);

        timeElapsed = 0;
    }

    @Override
    public void havePermissions() {
        view.startService();
    }

    @Override
    public void confirmExit() {
        view.displayExitAlertDialog();
    }

    // update the view with the latest data
    @Override
    public void onTick() {
        // increment the timer
        timeElapsed += 1;

        // calculate the average pace
        double currentPace = 0;

        if(model.getDistanceTravelled() > 0) {
            currentPace = timeElapsed / model.getDistanceTravelled();
        }

        // update the TextView elements
        if(currentPace == 0) {
            view.setTextViewPaceDefaultText();
        } else {
            view.updateTextViewPace(formatter.longToMinutesSeconds((long) currentPace));
        }

        determineGPSIcon(model.getCurrentAccuracy());
        view.updateTextViewTime(formatter.longToMinutesSeconds(timeElapsed));
        view.updateTextViewDistance(formatter.doubleToDistanceString(model.getDistanceTravelled()));

        view.nextTick();
    }

    // tell model to start requesting location updates and the view to start a Runnable
    @Override
    public void startRun() {
        // enabled the stop button
        view.enableButtonsStartPauseStop();

        // start requesting Location updates from locationClient
        service.startLocationUpdates();

        // start the timer
        view.nextTick();

        // hide the start button and display the pause button, disable the stop button
        view.hideButtonStart();
        view.showButtonPause();
    }

    // tell model to displayEndRunAlertDialog requesting location updates and the view to not post another Runnable
    @Override
    public void pauseRun() {
        // displayEndRunAlertDialog requesting Location updates from locationClient whilst paused
        service.stopLocationUpdates();

        // pause the timer
        view.pauseTick();

        // hide the pause button and display the start button, setting the start button text to resume
        view.hideButtonPause();
        view.showButtonStart();
        view.updateButtonStartText();
        view.setTextViewPaceDefaultText();
    }

    // end the view Activity
    @Override
    public void stopRun() {
        pauseRun();
        view.displayEndRunAlertDialog();
    }

    @Override
    public void endRunAlertDialogNo() {
        startRun();
    }

    @Override
    public void endRunAlertDialogYes() {
        model.setTimeElapsed(timeElapsed);
        view.endRun();
    }

    @Override
    public void lockButtons() {
        view.disableButtonsStartPauseStop();
        view.hideButtonLock();
        view.showButtonUnlock();
        view.noScreenTimeout();
    }

    @Override
    public void unlockButtons() {
        view.enableButtonsStartPauseStop();
        view.hideButtonUnlock();
        view.showButtonLock();
        view.defaultScreenTimeout();
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
