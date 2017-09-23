package com.tjrushby.runlite.injection.modules;

import dagger.Module;
import dagger.Provides;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.presenters.RunPresenter;

@Module
public class PresenterModule {
    @Provides
    @RunningActivityScope
    RunContract.Presenter providePresenter(RunContract.Activity activity,
                                           RunContract.Model model,
                                           RunContract.Service service) {
        return new RunPresenter(activity, model, service);
    }
}
