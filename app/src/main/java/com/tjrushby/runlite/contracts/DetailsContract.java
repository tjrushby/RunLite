package com.tjrushby.runlite.contracts;

import com.tjrushby.runlite.models.RunLatLng;

import java.util.List;

public interface DetailsContract {
    interface Activity {
        void endActivity();

        void getMapFragment();

        void addMapMarkers(List<RunLatLng> runCoordinates);

        void moveMapCamera();

        void calculateMapBounds(List<RunLatLng> runCoordinates);

        void calculateMapPolyline(List<RunLatLng> runCoordinates);

        void displayProgressBar(boolean display);

        void displayDeleteRunAlertDialog();

        void displayExitAlertDialog();

        void displayTimePickerDialog();

        void displayNotFoundErrorToast();

        void displayEditTextDistanceEmptyError();

        void displayEditTextDistanceNoNumbersError();

        void displayEditTextDistanceZeroError();

        void clearEditTextDistanceError();

        void displayFullscreenIcon();

        void displayMinimizeIcon();

        void displayFullscreenMap();

        void displaySmallMap();

        String getEditTextDistance();

        String getTextViewAveragePace();

        String getEditTextTimeElapsed();

        void setDistanceUnits(double distanceUnits);

        void setEditTextDistance(String distance);

        void setTextViewAveragePace(String averagePace);

        void setEditTextTimeElapsed(String timeElapsed);

        void setTextViews(String time, String distance, String averagePace);

        void setTextViewsDistanceUnit(String distanceUnitString);

        void setToolbarTitle(String title);

        void hideButtonDone();

        void showButtonDone();

        void hideButtonUpdate();

        void showButtonUpdate();

        void validateEditTextDistance();
    }

    interface Presenter {
        void onViewCreated(String runId);

        void onBackPressed();

        void onButtonDeleteClicked();

        void onButtonDoneClicked();

        void onButtonUpdateClicked();

        void onDeleteRunAlertDialogYes();

        void onExitAlertDialogYes();

        void onEditTextDistanceChanged();

        void onEditTextDistanceEmpty();

        void onEditTextDistanceNoNumbers();

        void onEditTextDistanceZero();

        void onEditTextDistanceValid();

        void onEditTextTimeElapsedClicked();

        void onEditTextTimeElapsedUpdated(long timeElapsed);

        void onImageViewFullscreenClicked();

        void onImageViewMinimizeClicked();

        void onMapFragmentReady();

        void onMapLoaded();
    }
}
