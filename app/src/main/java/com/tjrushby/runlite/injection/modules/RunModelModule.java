package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.models.Run;
import com.tjrushby.runlite.models.RunLatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RunModelModule {
    @Provides
    @Singleton
    RunContract.Model provideModel() {
        RunContract.Model runModel = new Run();
        runModel.setDateTime(new Date());

        return runModel;
    }

    @Provides
    @Singleton
    List<RunLatLng> providesRunLatLngs() {
        return new ArrayList<>();
    }
}
