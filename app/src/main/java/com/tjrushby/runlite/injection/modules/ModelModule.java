package com.tjrushby.runlite.injection.modules;

import dagger.Module;
import dagger.Provides;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.models.RunModel;

@Module
public class ModelModule {
    @Provides
    @RunningActivityScope
    RunContract.Model provideModel() {
        return new RunModel();
    }
}
