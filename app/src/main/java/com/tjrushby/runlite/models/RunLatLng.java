package com.tjrushby.runlite.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

@Entity(
    tableName = "run_latlng",
    foreignKeys = @ForeignKey(
        entity = Run.class,
        parentColumns = "id",
        childColumns = "runId",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
    indices = {
            @Index(value = {"runId"})
    }
)
public class RunLatLng {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long runId;
    private double latitude;
    private double longitude;
    private double distanceInRun;

    @Ignore
    public RunLatLng(double latitude, double longitude, double distanceInRun) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceInRun = distanceInRun;
    }

    public RunLatLng(long runId, double latitude, double longitude, double distanceInRun) {
        this.runId = runId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceInRun = distanceInRun;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getDistanceInRun() {
        return distanceInRun;
    }

    public void setDistanceInRun(double distanceInRun) {
        this.distanceInRun = distanceInRun;
    }
}
