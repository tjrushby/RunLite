package com.tjrushby.runlite.contracts;

public interface RunContract {
    interface Activity {
        void endActivity();

        void startDetailsActivity(String runId);

        void startServices();

        void speak(String message);

        void nextTick();

        void pauseTick();

        void displayNotification();

        void removeNotification();

        void setNotificationContent(String content);

        void setNotificationContentTitle(String contentTitle);

        void setNotificationActionPause();

        void setNotificationActionResume();

        void displayDialogCancelRun();

        void displayDialogEndRun();

        void displayToastGPSMode();

        void displayToastRunNotSaved();

        void displayToastRunSaved();

        void setScreenTimeoutDefault();

        void setScreenTimeoutNone();

        void disableSeekBar();

        void enableSeekBar();

        void disableButtonsStartPauseStop();

        void enableButtonsStartPauseStop();

        void hideButtonPause();

        void showButtonPause();

        void hideButtonStart();

        void showButtonStart();

        void tintIconGPSAverage();

        void tintIconGPSBad();

        void tintIconGPSGood();

        void fadeIconLock();

        void tintIconLock();

        void fadeIconUnlock();

        void tintIconUnlock();

        boolean isAudioCueEnabled();

        String getAudioCueType();

        double getAudioCueDistanceInterval();

        int getAudioCueTimeInterval();

        void setButtonStartText();

        void setColorAccentTypedValue();

        int getGPSAccuracyBadThreshold();

        int getGPSAccuracyGoodThreshold();

        int getSeekBarProgress();

        void setSeekBarProgress(int progress);

        void setTextViewsDistanceUnit(String distanceUnitsString);

        void setTextViewDistance(String distanceTravelled);

        void setTextViewPace(String currentPace);

        void setTextViewPaceDefaultText();

        void setTextViewTime(String timeElapsed);
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

        void onDialogEnableHighAccuracyNo();

        void onDialogEndRunNo();

        void onDialogEndRunYes();

        void onDialogCancelRunYes();

        void onSeekBarChanged();
    }

    interface RunService {
        void startLocationUpdates();

        void stopLocationUpdates();

        boolean isRunning();

        void setRunning(boolean running);
    }

}
