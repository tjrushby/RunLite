package com.tjrushby.runlite.contracts;

import java.util.Date;

public interface RunContract {
    interface Activity {
        void startService();

        void nextTick();

        void pauseTick();

        void endActivity();

        void endRun(String runId);

        void displayGPSModeToast();

        void displayEndRunAlertDialog();

        void displayExitAlertDialog();

        void defaultScreenTimeout();

        void noScreenTimeout();

        void disableSeekBar();

        void enableSeekBar();

        int getSeekBarProgress();

        void setSeekBarProgress(int progress);

        void fadeIconLock();

        void tintIconLock();

        void fadeIconUnlock();

        void tintIconUnlock();

        void hideButtonPause();

        void showButtonPause();

        void hideButtonStart();

        void showButtonStart();

        void disableButtonsStartPauseStop();

        void enableButtonsStartPauseStop();

        void updateButtonStartText();

        void updateGPSIconAverage();

        void updateGPSIconBad();

        void updateGPSIconGood();

        void updateTextViewTime(String timeElapsed);

        void updateTextViewDistance(String distanceTravelled);

        void updateTextViewPace(String currentPace);

        void setTextViewPaceDefaultText();
    }

    interface Presenter {
        void onViewCreated();

        void havePermissions();

        void onBackPressed();

        void onTick();

        void onButtonStartPressed();

        void onButtonPausePressed();

        void onButtonStopPressed();

        void enableHighAccuracyDialogNo();

        void endRunAlertDialogNo();

        void endRunAlertDialogYes();

        void exitRunAlertDialogYes();

        void onSeekBarChanged();
    }

    interface Model {
        long getId();

        void setId(long id);

        Date getDateTime();

        void setDateTime(Date dateTime);

        double getCurrentAccuracy();

        void setCurrentAccuracy(double currentAccuracy);

        double getCurrentSpeed();

        void setCurrentSpeed(double currentSpeed);

        double getDistanceTravelled();

        void setDistanceTravelled(double distanceTravelled);

        long getTimeElapsed();

        void setTimeElapsed(long timeElapsed);
    }

    interface Service {
        void startLocationUpdates();

        void stopLocationUpdates();
    }
}
