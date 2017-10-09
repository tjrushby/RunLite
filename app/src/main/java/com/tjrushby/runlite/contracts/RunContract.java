package com.tjrushby.runlite.contracts;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

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
        Date getDateTime();
        void setDateTime(Date dateTime);
        double getCurrentAccuracy();
        void setCurrentAccuracy(double currentAccuracy);
        double getCurrentSpeed();
        void setCurrentSpeed(double currentSpeed);
        double getDistanceTravelled();
        void setDistanceTravelled(double distanceTravelled);
        List<LatLng> getRunCoordinates();
        void setRunCoordinates(List<LatLng> runCoordinates);
        long getTimeElapsed();
        void setTimeElapsed(long timeElapsed);
    }

    interface Service {
        void startLocationUpdates();
        void stopLocationUpdates();
    }
}
