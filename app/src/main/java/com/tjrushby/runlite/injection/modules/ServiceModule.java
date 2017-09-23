package com.tjrushby.runlite.injection.modules;


import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;

import dagger.Module;
import dagger.Provides;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.services.RunService;

@Module
public class ServiceModule {
    @Provides
    @RunningActivityScope
    RunContract.Service provideFusedLocationService(Context context,
                                                    RunContract.Model model,
                                                    FusedLocationProviderClient locationClient,
                                                    LocationRequest locationRequest,
                                                    LocationSettingsRequest.Builder builder) {

        return new RunService(context, model, locationClient, locationRequest, builder);
    }

    @Provides
    FusedLocationProviderClient providesFusedLocationProviderClient(Context context) {
        return new FusedLocationProviderClient(context);
    }

    @Provides
    LocationRequest providesLocationRequest() {
        return new LocationRequest();
    }

    @Provides
    LocationSettingsRequest.Builder providesBuilder() {
        return new LocationSettingsRequest.Builder();
    }
}
