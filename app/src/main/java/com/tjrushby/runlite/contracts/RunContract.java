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

        void animateButtonUnlockScaleUp();

        void animateButtonUnlockScaleDown();

        void hideButtonAudioDisable();

        void showButtonAudioDisable();

        void hideButtonAudioEnable();

        void showButtonAudioEnable();

        void hideButtonLock();

        void showButtonLock();

        void hideButtonPause();

        void showButtonPause();

        void hideButtonStart();

        void showButtonStart();

        void hideButtonStop();

        void showButtonStop();

        void hideButtonUnlock();

        void showButtonUnlock();

        void hideIVLocationAverage();

        void showIVLocationAverage();

        void hideIVLocationBad();

        void showIVLocationBad();

        void hideIVLocationGood();

        void showIVLocationGood();

        void hideIVUnlockCircle();

        void showIVUnlockCircle();

        boolean isAudioCueEnabled();

        String getAudioCueType();

        double getAudioCueDistanceInterval();

        int getAudioCueTimeInterval();

        void setButtonUnlockDefaultScale();

        void setColorAccentTypedValue();

        int getGPSAccuracyBadThreshold();

        int getGPSAccuracyGoodThreshold();

        void setTextViewDistance(String distanceTravelled);

        void setTextViewAveragePace(String currentPace);

        void setTextViewPaceDefaultText();

        void setTextViewTime(String timeElapsed);
    }

    interface Presenter {
        void onActivityCreated();

        void onActivityResumed();

        void havePermissions();

        void onBackPressed();

        void onTick();

        void onButtonLockPressed();

        void onButtonUnlockActionDown();

        void onButtonUnlockActionUp();

        void onButtonStartPressed();

        void onButtonPausePressed();

        void onButtonStopPressed();

        void onDialogEnableHighAccuracyNo();

        void onDialogEndRunNo();

        void onDialogEndRunYes();

        void onDialogCancelRunYes();

        void onViewUnlocked();
    }

    interface RunService {
        void startLocationUpdates();

        void stopLocationUpdates();

        boolean isRunning();

        void setRunning(boolean running);
    }

}
