package com.tjrushby.runlite.services.listeners;

import android.speech.tts.UtteranceProgressListener;

import com.tjrushby.runlite.services.AudioCueService;

import timber.log.Timber;

public class AudioCueProgressListener extends UtteranceProgressListener {
    private AudioCueService audioCueService;

    public AudioCueProgressListener(AudioCueService audioCueService) {
        this.audioCueService = audioCueService;
    }

    @Override
    public void onStart(String s) {
    }

    @Override
    public void onDone(String s) {
        // tts is done speaking, abandon audio focus
        audioCueService.abandonFocus();
    }

    @Override
    public void onError(String s) {
    }
}
