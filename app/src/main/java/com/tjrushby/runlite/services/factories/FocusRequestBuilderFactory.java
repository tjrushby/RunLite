package com.tjrushby.runlite.services.factories;

import com.tjrushby.runlite.services.AudioCueService;

public interface FocusRequestBuilderFactory {
    AudioCueFocusRequestBuilder createACFRBuilder(AudioCueService audioCueService);
}
