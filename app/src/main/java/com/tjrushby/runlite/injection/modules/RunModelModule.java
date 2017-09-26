package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.models.RunModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RunModelModule {
    @Provides
    @Singleton
    RunContract.Model provideModel() {
        return new RunModel();
    }
}
