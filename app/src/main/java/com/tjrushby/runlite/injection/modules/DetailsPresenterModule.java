package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.presenters.DetailsPresenter;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsPresenterModule {
    @Provides
    @DetailsActivityScope
    DetailsContract.Presenter providesDetailsPresenter(DetailsContract.Activity view,
                                                       List<RunLatLng> mapMarkerCoordinates,
                                                       RunRepository runRepository,
                                                       RunWithLatLng run,
                                                       StringFormatter formatter) {
        return new DetailsPresenter(view, mapMarkerCoordinates, runRepository, run, formatter);
    }

    @Provides
    List<RunLatLng> providesArrayList() {
        return new ArrayList<>();
    }

    @Provides
    @DetailsActivityScope
    RunWithLatLng providesRunWithLatLng() {
        return new RunWithLatLng();
    }
}
