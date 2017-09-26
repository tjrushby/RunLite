package com.tjrushby.runlite.injection.components;

import dagger.Subcomponent;

import com.tjrushby.runlite.views.RunActivity;
import com.tjrushby.runlite.injection.modules.RunActivityModule;
import com.tjrushby.runlite.injection.modules.RunActivityContextModule;
import com.tjrushby.runlite.injection.modules.RunPresenterModule;
import com.tjrushby.runlite.injection.modules.RunServiceModule;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.presenters.RunPresenter;
import com.tjrushby.runlite.services.RunService;

@RunningActivityScope
@Subcomponent(modules = {RunActivityContextModule.class, RunActivityModule.class, RunPresenterModule.class, RunServiceModule.class})
public interface RunActivityComponent {
    void inject(RunActivity activity);
    void inject(RunPresenter presenter);
    void inject(RunService service);
}
