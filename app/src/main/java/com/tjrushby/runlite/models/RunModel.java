package com.tjrushby.runlite.models;

import com.tjrushby.runlite.contracts.RunContract;

public class RunModel implements RunContract.Model {
    private double currentAccuracy;
    private double currentSpeed;
    private double distanceTravelled;

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
}
