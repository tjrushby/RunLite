package com.tjrushby.runlite.contracts;

import java.util.Date;

public interface RunContract {
    interface Activity {
        void startServices();

        void speak(String message);

        void endActivity();

        void startDetailsActivity(String runId);

        void displayNotification();

        void removeNotification();

        void setNotificationContent(String content);

        void setNotificationContentTitle(String contentTitle);

        void displayGPSModeToast();

        void displayNoSaveToast();

        void displaySaveToast();

        void displayEndRunAlertDialog();

        void displayExitAlertDialog();

        void defaultScreenTimeout();

        void noScreenTimeout();

        void nextTick();

        void pauseTick();

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

        void updateColorAccentTypedValue();

        void updateGPSIconAverage();

        void updateGPSIconBad();

        void updateGPSIconGood();

        void updateTextViewTime(String timeElapsed);

        void updateTextViewDistance(String distanceTravelled);

        void updateTextViewPace(String currentPace);

        void setNotificationActionPause();

        void setNotificationActionResume();

        void setTextViewPaceDefaultText();

        void setTextViewsDistanceUnit(String distanceUnitsString);

        boolean isAudioCueEnabled();

        String getAudioCueType();

        double getAudioCueDistanceInterval();

        int getAudioCueTimeInterval();
    }

    interface Presenter {
        void onActivityCreated();

        void onActivityResumed();

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

    interface RunService {
        void startLocationUpdates();

        void stopLocationUpdates();

        boolean isRunning();

        void setRunning(boolean running);
    }

}
