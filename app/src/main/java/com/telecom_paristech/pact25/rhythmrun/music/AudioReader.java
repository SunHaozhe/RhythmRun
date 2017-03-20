package com.telecom_paristech.pact25.rhythmrun.music;

import android.media.AudioTrack;
import android.os.Build;
import android.support.annotation.RequiresApi;

import static android.media.AudioFormat.CHANNEL_OUT_MONO;
import static android.media.AudioFormat.ENCODING_PCM_FLOAT;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioTrack.MODE_STREAM;

/**
 * Created by Raphael Attali on 06/03/2017.
 * <p>
 * Project RythmRun
 */

public class AudioReader {
    int bufferLength;
    AudioTrack ar;
    boolean isPlaying;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AudioReader (int bufferLength) {
        this.bufferLength = bufferLength;
        ar = new AudioTrack(STREAM_MUSIC, 44100, CHANNEL_OUT_MONO, ENCODING_PCM_FLOAT, bufferLength, MODE_STREAM);
        ar.setVolume(1.0f);
        isPlaying = false;
    }

    public void play() {
        if (!isPlaying) {
            ar.play();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void write(float[] buffer) {
        ar.write(buffer, 0, bufferLength, AudioTrack.WRITE_BLOCKING);
    }
}
