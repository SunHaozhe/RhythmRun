package com.telecom_paristech.pact25.rhythmrun.music;

import android.annotation.TargetApi;
import android.media.AudioTrack;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.interfaces.music.ByteBufferPool;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.FloatArrayPool;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static android.media.AudioFormat.CHANNEL_OUT_MONO;
import static android.media.AudioFormat.ENCODING_PCM_FLOAT;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioTrack.MODE_STREAM;

/**
 * Permet de lire de la musique (format WAV)
 *
 *  Créé par Lucas, rectifications le 27/03 par Raphael
 */

public class MusicReader {
    Thread thread;
    ArrayList<float[]> floatFile;
    ArrayList<ByteBuffer> byteBufferFile;
    int bufferSize;
    FloatArrayThreadRunnable floatArrayThreadRunnable;
    ByteBufferThreadRunnable byteBufferThreadRunnable;
    FloatArrayPool floatArrayPool;
    ByteBufferPool byteBufferPool;
    private int bufferType;
    private Boolean songEnded;

    public MusicReader(int bufferSize, int bufferType) { //bufferType : 0 pour float[], autre pour ByteBuffer
        this.bufferType = bufferType;
        this.bufferSize = bufferSize;
<<<<<<< HEAD
        file = new ArrayList<>();
        threadRunnable = new ThreadRunnable(bufferSize, file);
        thread = new Thread(threadRunnable);
=======
        byteBufferPool = null;
        songEnded = false;
        if (bufferType == 0) {
            floatFile = new ArrayList<float[]>();
            floatArrayThreadRunnable = new FloatArrayThreadRunnable(bufferSize, floatFile);
            thread = new Thread(floatArrayThreadRunnable);
        } else {
            byteBufferFile = new ArrayList<ByteBuffer>();
            byteBufferThreadRunnable = new ByteBufferThreadRunnable(bufferSize, byteBufferFile);
            thread = new Thread(byteBufferThreadRunnable);
        }
    }

    public boolean songEnded() {
        synchronized (songEnded) {
            return songEnded;
        }
>>>>>>> distant_server/SaousanKad
    }

    public int getNumberOfBuffers() {
        int r;
        if (bufferType == 0) {
            synchronized (floatFile) {
                r = floatFile.size();
            }
        } else {
            synchronized (byteBufferFile) {
                r = byteBufferFile.size();
            }
        }
        return r;
    }

    public void addBuffer(float[] buffer) {
        if (bufferType == 0) {
            synchronized (floatFile) {
                floatFile.add(buffer);
                Log.i("lucas", "buffer added");
            }
        }
    }

    public void addBuffer(ByteBuffer buffer) {
        if (bufferType != 0) {
            synchronized (byteBufferFile) {
                byteBufferFile.add(buffer);
                //Log.i("lucas", "buffer added");
            }
        }
    }

    public void setByteBufferPool(ByteBufferPool byteBufferPool) {
        if (bufferType != 0) {
            this.byteBufferPool = byteBufferPool;
        }
    }

    public void setFloatArrayPool(FloatArrayPool FloatArrayPool) {
        if (bufferType == 0) {
            this.floatArrayPool = FloatArrayPool;
        }
    }

    public void play() {
        if (!thread.isAlive()) { //on recree un thread
            if (bufferType == 0) {
                floatArrayThreadRunnable = new FloatArrayThreadRunnable(bufferSize, floatFile);
                thread = new Thread(floatArrayThreadRunnable);
            } else {
                byteBufferThreadRunnable = new ByteBufferThreadRunnable(bufferSize, byteBufferFile);
                thread = new Thread(byteBufferThreadRunnable);
            }
            thread.start();
        }
    }

    public void stop() {
        if (bufferType == 0) {
            floatArrayThreadRunnable.stopReadingMusic();
        } else {
            byteBufferThreadRunnable.stopReadingMusic();
        }
    }

    public void pause() {

    }

    public void stopAtTheEnd() {
        Log.i("lucas", "stop at the end");
        if (bufferType == 0) {
            floatArrayThreadRunnable.stopAtTheEnd();
        } else {
            byteBufferThreadRunnable.stopAtTheEnd();
        }
    }

