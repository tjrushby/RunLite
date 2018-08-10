package com.tjrushby.runlite.injection.modules;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsActivityModule {
    private DetailsContract.Activity activity;
    private Context context;

    public DetailsActivityModule(DetailsContract.Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Provides
    @DetailsActivityScope
    DetailsContract.Activity providesDetailsActivity() {
        return activity;
    }

    @Provides
    AlertDialog.Builder providesAlertDialogBuilder(@Named("details_context") Context context) {
        return new AlertDialog.Builder(context);
    }

    @Provides
    @Named("details_context")
    Context providesContext() {
        return context;
    }

    @Provides
    IconGenerator providesIconGenerator() {
        return new IconGenerator(context);
    }

    @Provides
    Intent providesIntent() {
        return new Intent();
    }

    @Provides
    PolylineOptions providesPolylineOptions() {
        return new PolylineOptions();
    }

    @Provides
    LatLngBounds.Builder providesLatLngBoundsBuilder() {
        return new LatLngBounds.Builder();
    }

    @Provides
    MarkerOptions providesMarkerOptions() {
        return new MarkerOptions();
    }
}
