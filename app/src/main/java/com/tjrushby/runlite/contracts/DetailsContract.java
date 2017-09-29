package com.tjrushby.runlite.contracts;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface DetailsContract {
    interface Activity {
        void endActivity();
        void calculateMapBounds(List<LatLng> runCoordinates);
        void calculateMapPolyline(List<LatLng> runCoordinates);
        void addMapMarkers(List<LatLng> runCoordinates);
        void moveMapCamera();
        void updateTextViews(String time, String distance, String averagePace);
    }

    interface Presenter {
        void onViewCreated();
        void onButtonDoneClicked();
        void onButtonDeleteClicked();
        void onMapFragmentReady();
        void onMapLoaded();
    }
}
