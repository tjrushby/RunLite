package com.tjrushby.runlite.contracts;


import com.google.android.gms.maps.model.LatLng;
import com.tjrushby.runlite.models.RunLatLng;

import java.util.List;

public interface DetailsContract {
    interface Activity {
        void endActivity();
        void calculateMapBounds(List<RunLatLng> runCoordinates);
        void calculateMapPolyline(List<RunLatLng> runCoordinates);
        void addMapMarkers(List<RunLatLng> runCoordinates);
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
