package com.tjrushby.runlite.injection.modules;


import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailsActivityModule {
    private DetailsContract.Activity activity;

    public DetailsActivityModule(DetailsContract.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @DetailsActivityScope
    DetailsContract.Activity providesDetailsActivity() {
        return activity;
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
