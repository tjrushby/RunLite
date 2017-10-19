package com.tjrushby.runlite.injection.modules;


import android.os.Handler;

import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.presenters.DetailsPresenter;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsPresenterModule {
    @Provides
    @DetailsActivityScope
    DetailsContract.Presenter providesDetailsPresenter(DetailsContract.Activity view,
                                                       RunContract.Model model,
                                                       List<RunLatLng> runLatLngs,
                                                       StringFormatter formatter) {
        return new DetailsPresenter(view, model, runLatLngs, formatter);
    }
}
