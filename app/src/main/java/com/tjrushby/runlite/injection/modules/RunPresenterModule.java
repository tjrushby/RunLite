package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.presenters.RunPresenter;
import com.tjrushby.runlite.util.StringFormatter;

import dagger.Module;
import dagger.Provides;

@Module
public class RunPresenterModule {
    @Provides
    @RunningActivityScope
    RunContract.Presenter providePresenter(RunContract.Activity activity,
                                           RunContract.Model model,
                                           RunContract.Service service,
                                           StringFormatter formatter) {
        return new RunPresenter(activity, model, service, formatter);
    }
}
