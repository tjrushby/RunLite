package com.tjrushby.runlite.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.dialogs.TimePickerDialog;
import com.tjrushby.runlite.dialogs.TimePickerDialogListener;
import com.tjrushby.runlite.injection.modules.DetailsActivityModule;
import com.tjrushby.runlite.models.RunLatLng;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class DetailsActivity extends BaseActivity
        implements DetailsContract.Activity, TimePickerDialogListener, OnMapReadyCallback {

    @Inject
    protected AlertDialog.Builder builder;
    @Inject
    protected Bundle bundle;
    @Inject
    protected DetailsContract.Presenter presenter;
    @Inject
    protected IconGenerator iconGenerator;
    @Inject
    protected PolylineOptions polylineOptions;
    @Inject
    protected LatLngBounds.Builder mapBounds;
    @Inject
    protected MarkerOptions markerOptions;
    @Inject
    protected TimePickerDialog timePickerDialog;

    @BindView(R.id.ivFullscreen)
    protected AppCompatImageView ivFullscreen;
    @BindView(R.id.ivMinimize)
    protected AppCompatImageView ivMinimize;

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
    @BindView(R.id.tvDistanceUnit)
    protected TextView tvDistanceUnit;
    @BindView(R.id.tvPaceUnit)
    protected TextView tvPaceUnit;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.rlMap)
    protected RelativeLayout rlMap;

    private double distanceUnits;

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

    @OnClick(R.id.ivFullscreen)
    public void imageViewFullscreenClicked() {
        presenter.onImageViewFullscreenClicked();
    }

    @OnClick(R.id.ivMinimize)
    public void imageViewMinimizeClicked() {
        presenter.onImageViewMinimizeClicked();
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
    public void addMapMarkers(List<RunLatLng> mapMarkers) {
        for (RunLatLng runLatLng : mapMarkers) {
            map.addMarker(markerOptions
                    .position(runLatLng.toLatLng())
                    .flat(true)
            ).setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                            iconGenerator.makeIcon(
                                    new BigDecimal(runLatLng.getDistanceInRun() / distanceUnits)
                                            .setScale(2, BigDecimal.ROUND_HALF_UP)
                                            .toString()
                            )

                    )
            );
        }
    }

    @Override
    public void moveMapCamera() {
        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(mapBounds.build(), 100),
                1000,
                null
        );
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
        Toast.makeText(this, R.string.toast_run_not_found, Toast.LENGTH_SHORT).show();
        endActivity();
    }

    @Override
    public void displayRunDeletedToast() {
        Toast.makeText(this, R.string.toast_run_deleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayRunUpdatedToast() {
        Toast.makeText(this, R.string.toast_run_updated, Toast.LENGTH_SHORT).show();
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
    public void displayFullscreenIcon() {
        ivMinimize.setVisibility(View.GONE);
        ivFullscreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayMinimizeIcon() {
        ivFullscreen.setVisibility(View.GONE);
        ivMinimize.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayFullscreenMap() {
        rlMap.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        rlMap.bringToFront();
    }

    @Override
    public void displaySmallMap() {
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp.topToBottom = R.id.appBarLayout;
        lp.bottomToTop = R.id.guidelineDetailsHeader;
        lp.validate();

        rlMap.setLayoutParams(lp);
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
    public void setDistanceUnits(double distanceUnits) {
        this.distanceUnits = distanceUnits;
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
    public void setTextViewsDistanceUnit(String distanceUnitString) {
        tvDistanceUnit.setText(distanceUnitString);
        tvPaceUnit.setText("Mins/" + distanceUnitString);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
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
    public void onTimePickerDialogPositiveClick(int timeElapsed) {
        presenter.onEditTextTimeElapsedChanged(timeElapsed);
    }
}
