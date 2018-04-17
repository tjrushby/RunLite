package com.tjrushby.runlite.injection.components;

import com.tjrushby.runlite.injection.modules.RunPreferencesActivityModule;
import com.tjrushby.runlite.injection.scopes.RunPreferencesScope;
import com.tjrushby.runlite.views.RunPreferencesActivity;

import dagger.Subcomponent;

@RunPreferencesScope
@Subcomponent(modules = {RunPreferencesActivityModule.class})
public interface RunPreferencesActivityComponent {
    void inject(RunPreferencesActivity activity);
}
