package com.tjrushby.runlite.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import timber.log.Timber;

import static android.view.View.GONE;

public class DetailsActivity extends BaseActivity
        implements DetailsContract.Activity, OnMapReadyCallback, View.OnTouchListener {

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
    protected LatLngBounds.Builder mapBoundsBuilder;
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
    @BindView(R.id.clRunDetails)
    protected CoordinatorLayout clRunDetails;
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

    private static final int EDIT_RUN_REQUEST = 1;

    private double distanceUnits;

    private LatLngBounds mapBounds;
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

        // prevent being able to move the map with touch events on these views
        clRunDateTime.setOnTouchListener(this);
        clRunDetails.setOnTouchListener(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getDarkThemeEnabled()) {
            // change toolbar and actionbar icons to be more visible in dark mode
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back_grey);
        }

        // inject dependencies
        App.getAppComponent()
                .plus(new DetailsActivityModule(this, this))
                .inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EDIT_RUN_REQUEST) {
            if(resultCode == RESULT_OK) {
                // user edited the run
                presenter.onRunDetailsChanged(data.getStringArrayExtra("UPDATED_DETAILS"));
            }
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
    public void endActivityResultCancelled() {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    @Override
    public void endActivityResultOK() {
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void startEditActivity(String distanceTravelled) {
        startActivityForResult(
                intent.setClass(this, EditActivity.class)
                        .putExtra("RUN_DETAILS",
                                new String[]{
                                        tvTimeElapsed.getText().toString(),
                                        distanceTravelled,
                                        tvAveragePace.getText().toString()
                                }
                        ),
                EDIT_RUN_REQUEST);
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
    public void animateMapCameraForLargeMap() {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(
                mapBounds,
                rlMap.getWidth(),
                rlMap.getHeight() - clRunDateTime.getHeight(),
                125)
        );
    }

    @Override
    public void animateMapCameraForSmallMap() {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(
                mapBounds,
                rlMap.getWidth(),
                rlMap.getHeight() - (clRunDateTime.getHeight() + clRunDetails.getHeight()),
                125
        ), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                map.animateCamera(CameraUpdateFactory.scrollBy(0,
                        (clRunDateTime.getHeight() + clRunDetails.getHeight()) / 2)
                );
            }

            @Override
            public void onCancel() {
                // do nothing
            }
        });
    }

    @Override
    public void moveMapCameraForSmallMap() {
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                mapBounds,
                rlMap.getWidth(),
                rlMap.getHeight() - (clRunDateTime.getHeight() + clRunDetails.getHeight()),
                125
        ));

        map.moveCamera(CameraUpdateFactory.scrollBy(0,
                (clRunDateTime.getHeight() + clRunDetails.getHeight()) / 2)
        );
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
        endActivityResultCancelled();
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
    public void displayLargeMap() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.clMinMax));
        TransitionManager.beginDelayedTransition(clRunDateTime);
        TransitionManager.beginDelayedTransition(clRunDetails);

        // move clRunDateTime down to bottom of parent
        ConstraintLayout.LayoutParams lpRunDateTime = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lpRunDateTime.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        lpRunDateTime.validate();

        clRunDateTime.setLayoutParams(lpRunDateTime);

        // constrain clRunDetails to bottom of clRunDateTime so it slides down with it
        ConstraintLayout.LayoutParams lpRunDetails = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lpRunDetails.topToBottom = R.id.clRunDateTime;
        lpRunDetails.validate();

        clRunDetails.setLayoutParams(lpRunDetails);

        // change homeAsUp indicator to a cross
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close_grey);
    }

    @Override
    public void displaySmallMap() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.clMinMax));
        TransitionManager.beginDelayedTransition(clRunDateTime);
        TransitionManager.beginDelayedTransition(clRunDetails);

        // constrain clRunDetails to bottom of parent
        ConstraintLayout.LayoutParams lpRunDetails = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lpRunDetails.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        lpRunDetails.validate();

        clRunDetails.setLayoutParams(lpRunDetails);

        // reposition clRunDateTime back to original location
        ConstraintLayout.LayoutParams lpRunDateTime = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        lpRunDateTime.bottomToTop = R.id.clRunDetails;
        lpRunDateTime.validate();

        clRunDateTime.setLayoutParams(lpRunDateTime);

        // change homeAsUp indicator to back arrow
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back_grey);
    }

    @Override
    public void getMapFragment() {
        // get GoogleMap from MapFragment
        ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment))
                .getMapAsync(this);
    }

    @Override
    public void calculateMapBounds(List<RunLatLng> runCoordinates) {
        for (LatLng coordinates : polylineOptions.getPoints()) {
            mapBoundsBuilder.include(coordinates);
        }

        mapBounds = mapBoundsBuilder.build();

        // set map camera bounds
        map.setLatLngBoundsForCameraTarget(mapBounds);
    }

    @Override
    public void calculateMapPolyline(List<RunLatLng> runCoordinates) {
        for (RunLatLng coordinates : runCoordinates) {
            polylineOptions.add(coordinates.toLatLng());
        }

        map.addPolyline(polylineOptions);
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }
}
