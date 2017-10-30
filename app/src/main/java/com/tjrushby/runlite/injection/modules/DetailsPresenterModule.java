package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.presenters.DetailsPresenter;
import com.tjrushby.runlite.util.StringFormatter;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsPresenterModule {
    @Provides
    @DetailsActivityScope
    DetailsContract.Presenter providesDetailsPresenter(DetailsContract.Activity view,
                                                       RunRepository runRepository,
                                                       RunWithLatLng run,
                                                       StringFormatter formatter) {
        return new DetailsPresenter(view, runRepository, run, formatter);
    }

    @Provides
    @DetailsActivityScope
    RunWithLatLng providesRunWithLatLng() {
        return new RunWithLatLng();
    }
}
