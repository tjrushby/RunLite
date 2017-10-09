package com.tjrushby.runlite.injection.components;


import com.tjrushby.runlite.injection.modules.MainActivityModule;
import com.tjrushby.runlite.injection.modules.MainPresenterModule;
import com.tjrushby.runlite.injection.scopes.MainActivityScope;
import com.tjrushby.runlite.views.MainActivity;

import dagger.Subcomponent;

@MainActivityScope
@Subcomponent(modules = {MainActivityModule.class, MainPresenterModule.class})
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
}
