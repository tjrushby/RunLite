package com.tjrushby.runlite.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.injection.modules.DetailsActivityModule;
import com.tjrushby.runlite.models.RunLatLng;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

public class DetailsActivity extends BaseActivity
        implements DetailsContract.Activity, OnMapReadyCallback {

    @Inject
    protected AlertDialog.Builder builder;
    @Inject
    protected DetailsContract.Presenter presenter;
    @Inject
    protected IconGenerator iconGenerator;
    @Inject
    protected Intent intent;
    @Inject
    protected PolylineOptions polylineOptions;
    @Inject
    protected LatLngBounds.Builder mapBounds;
    @Inject
    protected MarkerOptions markerOptions;

    @BindView(R.id.ivFullscreen)
    protected AppCompatImageView ivFullscreen;
    @BindView(R.id.ivMinimize)
    protected AppCompatImageView ivMinimize;

    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

    @BindView(R.id.clRunDateTime)
    protected CoordinatorLayout clRunDateTime;
    @BindView(R.id.rlMap)
    protected RelativeLayout rlMap;

    @BindView(R.id.tvTimeElapsed)
    protected TextView tvTimeElapsed;
    @BindView(R.id.tvDistance)
    protected TextView tvDistance;
    @BindView(R.id.tvAveragePaceString)
    protected TextView tvAveragePace;
    @BindView(R.id.tvRunDateTime)
    protected TextView tvRunDateTime;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private double distanceUnits;

    private GoogleMap map;

    @OnClick(R.id.ivFullscreen)
    public void imageViewFullscreenClicked() {
        presenter.onImageViewFullscreenClicked();
    }

    @OnClick(R.id.ivMinimize)
    public void imageViewMinimizeClicked() {
        presenter.onImageViewMinimizeClicked();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // inject dependencies
        App.getAppComponent()
                .plus(new DetailsActivityModule(this, this))
                .inject(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.hasExtra("UPDATED_DETAILS")) {
            presenter.onRunDetailsChanged(intent.getStringArrayExtra("UPDATED_DETAILS"));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        presenter.onViewCreated(getIntent().getStringExtra("runId"));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.onBackPressed();
                return true;

            case R.id.action_edit:
                presenter.onActionEditSelected();
                return true;

            case R.id.action_delete:
                presenter.onActionDeleteSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void endActivityWithIntent() {
        startActivity(intent
                .setClass(this, MainActivity.class)
                .putExtra("UPDATED", true)
        );

        this.finish();
    }

    @Override
    public void startEditActivity(String distanceTravelled) {
        startActivity(intent
                .setClass(this, EditActivity.class)
                .putExtra("RUN_DETAILS", new String[] {
                        tvTimeElapsed.getText().toString(),
                        distanceTravelled,
                        tvAveragePace.getText().toString()
                })
        );
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
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds.build(), 100));
    }

    @Override
    public void displayProgressBar(boolean display) {
        if(display) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(GONE);
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
    public void displayFullscreenIcon() {
        ivMinimize.setVisibility(GONE);
        ivFullscreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayMinimizeIcon() {
        ivFullscreen.setVisibility(GONE);
        ivMinimize.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayFullscreenMap() {
        // move clRunDateTime down to bottom of parent
        ConstraintLayout.LayoutParams lpRunDateTime = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lpRunDateTime.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        lpRunDateTime.validate();

        clRunDateTime.setLayoutParams(lpRunDateTime);

        // increase size of map down to the top of clRunDateTime
        ConstraintLayout.LayoutParams lpMap = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels -
                        getResources().getDimensionPixelSize(R.dimen.seek_thumb_height)
        );

        lpMap.bottomToTop = R.id.clRunDateTime;
        lpMap.validate();

        rlMap.setLayoutParams(lpMap);
    }

    @Override
    public void displaySmallMap() {
        // decrease size of map back to default
        ConstraintLayout.LayoutParams lpMap = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lpMap.topToBottom = R.id.appBarLayout;
        lpMap.bottomToTop = R.id.guidelineRunDetails;
        lpMap.validate();

        rlMap.setLayoutParams(lpMap);

        // reposition clRunDateTime back to original location
        ConstraintLayout.LayoutParams lpRunDateTime = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lpRunDateTime.topToBottom = R.id.guidelineRunDetails;
        lpRunDateTime.validate();

        clRunDateTime.setLayoutParams(lpRunDateTime);
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
    public String getTextViewTimeElapsed() {
        return tvTimeElapsed.getText().toString();
    }

    @Override
    public String getTextViewDistance() {
        return tvDistance.getText().toString();
    }

    @Override
    public void setDistanceUnits(double distanceUnits) {
        this.distanceUnits = distanceUnits;
    }

    @Override
    public void setTextViewRunDateTime(String headerText) {
        tvRunDateTime.setText(headerText);
    }

    @Override
    public void setTextViews(String time, String distance, String averagePace) {
        tvTimeElapsed.setText(time);
        tvDistance.setText(distance);
        tvAveragePace.setText(averagePace);
    }
}
