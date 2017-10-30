package com.tjrushby.runlite.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;

import com.tjrushby.runlite.contracts.RunContract;

import java.util.Date;
import java.util.List;

@Entity(tableName = "runs")
public class Run implements RunContract.Model {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Date dateTime;
    private double currentAccuracy;
    private double currentSpeed;
    private double distanceTravelled;
    private long timeElapsed;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

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
    public long getTimeElapsed() {
        return timeElapsed;
    }

    @Override
    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}