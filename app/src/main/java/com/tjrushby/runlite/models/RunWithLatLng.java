package com.tjrushby.runlite.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.tjrushby.runlite.contracts.RunContract;

import java.util.List;

public class RunWithLatLng {
    @Embedded
    public Run run;

    @Relation(parentColumn = "id", entityColumn = "runId")
    public List<RunLatLng> runLatLngs;
}
