package com.tjrushby.runlite.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.tjrushby.runlite.dialogs.TimePickerDialog;
import com.tjrushby.runlite.dialogs.TimePickerDialogListener;
import com.tjrushby.runlite.injection.modules.DetailsActivityModule;
import com.tjrushby.runlite.models.RunLatLng;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class DetailsActivity extends AppCompatActivity
        implements DetailsContract.Activity, TimePickerDialogListener, OnMapReadyCallback {

    @Inject
    public AlertDialog.Builder builder;
    @Inject
    public Bundle bundle;
    @Inject
    public DetailsContract.Presenter presenter;
    @Inject
    public PolylineOptions polylineOptions;
    @Inject
    public LatLngBounds.Builder mapBounds;
    @Inject
    public MarkerOptions markerOptions;
    @Inject
    public TimePickerDialog timePickerDialog;

    @BindView(R.id.buttonDone)
    protected Button buttonDone;
    @BindView(R.id.buttonUpdate)
    protected Button buttonUpdate;

    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

    @BindView(R.id.etTimeElapsed)
    protected EditText etTimeElapsed;
    @BindView(R.id.etDistance)
    protected TextInputEditText etDistance;
    @BindView(R.id.tilDistance)
    protected TextInputLayout tilDistance;
    @BindView(R.id.tvAveragePace)
    protected TextView tvAveragePace;

    private GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        // inject dependencies
        App.getAppComponent().plus(new DetailsActivityModule(this, this)).inject(this);

        presenter.onViewCreated(getIntent().getStringExtra("runId"));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @OnClick(R.id.buttonUpdate)
    public void buttonUpdateClicked() {
        presenter.onButtonUpdateClicked();
    }

    @OnClick(R.id.buttonDelete)
    public void buttonDeleteClicked() {
        presenter.onButtonDeleteClicked();
    }

    @OnClick(R.id.buttonDone)
    public void buttonDoneClicked() {
        presenter.onButtonDoneClicked();
    }

    @OnClick(R.id.etTimeElapsed)
    public void editTextTimeElapsedClicked() {
        presenter.onEditTextTimeElapsedClicked();
    }

    @OnTextChanged(R.id.etDistance)
    public void editTextDistanceEdited() {
        presenter.onEditTextDistanceChanged();
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        presenter.onMapFragmentReady();

        googleMap.setOnMapLoadedCallback(() -> presenter.onMapLoaded());
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
    public void displayProgressBar(boolean display) {
        if(display) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void displayDeleteRunAlertDialog() {
        builder.setTitle("Delete Run?")
               .setMessage("Are you sure? This cannot be undone.")
               .setPositiveButton("Yes", (dialog, which) -> presenter.onDeleteRunAlertDialogYes())
               .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
               .show();
    }

    @Override
    public void displayExitAlertDialog() {
        builder.setTitle("Go Back?")
               .setMessage("Are you sure? Changes will be discarded.")
               .setPositiveButton("Yes", (dialog, which) -> presenter.onExitAlertDialogYes())
               .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
               .show();
    }

    @Override
    public void displayTimePickerDialog() {
        bundle.clear();
        bundle.putString("timeElapsed", etTimeElapsed.getText().toString());

        timePickerDialog.setArguments(bundle);
        timePickerDialog.show(getFragmentManager(), null);
    }

    @Override
    public void displayNotFoundErrorToast() {
        Toast.makeText(this, "Error. Could not load run.", Toast.LENGTH_SHORT).show();
        endActivity();
    }

    @Override
    public void displayEditTextDistanceEmptyError() {
        tilDistance.setError(getString(R.string.error_edit_text_distance_empty));
    }

    @Override
    public void displayEditTextDistanceNoNumbersError() {
        tilDistance.setError(getString(R.string.error_edit_text_distance_no_numbers));
    }

    @Override
    public void displayEditTextDistanceZeroError() {
        tilDistance.setError(getString(R.string.error_edit_text_distance_zero));
    }

    @Override
    public void clearEditTextDistanceError() {
        tilDistance.setError(null);
        tilDistance.setErrorEnabled(false);
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
    public String getEditTextDistance() {
        return etDistance.getText().toString();
    }

    @Override
    public void setEditTextDistance(String distance) {
        etDistance.setText(distance);
        etDistance.clearFocus();
    }

    @Override
    public String getEditTextTimeElapsed() {
        return etTimeElapsed.getText().toString();
    }

    @Override
    public void setEditTextTimeElapsed(String timeElapsed) {
        etTimeElapsed.setText(timeElapsed);
    }

    @Override
    public String getTextViewAveragePace() {
        return tvAveragePace.getText().toString();
    }

    @Override
    public void setTextViewAveragePace(String averagePace) {
        tvAveragePace.setText(averagePace);
    }

    @Override
    public void setTextViews(String time, String distance, String averagePace) {
        etTimeElapsed.setText(time);
        etDistance.setText(distance);
        tvAveragePace.setText(averagePace);
    }

    @Override
    public void hideButtonDone() {
        buttonDone.setVisibility(View.GONE);
    }

    @Override
    public void showButtonDone() {
        buttonDone.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideButtonUpdate() {
        buttonUpdate.setVisibility(View.GONE);
    }

    @Override
    public void showButtonUpdate() {
        buttonUpdate.setVisibility(View.VISIBLE);
    }

    @Override
    public void validateEditTextDistance() {
        String distanceText = etDistance.getText().toString();

        if(TextUtils.isEmpty(distanceText)) {
            presenter.onEditTextDistanceEmpty();
        } else if(distanceText.equals(".")) {
            presenter.onEditTextDistanceNoNumbers();
        } else if(Double.parseDouble(distanceText) == 0) {
            presenter.onEditTextDistanceZero();
        } else {
            presenter.onEditTextDistanceValid();
        }
    }

    @Override
    public void onTimePickerDialogPositiveClick(long timeElapsed) {
        presenter.onEditTextTimeElapsedUpdated(timeElapsed);
    }
}
