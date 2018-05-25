package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.presenters.RunPresenter;
import com.tjrushby.runlite.util.StringFormatter;

import java.util.Date;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class RunPresenterModule {
    @Provides
    @RunningActivityScope
    RunContract.Presenter providePresenter(RunContract.Activity activity,
                                           RunContract.Model model,
                                           RunContract.RunService runService,
                                           RunRepository runRepository,
                                           List<RunLatLng> runLatLngList,
                                           StringFormatter formatter) {
        return new RunPresenter(activity, model, runService, runRepository, runLatLngList, formatter);
    }

    @Provides
    @RunningActivityScope
    RunContract.Model provideModel() {
        RunContract.Model runModel = new Run();
        runModel.setDateTime(new Date());

        return runModel;
    }
}