    private int fileSize() {
        if (bufferType == 0) {
            synchronized (floatFile) {
                return floatFile.size();
            }
        }
        synchronized (byteBufferFile) {
            return byteBufferFile.size();
        }
    }

    class FloatArrayThreadRunnable implements Runnable {
        ArrayList<float[]> file;
        int bufferSize;
        float[] buffer;
        boolean pasFiniLaBoucle, pasFiniDeLire;
        boolean pause;
        AudioTrack audioTrack;

        public FloatArrayThreadRunnable(int bufferSize, ArrayList<float[]> file) {
            this.bufferSize = bufferSize;
            this.file = file;
            pasFiniLaBoucle = true;
            pasFiniDeLire = true;
            audioTrack = new AudioTrack(STREAM_MUSIC, 44100, CHANNEL_OUT_MONO, ENCODING_PCM_FLOAT, bufferSize, MODE_STREAM);
        }

        public void stopReadingMusic() {
            pasFiniLaBoucle = false;
        }

        public void stopAtTheEnd() {
            pasFiniDeLire = false;
        }

        public void pause() {
            audioTrack.pause();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            Log.i("lucas", "bufferSize : " + String.valueOf(bufferSize));
            audioTrack.setVolume(1.0f);
            Log.i("lucas", "on a write, on va play");
            boolean first = true;
            while(pasFiniLaBoucle && ((pasFiniDeLire) || (fileSize() > 0))) { // faire quelque chose pour une file de taille nulle
                buffer = null;
                synchronized (file) {
                    if (file.size() > 0) {
                        buffer = file.get(0);
                        Log.i("lucas", "buffer chargé");
                        file.remove(0);
                    } else {
                        //Log.i("lucas", "Famine de buffer.");
                    }
                }
                if (buffer != null) {
                    audioTrack.write(buffer, 0, bufferSize, AudioTrack.WRITE_BLOCKING);
                    if (floatArrayPool != null) {
                        floatArrayPool.returnBuffer(buffer);
                    }
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
            songEnded = true;
        }
    }

    class ByteBufferThreadRunnable implements Runnable {
        ArrayList<ByteBuffer> file;
        int bufferSize;
        final int bytesPerFloat = 4;
        ByteBuffer buffer;
        boolean pasFiniLaBoucle, pasFiniDeLire;
        boolean pause;
        AudioTrack audioTrack;

        public ByteBufferThreadRunnable(int bufferSize, ArrayList<ByteBuffer> file) {
            this.bufferSize = bufferSize;
            this.file = file;
            pasFiniLaBoucle = true;
            pasFiniDeLire = true;
            audioTrack = new AudioTrack(STREAM_MUSIC, 44100, CHANNEL_OUT_MONO, ENCODING_PCM_FLOAT, bufferSize*bytesPerFloat, MODE_STREAM);
        }

        public void stopReadingMusic() {
            pasFiniLaBoucle = false;
        }

        public void stopAtTheEnd() {
            pasFiniDeLire = false;
        }

        public void pause() {
            audioTrack.pause();
        }

        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            Log.i("lucas", "bufferSize : " + String.valueOf(bufferSize));
            audioTrack.setVolume(1.0f);
            Log.i("lucas", "on va play");
            boolean first = true;
            while(pasFiniLaBoucle && ((pasFiniDeLire) || (fileSize() > 0))) { // faire quelque chose pour une file de taille nulle
                buffer = null;
                synchronized (file) {
                    if (file.size() > 0) {
                        buffer = file.get(0);
                        //Log.i("lucas", "buffer chargé");
                        file.remove(0);
                    } else {
                        //Log.i("lucas", "Famine de buffer.");
                    }
                }
                if (buffer != null) {
                    //Log.i("lucas", "on va write");
                    audioTrack.write(buffer, bufferSize*bytesPerFloat, AudioTrack.WRITE_BLOCKING);
                    //Log.i("lucas", "on a write");
                    if (byteBufferPool != null) {
                        byteBufferPool.returnBuffer(buffer);
                        //Log.i("lucas", "buffer retourne");
                    }
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
            songEnded = true;
        }
    }
}
