package com.tjrushby.runlite.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.tjrushby.runlite.data.dao.RunDAO;
import com.tjrushby.runlite.data.dao.RunLatLngDAO;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;

@Database(entities = {Run.class, RunLatLng.class}, version = 2)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RunDAO runDAO();
    public abstract RunLatLngDAO runLatLngDAO();
}
