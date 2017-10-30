package com.tjrushby.runlite.data;

import android.support.annotation.NonNull;

import com.tjrushby.runlite.data.dao.RunDAO;
import com.tjrushby.runlite.models.RunWithLatLng;

import java.util.List;

public class RunDataSource {
    private RunDAO runDao;

    public RunDataSource(@NonNull RunDAO runDao) {
        this.runDao = runDao;
    }

    public void getTasks() {
        Runnable runnable = new Runnable() {
            final List<RunWithLatLng> runs = runDao.loadRunWithLatLng();

            @Override
            public void run() {

            }
        };
    }
}
