package com.tjrushby.runlite.injection.components;


import com.tjrushby.runlite.injection.modules.DetailsPresenterModule;
import com.tjrushby.runlite.injection.scopes.DetailsActivityScope;
import com.tjrushby.runlite.views.DetailsActivity;

import dagger.Subcomponent;

@DetailsActivityScope
@Subcomponent(modules = DetailsPresenterModule.class)
public interface DetailsActivityComponent {
    void inject(DetailsActivity detailsActivity);
}
