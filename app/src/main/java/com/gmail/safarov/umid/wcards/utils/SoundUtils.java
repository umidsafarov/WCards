package com.gmail.safarov.umid.wcards.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import java.io.IOException;

public class SoundUtils {

    private static SoundUtils mInstance;

    public static SoundUtils getInstance() {
        if (mInstance == null)
            mInstance = new SoundUtils();
        return mInstance;
    }

    private MediaPlayer mediaPlayer;

    private SoundUtils() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void playSound(@NonNull String path) {
        try {
            //interrupt previously playing sound
            mediaPlayer.reset();
            //play new sound
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
        }
    }

}