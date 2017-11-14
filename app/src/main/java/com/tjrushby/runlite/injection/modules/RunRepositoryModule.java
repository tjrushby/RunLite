package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.data.AppDatabase;
import com.tjrushby.runlite.data.RunDataSource;
import com.tjrushby.runlite.data.RunLocalDataSource;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.data.dao.RunDAO;
import com.tjrushby.runlite.data.dao.RunDAO_Impl;
import com.tjrushby.runlite.data.dao.RunLatLngDAO;
import com.tjrushby.runlite.data.dao.RunLatLngDAO_Impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RunRepositoryModule {
    @Provides
    @Singleton
    RunRepository providesRunRepository(RunDataSource runDataSource) {
        return new RunRepository(runDataSource);
    }

    @Provides
    @Singleton
    RunDataSource providesRunLocalDataSource(Executor executor, RunDAO runDao, RunLatLngDAO runLatLngDAO) {
        return new RunLocalDataSource(executor, runDao, runLatLngDAO);
    }

    @Provides
    Executor providesExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    RunDAO providesRunDao(AppDatabase appDatabase) {
        return new RunDAO_Impl(appDatabase);
    }

    @Provides
    @Singleton
    RunLatLngDAO providesRunLatLngDao(AppDatabase appDatabase) {
        return new RunLatLngDAO_Impl(appDatabase);
    }
}
