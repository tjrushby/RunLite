package com.tjrushby.runlite.data;

import com.tjrushby.runlite.data.dao.RunDAO;
import com.tjrushby.runlite.data.dao.RunLatLngDAO;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.models.RunWithLatLng;

import java.util.List;
import java.util.concurrent.Executor;

import timber.log.Timber;

public class RunLocalDataSource implements RunDataSource {
    private Executor executor;
    private RunDAO runDao;
    private RunLatLngDAO runLatLngDao;

    public RunLocalDataSource(Executor executor, RunDAO runDao, RunLatLngDAO runLatLngDao) {
        this.executor = executor;
        this.runDao = runDao;
        this.runLatLngDao = runLatLngDao;
    }

    @Override
    public void getRuns(LoadRunsCallback callback) {
        Runnable runnable = () -> {
            final List<RunWithLatLng> runs = runDao.loadRunWithLatLng();

            if(runs.isEmpty()) {
                // nothing in the database table
                callback.onDataNotAvailable();
            } else {
                // retrieved data from the database
                callback.onRunsLoaded(runs);
            }
        };

        executor.execute(runnable);
    }

    @Override
    public void getRunById(Long runId, GetRunCallback callback) {
        Runnable runnable = () -> {
            final RunWithLatLng run = runDao.findById(Long.toString(runId));

            if(run != null) {
                // retrieved run with runId from database
                callback.onRunLoaded(run);
            } else {
                // no run with runId in the database
                callback.onDataNotAvailable();
            }
        };

        executor.execute(runnable);
    }

    @Override
    public void saveRun(Run run, List<RunLatLng> runLatLngList, SaveRunCallback callback) {
        Runnable runnable = () -> {
            long runId = runDao.insert(run);

            for(RunLatLng runLatLng : runLatLngList) {
                runLatLng.setRunId(runId);
            }

            runLatLngDao.insertAll(runLatLngList);
            callback.onRunSaved(runId);
        };

        executor.execute(runnable);
    }

    @Override
    public void updateRun(RunWithLatLng runWithLatLng) {
        Runnable runnable = () -> runDao.update(runWithLatLng.run);
        executor.execute(runnable);
    }

    @Override
    public void deleteRun(Run run, DeleteRunCallback callback) {
        Runnable runnable = () -> {
            runDao.delete(run);
            callback.onRunDeleted();
        };

        executor.execute(runnable);
    }
}
