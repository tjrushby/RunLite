package com.tjrushby.runlite.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.tjrushby.runlite.contracts.RunContract;

import java.util.Date;

@Entity(tableName = "runs")
public class Run {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private double currentAccuracy;
    private double averagePace;
    private double distanceTravelled;
    private int timeElapsed;
    private Date dateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getCurrentAccuracy() {
        return currentAccuracy;
    }

    public void setCurrentAccuracy(double currentAccuracy) {
        this.currentAccuracy = currentAccuracy;
    }

    public double getAveragePace() {
        return averagePace;
    }

    public void setAveragePace(double averagePace) {
        this.averagePace = averagePace;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
