package com.tjrushby.runlite.data;

import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.models.RunWithLatLng;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RunRepository implements RunDataSource {
    private final RunDataSource runLocalDataSource;

    private Map<Long, RunWithLatLng> cachedRuns;

    public RunRepository(RunDataSource runLocalDataSource) {
        this.runLocalDataSource = runLocalDataSource;
    }

    @Override
    public void getRuns(LoadRunsCallback callback) {
        if(cachedRuns != null) {
            // we've already checked the database for runs
            if(cachedRuns.size() > 0) {
                // cache contains at least one run, callback runs loaded
                callback.onRunsLoaded(new ArrayList<>(cachedRuns.values()));
            } else {
                // cache contains no runs, callback no runs available
                callback.onDataNotAvailable();
            }
        } else {
            // need to retrieve runs from database
            runLocalDataSource.getRuns(new LoadRunsCallback() {
                @Override
                public void onRunsLoaded(List<RunWithLatLng> runs) {
                    updateCache(runs);
                    callback.onRunsLoaded(new ArrayList<>(runs));
                }

                @Override
                public void onDataNotAvailable() {
                    // no runs in database
                    updateCache(new ArrayList<RunWithLatLng>());
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void getRunById(Long runId, GetRunCallback callback) {
        RunWithLatLng cachedRun = getRunFromCacheById(runId);

        if(cachedRun != null) {
            // successfully retrieved run from cache
            callback.onRunLoaded(cachedRun);
        } else {
            // run not in the cache
            runLocalDataSource.getRunById(runId, new GetRunCallback() {
                @Override
                public void onRunLoaded(RunWithLatLng run) {
                    // retrieved run from database, add to cache
                    if(cachedRuns == null) {
                        cachedRuns = new LinkedHashMap<>();
                    }

                    cachedRuns.put(run.run.getId(), run);

                    callback.onRunLoaded(run);
                }

                @Override
                public void onDataNotAvailable() {
                    // no run matching runId in database
                    callback.onDataNotAvailable();
                }
            });
        }
    }

    @Override
    public void saveRun(Run run, List<RunLatLng> runLatLngList, SaveRunCallback callback) {
        // save to database
        runLocalDataSource.saveRun(run, runLatLngList, (runId) -> {
                    // add to cache
                    RunWithLatLng runWithLatLng = new RunWithLatLng();
                    runWithLatLng.run = run;
                    runWithLatLng.run.setId(runId);
                    runWithLatLng.runLatLngs = runLatLngList;
                    cachedRuns.put(runId, runWithLatLng);

                    callback.onRunSaved(runId);
                }
        );
    }

    @Override
    public void updateRun(RunWithLatLng runWithLatLng) {
        runLocalDataSource.updateRun(runWithLatLng);
    }

    @Override
    public void deleteRun(Run run) {
        // remove from database
        runLocalDataSource.deleteRun(run);

        // remove from cache
        cachedRuns.remove(run.getId());
    }

    private RunWithLatLng getRunFromCacheById(Long id) {
        if(cachedRuns == null || cachedRuns.isEmpty()) {
            // cache is empty or not instantiated, return null
            return null;
        } else {
            // return object from cache
            return cachedRuns.get(id);
        }
    }

    private void updateCache(List<RunWithLatLng> runs) {
        if(cachedRuns == null) {
            cachedRuns = new LinkedHashMap<>();
        }

        // clear the cache
        cachedRuns.clear();

        // add the new data to the cache
        for (RunWithLatLng run : runs) {
            cachedRuns.put(run.run.getId(), run);
        }
    }
}
