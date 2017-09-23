package com.tjrushby.runlite.contracts;

import com.tjrushby.runlite.injection.components.RunningActivityComponent;

public interface RunContract {
    interface Activity {
        void startService();
        void nextTick();
        void pauseTick();
        void endRun();
        void displayEndRunAlertDialog();
        void displayExitAlertDialog();
        void defaultScreenTimeout();
        void noScreenTimeout();
        void hideButtonLock();
        void showButtonLock();
        void hideButtonUnlock();
        void showButtonUnlock();
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
        RunningActivityComponent getComponent();
    }

    interface Presenter {
        void havePermissions();
        void confirmExit();
        void onTick();
        void startRun();
        void pauseRun();
        void stopRun();
        void endRunAlertDialogNo();
        void endRunAlertDialogYes();
        void lockButtons();
        void unlockButtons();
    }

    interface Model {
        double getCurrentAccuracy();
        void setCurrentAccuracy(double currentAccuracy);
        double getCurrentSpeed();
        void setCurrentSpeed(double currentSpeed);
        double getDistanceTravelled();
        void setDistanceTravelled(double distanceTravelled);
    }

    interface Service {
        void startLocationUpdates();
        void stopLocationUpdates();
    }
}
