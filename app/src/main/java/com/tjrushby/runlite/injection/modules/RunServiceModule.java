package com.tjrushby.runlite.injection.modules;


import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.models.RunLatLng;
import com.tjrushby.runlite.services.RunService;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class RunServiceModule {
    @Provides
    @RunningActivityScope
    RunContract.Service provideFusedLocationService(@Named("activity_context") Context context,
                                                    DecimalFormat df,
                                                    RunContract.Model model,
                                                    List<RunLatLng> runLatLngs,
                                                    FusedLocationProviderClient locationClient,
                                                    LocationRequest locationRequest,
                                                    LocationSettingsRequest.Builder builder) {

        return new RunService(context, df, model, runLatLngs, locationClient, locationRequest, builder);
    }

    @Provides
    @RunningActivityScope
    List<RunLatLng> providesRunLatLngList() {
        return new ArrayList<>();
    }

    @Provides
    FusedLocationProviderClient providesFusedLocationProviderClient(@Named("activity_context") Context context) {
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

    @Provides
    DecimalFormat providesDecimalFormat() {
        return new DecimalFormat("#0.00");
    }
}
