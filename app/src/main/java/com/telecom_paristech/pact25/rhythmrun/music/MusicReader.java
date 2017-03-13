package com.telecom_paristech.pact25.rhythmrun.music;

import android.media.AudioTrack;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;

import static android.media.AudioFormat.CHANNEL_OUT_MONO;
import static android.media.AudioFormat.ENCODING_PCM_FLOAT;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioTrack.MODE_STREAM;

/**
 * Created by lucas on 13/03/17.
 */

public class MusicReader {
    Thread thread;
    ArrayList<float[]> file;
    int bufferSize;
    ThreadRunnable threadRunnable;

    public MusicReader(int bufferSize) {
        this.bufferSize = bufferSize;
        file = new ArrayList<float[]>();
        threadRunnable = new ThreadRunnable(bufferSize, file);
        thread = new Thread(threadRunnable);
    }

    public int getNumberOfBuffers() {
        int r;
        synchronized (file) {
            r = file.size();
        }
        return r;
    }

    public void addBuffer(float[] buffer) {
        synchronized (file) {
            file.add(buffer);
            Log.i("lucas", "buffer added");
        }
    }

    public void play() {
        thread.start();
    }

    public void stop() {
        threadRunnable.stopReadingMusic();
    }

    public void stopAtTheEnd() {
        threadRunnable.stopAtTheEnd();
    }

    class ThreadRunnable implements Runnable {
        ArrayList<float[]> file;
        int bufferSize;
        float[] buffer;
        boolean pasFiniLaBoucle, pasFiniDeLire;

        public ThreadRunnable(int bufferSize, ArrayList<float[]> file) {
            this.bufferSize = bufferSize;
            this.file = file;
            pasFiniLaBoucle = true;
            pasFiniDeLire = true;
        }

        public void stopReadingMusic() {
            pasFiniLaBoucle = false;
        }

        public void stopAtTheEnd() {
            pasFiniDeLire = false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            Log.i("lucas", "bufferSize : " + String.valueOf(bufferSize));
            AudioTrack audioTrack = new AudioTrack(STREAM_MUSIC, 44100, CHANNEL_OUT_MONO, ENCODING_PCM_FLOAT, bufferSize, MODE_STREAM);
            audioTrack.setVolume(1.0f);
            Log.i("lucas", "on a write, on va play");
            boolean first = true;
            while(pasFiniLaBoucle && ((pasFiniDeLire) || (file.size() > 0))) {
                synchronized (file) {
                    if (file.size() > 0) {
                        buffer = file.get(0);
                        file.remove(0);
                        Log.i("lucas", "buffer chargé");
                    } else {
                        //Log.i("lucas", "Famine de buffer.");
                    }
                }
                if (buffer != null) {
                audioTrack.write(buffer, 0, bufferSize, AudioTrack.WRITE_BLOCKING);
                    if (first) {
                        first = false;
                        audioTrack.play();
                        Log.i("lucas", "audio track play");
                    }
                    //Log.i("lucas", "buffer write");
                }
            }
            Log.i("lucas", "audioTrack.stop");
            audioTrack.stop();
        }
    }
}
