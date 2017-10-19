package com.tjrushby.runlite.injection.components;


import com.tjrushby.runlite.injection.modules.DetailsActivityModule;
import com.tjrushby.runlite.injection.modules.HandlerModule;
import com.tjrushby.runlite.injection.modules.MainActivityModule;
import com.tjrushby.runlite.injection.modules.RunActivityModule;
import com.tjrushby.runlite.injection.modules.RunActivityContextModule;
import com.tjrushby.runlite.injection.modules.RunModelModule;
import com.tjrushby.runlite.injection.modules.StringFormatterModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {HandlerModule.class, RunModelModule.class, StringFormatterModule.class})
public interface AppComponent {
    //void inject(App app);
    DetailsActivityComponent plus(DetailsActivityModule detailsActivityModule);
    MainActivityComponent plus(MainActivityModule mainActivityModule);
    RunActivityComponent plus(RunActivityModule activityModule, RunActivityContextModule contextModule);
}
