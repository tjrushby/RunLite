package com.tjrushby.runlite.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.injection.modules.DetailsActivityModule;
import com.tjrushby.runlite.models.RunLatLng;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        presenter.onViewCreated(getIntent().getStringExtra("runId"));
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
    public void displayDeleteRunAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Run?")
                .setMessage("Are you sure? This cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> presenter.onDeleteRunAlertDialogYes())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void displayNotFoundErrorToast() {
        Toast.makeText(this, "Error. Could not load run.", Toast.LENGTH_SHORT).show();
        endActivity();
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void getMapFragment() {
        // get GoogleMap from MapFragment
        ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment))
                .getMapAsync(this);
    }

    @Override
    public void calculateMapBounds(List<RunLatLng> runCoordinates) {
        for (RunLatLng coordinates : runCoordinates) {
            mapBounds.include(coordinates.toLatLng());
        }

        map.setLatLngBoundsForCameraTarget(mapBounds.build());
    }

    @Override
    public void calculateMapPolyline(List<RunLatLng> runCoordinates) {
        for (RunLatLng coordinates : runCoordinates) {
            polylineOptions.add(coordinates.toLatLng());
        }

        map.addPolyline(polylineOptions);
    }

    @Override
    public void addMapMarkers(List<RunLatLng> runCoordinates) {
        map.addMarker(markerOptions.position(runCoordinates.get(0).toLatLng()));
        map.addMarker(markerOptions.position(runCoordinates.get(runCoordinates.size() - 1)
                .toLatLng()));
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
