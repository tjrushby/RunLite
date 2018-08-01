package com.tjrushby.runlite.contracts;

import com.tjrushby.runlite.models.RunLatLng;

import java.util.List;

public interface DetailsContract {
    interface Activity {
        void endActivity();

        void endActivityWithIntent();

        void startEditActivity(String distanceTravelled);

        void getMapFragment();

        void addMapMarkers(List<RunLatLng> runCoordinates);

        void moveMapCamera();

        void calculateMapBounds(List<RunLatLng> runCoordinates);

        void calculateMapPolyline(List<RunLatLng> runCoordinates);

        void displayProgressBar(boolean display);

        void displayDeleteRunAlertDialog();

        void displayNotFoundErrorToast();

        void displayRunDeletedToast();

        void displayRunUpdatedToast();

        void displayFullscreenIcon();

        void displayMinimizeIcon();

        void displayLargeMap();

        void displaySmallMap();

        void setDistanceUnits(double distanceUnits);

        void setTextViewRunDateTime(String headerText);

        void setTextViews(String time, String distance, String averagePace);
    }

    interface Presenter {
        void onViewCreated(String runId);

        void onBackPressed();

        void onActionDeleteSelected();

        void onActionEditSelected();

        void onDeleteRunAlertDialogYes();

        void onImageViewFullscreenClicked();

        void onImageViewMinimizeClicked();

        void onMapFragmentReady();

        void onMapLoaded();

        void onRunDetailsChanged(String[] details);
    }
}
