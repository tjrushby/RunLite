package com.tjrushby.runlite.injection.modules;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;

import com.tjrushby.runlite.injection.scopes.RunningActivityScope;
import com.tjrushby.runlite.services.AudioCueService;
import com.tjrushby.runlite.services.factories.AudioCueFocusRequestBuilderFactory;
import com.tjrushby.runlite.services.listeners.AudioCueProgressListener;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class AudioCueServiceModule {
    private AudioCueService audioCueService;

    public AudioCueServiceModule(AudioCueService audioCueService) {
        this.audioCueService = audioCueService;
    }

    @Provides
    @RunningActivityScope
    AudioCueFocusRequestBuilderFactory providesAudioCueFocusRequestBuilderFactory() {
        return new AudioCueFocusRequestBuilderFactory();
    }

    @Provides
    @RunningActivityScope
    AudioCueProgressListener providesAudioCueProgressListener() {
        return new AudioCueProgressListener(audioCueService);
    }

    @Provides
    @RunningActivityScope
    AudioManager providesAudioManager(@Named("app_context") Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @RunningActivityScope
    TextToSpeech providesTextToSpeech(@Named("app_context") Context context) {
        return new TextToSpeech(context, audioCueService);
    }
}
