package com.tjrushby.runlite.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.injection.modules.AudioCueServiceModule;
import com.tjrushby.runlite.services.factories.AudioCueFocusRequestBuilder;
import com.tjrushby.runlite.services.factories.AudioCueFocusRequestBuilderFactory;
import com.tjrushby.runlite.services.listeners.AudioCueProgressListener;

import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

public class AudioCueService extends Service
        implements AudioManager.OnAudioFocusChangeListener, TextToSpeech.OnInitListener {
    @Inject
    protected AudioCueFocusRequestBuilderFactory factory;
    @Inject
    protected AudioCueProgressListener progressListener;
    @Inject
    protected AudioManager audioManager;
    @Inject
    protected TextToSpeech tts;

    private boolean isInit;
    private int streamMaxVolume;
    private int userStreamVolume;

    private AudioCueFocusRequestBuilder requestBuilder;
    private AudioFocusRequest focusRequest;
    private String message;

    @Override
    public void onCreate() {
        super.onCreate();

        App.getAppComponent()
                .plus(new AudioCueServiceModule(this))
                .inject(this);

        requestBuilder = factory.create(this);
        streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        userStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.hasExtra("message")) {
            // get tts text from extras
            message = intent.getStringExtra("message");
        } else {
            message = "";
        }

        if(isInit) {
            // only perform audio cue if tts has been initialised
            performAudioCue(message);
        }

        return AudioCueService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(tts != null) {
            while (tts.isSpeaking()) {
                // do nothing, wait for tts to finish
            }

            // shutdown tts system now that it has finished talking
            tts.shutdown();
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // regained audio focus
                // manually set audio volume on api level < 26
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                    // set volume to what the user had initially set if it has been lowered
                    // (i.e. for ducking)
                    if(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != userStreamVolume) {
                        audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC, userStreamVolume, 0);
                    }
                }

                Timber.d("AUDIOFOCUS_GAIN");

                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // lost focus for a long period of time, stop tts if it is speaking
                if(tts != null && tts.isSpeaking()) {
                    tts.stop();
                }

                Timber.d("AUDIOFOCUS_LOSS");

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // lost focus for a short period of time, stop tts if it is speaking
                if(tts != null && tts.isSpeaking()) {
                    tts.stop();
                }

                Timber.d("AUDIOFOCUS_LOSS_TRANSIENT");

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // another stream has focus but tts is able to continue at a lower volume

                // save userStreamVolume (it may have been changed since onCreate) so that it can
                // be restored upon focus being regained
                userStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                // manually set audio ducking volume for api level < 26
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    // set volume to 20%
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC, streamMaxVolume / 5, 0);
                }

                Timber.d("AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");

                break;
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());

            if(result != TextToSpeech.LANG_MISSING_DATA
                    && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                // tts successfully initialised
                isInit = true;

                // set progress listener in order to abandon focus when done speaking
                tts.setOnUtteranceProgressListener(progressListener);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // build focusRequest for api level >= 26
                    focusRequest = requestBuilder.buildAudioCueFocusRequest();
                }

                performAudioCue(message);
            }
        } else if(status == TextToSpeech.ERROR) {
            // tts failed to initialise, notify the user
            Toast.makeText(
                    this,
                    "Audio Cue Service failed to initialise",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void performAudioCue(String message) {
        if(tts != null) {
            // first request audio focus
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // request audio focus for api level < 26
                int result =
                        audioManager.requestAudioFocus(
                                this,
                                AudioManager.STREAM_MUSIC,
                                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                        );

                if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // save userStreamVolume (it may have been changed since onCreate) so that it can
                    // be restored upon completion
                    userStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                    // gained audio focus, speak
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        // speak api level < 21
                        tts.speak(message, TextToSpeech.QUEUE_ADD, null);
                    } else {
                        // speak api level >= 21
                        tts.speak(
                                message,
                                TextToSpeech.QUEUE_ADD,
                                null,
                                "audioCueService"
                        );
                    }
                }
            } else {
                // request audio focus for api level >= 26
                int result = audioManager.requestAudioFocus(focusRequest);

                if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // gained audio focus, speak
                    tts.speak(
                            message,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            "audioCueService"
                    );
                }
            }
        }
    }

    // abandons audio focus
    public void abandonFocus() {
        // tts is done speaking, abandon audio focus
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // abandon audio focus for sdk level < 26 and restore audio volume if ducked
            getAudioManager().abandonAudioFocus(this);
            restoreVolume();
        } else {
            // abandon audio focus for sdk level >= 26
            getAudioManager()
                    .abandonAudioFocusRequest(focusRequest);
        }
    }

    // restores volume to user defined volume after ducking on api < 26
    public void restoreVolume() {
        if(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != userStreamVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, userStreamVolume, 0);
        }
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }
}
