package com.tjrushby.runlite.contracts;

import com.tjrushby.runlite.models.RunLatLng;

import java.util.List;

public interface DetailsContract {
    interface Activity {
        void endActivity();

        void getMapFragment();

        void calculateMapBounds(List<RunLatLng> runCoordinates);

        void calculateMapPolyline(List<RunLatLng> runCoordinates);

        void displayDeleteRunAlertDialog();

        void displayNotFoundErrorToast();

        void addMapMarkers(List<RunLatLng> runCoordinates);

        void moveMapCamera();

        void updateTextViews(String time, String distance, String averagePace);
    }

    interface Presenter {
        void onViewCreated(String runId);

        void onButtonDoneClicked();

        void onButtonDeleteClicked();

        void onDeleteRunAlertDialogYes();

        void onMapFragmentReady();

        void onMapLoaded();
    }
}
