package com.tjrushby.runlite.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunWithLatLng;

import java.util.List;

@Dao
public interface RunDAO {
    @Query("SELECT * FROM runs WHERE id LIKE :id")
    RunWithLatLng findById(String id);

    @Query("SELECT * FROM runs ORDER BY dateTime DESC")
    List<RunWithLatLng> loadRunWithLatLng();

    @Insert
    void insertAll(List<Run> runs);

    @Insert
    long insert(Run run);

    @Update
    void update(Run run);

    @Delete
    void delete(Run run);
}
