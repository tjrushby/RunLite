package com.tjrushby.runlite.injection.components;

import com.tjrushby.runlite.injection.modules.RunPreferencesActivityModule;
import com.tjrushby.runlite.injection.scopes.RunPreferencesScope;
import com.tjrushby.runlite.views.RunPreferencesActivity;
import com.tjrushby.runlite.views.RunPreferencesFragment;

import dagger.Subcomponent;

@RunPreferencesScope
@Subcomponent(modules = {RunPreferencesActivityModule.class})
public interface RunPreferencesComponent {
    void inject(RunPreferencesActivity activity);
}
