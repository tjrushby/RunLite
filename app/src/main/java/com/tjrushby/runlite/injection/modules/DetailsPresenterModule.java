package com.tjrushby.runlite.injection.modules;


import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;
import com.tjrushby.runlite.presenters.DetailsPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsPresenterModule {
    @Provides
    @DetailsActivityScope
    DetailsContract.Presenter providesDetailsPresenter() {
        return new DetailsPresenter();
    }
}
