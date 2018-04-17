package com.tjrushby.runlite.injection.components;

import com.tjrushby.runlite.injection.modules.RunPreferencesFragmentModule;
import com.tjrushby.runlite.injection.scopes.RunPreferencesFragmentScope;
import com.tjrushby.runlite.views.RunPreferencesFragment;

import dagger.Subcomponent;

@RunPreferencesFragmentScope
@Subcomponent(modules = {RunPreferencesFragmentModule.class})
public interface RunPreferencesFragmentComponent {
    void inject(RunPreferencesFragment fragment);
}
