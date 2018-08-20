package com.tjrushby.runlite.services.factories;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;

import com.google.auto.factory.AutoFactory;
import com.tjrushby.runlite.services.AudioCueService;

@AutoFactory(implementing = FocusRequestBuilderFactory.class)
public class AudioCueFocusRequestBuilder implements FocusRequestBuilder {
    private AudioAttributes audioAttributes;
    private AudioCueService audioCueService;

    public AudioCueFocusRequestBuilder(AudioCueService audioCueService) {
        this.audioCueService = audioCueService;

        buildAudioAttributes();
    }

    @Override
    @TargetApi(26)
    public AudioFocusRequest buildAudioCueFocusRequest() {
        return new android.media.AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener(audioCueService)
                .setWillPauseWhenDucked(true)
                .build();
    }

    @TargetApi(26)
    private void buildAudioAttributes() {
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
    }
}
