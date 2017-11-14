package com.tjrushby.runlite.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.tjrushby.runlite.models.RunLatLng;

import java.util.List;

@Dao
public interface RunLatLngDAO {
    @Query("SELECT * FROM run_latlng")
    List<RunLatLng> getAll();

    @Query("SELECT * FROM run_latlng WHERE runId LIKE :runId")
    List<RunLatLng> findById(String runId);

    @Insert
    void insertAll(List<RunLatLng> runs);

    @Update
    void update(RunLatLng runLatLng);

    @Update
    void updateAll(List<RunLatLng> runLatLngList);

    @Delete
    void delete(RunLatLng runLatLng);
}
