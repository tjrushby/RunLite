package com.tjrushby.runlite.injection.modules;


import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.injection.scopes.MainActivityScope;
import com.tjrushby.runlite.presenters.MainPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainPresenterModule {
    @Provides
    @MainActivityScope
    MainContract.Presenter providesMainPresenter(MainContract.Activity activity) {
        return new MainPresenter(activity);
    }
}
