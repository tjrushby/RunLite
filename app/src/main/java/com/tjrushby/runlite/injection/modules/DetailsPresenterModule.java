package com.tjrushby.runlite.injection.modules;


import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;
import com.tjrushby.runlite.presenters.DetailsPresenter;
import com.tjrushby.runlite.util.StringFormatter;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsPresenterModule {
    @Provides
    @DetailsActivityScope
    DetailsContract.Presenter providesDetailsPresenter(DetailsContract.Activity view,
                                                       RunContract.Model model,
                                                       StringFormatter formatter) {
        return new DetailsPresenter(view, model, formatter);
    }
}
