package com.tjrushby.runlite.injection.components;


import com.tjrushby.runlite.App;
import com.tjrushby.runlite.injection.modules.AppModule;
import com.tjrushby.runlite.injection.modules.DetailsActivityModule;
import com.tjrushby.runlite.injection.modules.HandlerModule;
import com.tjrushby.runlite.injection.modules.MainActivityModule;
import com.tjrushby.runlite.injection.modules.RunActivityModule;
import com.tjrushby.runlite.injection.modules.RunActivityContextModule;
import com.tjrushby.runlite.injection.modules.RunRepositoryModule;
import com.tjrushby.runlite.injection.modules.StringFormatterModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, HandlerModule.class, RunRepositoryModule.class, StringFormatterModule.class})
public interface AppComponent {
    void inject(App app);
    DetailsActivityComponent plus(DetailsActivityModule detailsActivityModule);
    MainActivityComponent plus(MainActivityModule mainActivityModule);
    RunActivityComponent plus(RunActivityModule activityModule, RunActivityContextModule contextModule);
}
