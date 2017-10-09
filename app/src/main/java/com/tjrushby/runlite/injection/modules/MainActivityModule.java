package com.tjrushby.runlite.injection.modules;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import com.tjrushby.runlite.adapters.RunModelAdapter;
import com.tjrushby.runlite.adapters.viewholders.RunModelViewHolderFactory;
import com.tjrushby.runlite.contracts.MainContract;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.scopes.MainActivityScope;
import com.tjrushby.runlite.presenters.ListPresenter;
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
    Intent providesIntent() {
        return new Intent();
    }

    @Provides
    @MainActivityScope
    MainContract.ListPresenter providesListPresenter(List<RunContract.Model> runsList,
                                                     StringFormatter formatter) {
        return new ListPresenter(runsList, formatter);
    }

    @Provides
    LinearLayoutManager providesLinearLayoutManager(MainContract.Activity activity) {
        return new LinearLayoutManager(activity.getContext());
    }

    @Provides
    RunModelAdapter providesRunModelAdapter(MainContract.ListPresenter listPresenter,
                                            RunModelViewHolderFactory factory) {
        return new RunModelAdapter(listPresenter, factory);
    }

    @Provides
    @MainActivityScope
    List<RunContract.Model> providesRunList() {
        return new ArrayList<>();
    }

    @Provides
    RunModelViewHolderFactory providesRunModelViewHolderFactory() {
        return new RunModelViewHolderFactory();
    }
}
