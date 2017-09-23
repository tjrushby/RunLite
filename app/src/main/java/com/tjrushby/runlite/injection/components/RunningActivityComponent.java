package com.tjrushby.runlite.injection.components;

import dagger.Component;
import com.tjrushby.runlite.views.RunActivity;
import com.tjrushby.runlite.injection.modules.ActivityModule;
import com.tjrushby.runlite.injection.modules.ContextModule;
import com.tjrushby.runlite.injection.modules.ModelModule;
import com.tjrushby.runlite.injection.modules.PresenterModule;
import com.tjrushby.runlite.injection.modules.ServiceModule;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.presenters.RunPresenter;
import com.tjrushby.runlite.services.RunService;

@RunningActivityScope
@Component(modules = {ContextModule.class, ActivityModule.class, PresenterModule.class, ModelModule.class, ServiceModule.class})
public interface RunningActivityComponent {
    void inject(RunActivity activity);
    void inject(RunPresenter presenter);
    void inject(RunService service);
}
