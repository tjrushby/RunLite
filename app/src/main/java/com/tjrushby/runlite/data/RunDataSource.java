package com.tjrushby.runlite.data;

import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.models.RunWithLatLng;

import java.util.List;

public interface RunDataSource {

    interface LoadRunsCallback {
        void onRunsLoaded(List<RunWithLatLng> runs);

        void onDataNotAvailable();
    }

    interface GetRunCallback {
        void onRunLoaded(RunWithLatLng run);

        void onDataNotAvailable();
    }

    interface SaveRunCallback {
        void onRunSaved(Long runId);
    }

    interface DeleteRunCallback {
        void onRunDeleted();
    }

    void getRuns(LoadRunsCallback callback);

    void getRunById(Long runId, GetRunCallback callback);

    void saveRun(Run run, List<RunLatLng> runLatLngList, SaveRunCallback callback);

    void updateRun(RunWithLatLng run);

    void deleteRun(Run run, DeleteRunCallback callback);
}
