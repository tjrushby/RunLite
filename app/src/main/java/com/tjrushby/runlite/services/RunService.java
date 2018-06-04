package com.tjrushby.runlite.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.tjrushby.runlite.App;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.views.RunActivity;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class RunService extends Service implements RunContract.RunService {
    private static final long UPDATE_INTERVAL = 5000; // five seconds
    private static final long FASTEST_INTERVAL = 1000; // one second

    private boolean running;
    private double currentAccuracy;
    private double currentSpeed;
    private double distanceTravelled;
    private long locationTimeElapsed;

    private Context context;
    private DecimalFormat df;
    private RunContract.Activity view;
    private Run model;
    private List<RunLatLng> runLatLngs;

    private FusedLocationProviderClient locationClient;
    private Location lastLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    public RunService() {}

    @Inject
    public RunService(Context context,
                      DecimalFormat df,
                      RunContract.Activity view,
                      Run model,
                      List<RunLatLng> runLatLngs,
                      FusedLocationProviderClient locationClient,
                      LocationRequest locationRequest,
                      LocationSettingsRequest.Builder builder) {

        this.context = context;
        this.df = df;
        this.view = view;
        this.model = model;
        this.runLatLngs = runLatLngs;
        this.locationClient = locationClient;
        this.locationRequest = locationRequest;

        // settings for locationRequest
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        builder.addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnFailureListener(apiException -> {
            if(apiException instanceof ResolvableApiException) {
                // location settings are not satisfied
                try {
                    // show a dialog to the user prompting them to enable high accuracy location
                    ResolvableApiException resolvable = (ResolvableApiException) apiException;
                    if(view instanceof Activity) {
                        resolvable.startResolutionForResult(
                                (RunActivity) view,
                                ((RunActivity) view).REQUEST_HIGH_ACCURACY_GPS
                        );
                    }
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error
                }
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    }

    // start requesting location updates from the FusedLocationProviderClient
    @SuppressWarnings({"MissingPermission"})
    @Override
    public void startLocationUpdates() {
        Timber.d("starting location updates...");
        locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    // stop requesting location updates from the FusedLocationProviderClient
    @Override
    public void stopLocationUpdates() {
        if(locationCallback != null) {
            Timber.d("stopping location updates...");
            locationClient.removeLocationUpdates(locationCallback);
        }
    }

    // callback method for FusedLocationClientProvider.requestLocationUpdates()
    // calculates the distance travelled and time elapsed since the previously received location
    // updates and gets the current accuracy
    public void onLocationChanged(Location location) {
        // update gps accuracy
        if(location.hasAccuracy()) {
            currentAccuracy = location.getAccuracy();
        } else {
            // location has no accuracy value, assume bad gps accuracy
            currentAccuracy = App.GPS_ACCURACY_BAD_THRESHOLD;
        }

        if(running) {
            // update locationTimeElapsed and distanceTravelled
            if(lastLocation != null) {
                // calculate the time and distance (in km) between location and lastLocation
                double timeDifference = location.getTime() - lastLocation.getTime();
                double distanceDifference = location.distanceTo(lastLocation) / 1000;

                Timber.d("distanceTravelled: " + distanceTravelled);
                Timber.d("distanceDifference: " + distanceDifference);

                // add the new differences to locationTimeElapsed and distanceTravelled
                locationTimeElapsed += timeDifference;
                distanceTravelled += distanceDifference;

                Timber.d("newDistanceTravelled: " + distanceTravelled);

                // determine speed
                if(!location.hasSpeed() && distanceTravelled != 0) {
                    // location has no speed value, calculate manually
                    currentSpeed = distanceTravelled * 1000 / locationTimeElapsed;  // km per hour
                } else {
                    currentSpeed = location.getSpeed() * 3.6;   // km per hour
                }

                // update the model
                model.setCurrentAccuracy(currentAccuracy);
                model.setAveragePace(currentSpeed);
                model.setDistanceTravelled(distanceTravelled);
            }

            // add the current latitude and longitude
            runLatLngs.add(
                    new RunLatLng(
                            location.getLatitude(),
                            location.getLongitude(),
                            Double.parseDouble(df.format(distanceTravelled))
                    )
            );

            // update lastLocation to location now that all calculations have been performed
            lastLocation = location;
        } else if(!running) {
            lastLocation = null;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }
}
