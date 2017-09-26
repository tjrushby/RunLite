package com.tjrushby.runlite.models;

import com.google.android.gms.maps.model.LatLng;
import com.tjrushby.runlite.contracts.RunContract;

import java.util.List;

public class RunModel implements RunContract.Model {
    private double currentAccuracy;
    private double currentSpeed;
    private double distanceTravelled;
    private long timeElapsed;

    private List<LatLng> runCoordinates;

    @Override
    public double getCurrentAccuracy() {
        return currentAccuracy;
    }

    @Override
    public void setCurrentAccuracy(double currentAccuracy) {
        this.currentAccuracy = currentAccuracy;
    }

    @Override
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    @Override
    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    @Override
    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    @Override
    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    @Override
    public List<LatLng> getRunCoordinates() {
        return runCoordinates;
    }

    @Override
    public void setRunCoordinates(List<LatLng> runCoordinates) {
        this.runCoordinates = runCoordinates;
    }

    @Override
    public long getTimeElapsed() {
        return timeElapsed;
    }

    @Override
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
