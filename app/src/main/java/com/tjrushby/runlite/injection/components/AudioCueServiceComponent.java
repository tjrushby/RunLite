package com.tjrushby.runlite.injection.components;

import com.tjrushby.runlite.injection.modules.AudioCueServiceModule;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.services.AudioCueService;

import dagger.Subcomponent;

@RunningActivityScope
@Subcomponent(modules = {AudioCueServiceModule.class})
public interface AudioCueServiceComponent {
    void inject(AudioCueService audioCueService);
}
