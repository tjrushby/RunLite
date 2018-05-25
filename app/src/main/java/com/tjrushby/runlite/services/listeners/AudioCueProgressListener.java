package com.tjrushby.runlite.services.listeners;

import android.os.Build;
import android.speech.tts.UtteranceProgressListener;

import com.tjrushby.runlite.services.AudioCueService;

public class AudioCueProgressListener extends UtteranceProgressListener {
    private AudioCueService audioCueService;

    public AudioCueProgressListener(AudioCueService audioCueService) {
        this.audioCueService = audioCueService;
    }

    @Override
    public void onStart(String s) {}

    @Override
    public void onDone(String s) {
        // tts is done speaking, abandon audio focus
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // abandon audio focus for sdk level < 26
            audioCueService.getAudioManager().abandonAudioFocus(audioCueService);
        } else {
            // abandon audio focus for sdk level >= 26
            audioCueService.getAudioManager()
                    .abandonAudioFocusRequest(audioCueService.getFocusRequest());
        }
    }

    @Override
    public void onError(String s) {}
}
