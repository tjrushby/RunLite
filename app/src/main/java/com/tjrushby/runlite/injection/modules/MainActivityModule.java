package com.tjrushby.runlite.injection.modules;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;

import com.tjrushby.runlite.R;
import com.tjrushby.runlite.adapters.RunModelAdapter;
import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolderFactory;
import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.data.RunRepository;
import com.tjrushby.runlite.injection.scopes.MainActivityScope;
import com.tjrushby.runlite.models.RunWithLatLng;
import com.tjrushby.runlite.util.StringFormatter;
import com.tjrushby.runlite.views.MainActivity;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
    private MainContract.Activity activity;

    public MainActivityModule(MainActivity activity) {
        this.activity = activity;
    }

    @Provides
    @MainActivityScope
    MainContract.Activity providesMainActivity() {
        return activity;
    }

    @Provides
    ColorDrawable providesColorDrawable() {
        if(activity.getDarkThemeEnabled()) {
            return new ColorDrawable(
                    ContextCompat.getColor(activity.getContext(), R.color.deleteItemBackground));
        } else {
            return new ColorDrawable(
                    ContextCompat.getColor(activity.getContext(), R.color.colorGPSBad));
        }
    }

    @Provides
    Intent providesIntent() {
        return new Intent();
    }

    @Provides
    LinearLayoutManager providesLinearLayoutManager(MainContract.Activity activity) {
        return new LinearLayoutManager(activity.getContext());
    }

    @Provides
    RunModelAdapter providesRunModelAdapter(List<RunWithLatLng> runsList,
                                            MainContract.Presenter presenter,
                                            RunModelViewHolderFactory factory,
                                            RunRepository runRepository,
                                            StringFormatter formatter) {
        return new RunModelAdapter(runsList, presenter, factory, runRepository, formatter);
    }

    @Provides
    RunModelViewHolderFactory providesRunModelViewHolderFactory() {
        return new RunModelViewHolderFactory();
    }

    @Provides
    List<RunWithLatLng> providesRunWithLatLngList() {
        return new ArrayList<>();
    }
}
