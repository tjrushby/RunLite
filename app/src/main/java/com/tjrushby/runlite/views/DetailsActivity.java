package com.tjrushby.runlite.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.injection.modules.DetailsActivityModule;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class DetailsActivity extends AppCompatActivity
        implements DetailsContract.Activity, OnMapReadyCallback {

    @Inject
    public DetailsContract.Presenter presenter;
    @Inject
    public PolylineOptions polylineOptions;
    @Inject
    public LatLngBounds.Builder mapBounds;
    @Inject
    public MarkerOptions markerOptions;

    private GoogleMap map;

    @BindView(R.id.tvAveragePace)
    protected TextView tvAveragePace;
    @BindView(R.id.tvDistance)
    protected TextView tvDistance;
    @BindView(R.id.tvTimeElapsed)
    protected TextView tvTimeElapsed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        // inject dependencies
        App.getAppComponent().plus(new DetailsActivityModule(this)).inject(this);

        // get GoogleMap from MapFragment
        ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment))
                .getMapAsync(this);

        presenter.onViewCreated();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        presenter.onMapFragmentReady();

        googleMap.setOnMapLoadedCallback(() -> {
            presenter.onMapLoaded();
        });
    }

    @OnClick(R.id.buttonDone)
    public void buttonDoneClicked() {
        presenter.onButtonDoneClicked();
    }

    @OnClick(R.id.buttonDelete)
    public void buttonDeleteClicked() {
        presenter.onButtonDeleteClicked();
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void calculateMapBounds(List<LatLng> runCoordinates) {
        for (LatLng coordinates : runCoordinates) {
            mapBounds.include(coordinates);
        }

        map.setLatLngBoundsForCameraTarget(mapBounds.build());
    }

    @Override
    public void calculateMapPolyline(List<LatLng> runCoordinates) {
        polylineOptions.addAll(runCoordinates);
        map.addPolyline(polylineOptions);
    }

    @Override
    public void addMapMarkers(List<LatLng> runCoordinates) {
        map.addMarker(markerOptions.position(runCoordinates.get(0)));
        map.addMarker(markerOptions.position(runCoordinates.get(runCoordinates.size() - 1)));
    }

    @Override
    public void moveMapCamera() {
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds.build(), 100));
    }

    @Override
    public void updateTextViews(String time, String distance, String averagePace) {
        tvTimeElapsed.setText(time);
        tvDistance.setText(distance);
        tvAveragePace.setText(averagePace);
    }
}
