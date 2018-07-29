package com.tjrushby.runlite.injection.components;

import com.tjrushby.runlite.injection.modules.EditActivityModule;
import com.tjrushby.runlite.injection.scopes.EditActivityScope;
import com.tjrushby.runlite.views.EditActivity;

import dagger.Subcomponent;

@EditActivityScope
@Subcomponent(modules = EditActivityModule.class)
public interface EditActivityComponent {
    void inject(EditActivity view);
}
