package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFileException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by lucas on 09/04/17.
 */

public class NativeVocoder { //n'y acceder que depuis un seul thread (ou rajouter un flag pour le stop)

    static {
        System.loadLibrary("vocoder");
        Log.i("lucas", "lib chargee");
    }
    private native void initVocoderex(FloatBuffer floatSignal, int bufferSize, int tailleFenetre, int decalageIn);
    private native void freeVocoder();
    private native int nextVocoder(FloatBuffer floatBuffer, int decalageOut, int ts, int fenetresRequises);
    private native void initVocoder(FloatBuffer floatBuffer, int bufferSize, int tailleFenetre, int tagada);

    private String songPath;
    private ArrayList<ByteBuffer> byteBuffers;
    private ByteBuffer byteBuffer;
    private FloatBuffer floatBuffer;
    private final int bufferTime = 1;
    private final int bufferSize = 44100*bufferTime;
    private final int bytesPerFloat = 4;
    private final int tailleFenetre = 1024, decalageIn = tailleFenetre/4;
    private int lenSignal;
    private ByteBuffer byteSignal;
    private FloatBuffer floatSignal;
    private WavFile waveFile;

    private boolean songEnded;
    private long framesRemainingToBeRead;

    private float ratio, ratio_max; //mettre un ratio_min
    private int decalageOut, ts, fenetresRequises;

    public NativeVocoder(String songPath, int numberOfBuffersToHold) throws IOException, WavFileException {
        this.songPath = songPath;
        byteBuffers = new ArrayList<ByteBuffer>();
        for(int k=0; k<numberOfBuffersToHold; k++) {
            byteBuffers.add(ByteBuffer.allocateDirect(bytesPerFloat*bufferSize).order(ByteOrder.nativeOrder())); //les bits d'un float sont a l'envers en C;
        }
        ratio = 1.0f;
        ratio_max = 2.0f;

        int decalageOutMin = (int)((float)decalageIn/ratio_max);
        lenSignal = ((bufferSize-1)/decalageOutMin)*decalageIn+tailleFenetre;
        byteSignal = ByteBuffer.allocateDirect(bytesPerFloat*lenSignal).order(ByteOrder.nativeOrder());
        byteSignal.clear();
        floatSignal = byteSignal.asFloatBuffer();
        Log.i("lucas","len signal : " + String.valueOf(lenSignal));
        Log.i("lucas","capacity : " + String.valueOf(floatSignal.capacity()));
        Log.i("lucas","limit : " + String.valueOf(floatSignal.limit()));
        Log.i("lucas","position : " + String.valueOf(floatSignal.position()));
        Log.i("lucas","on va init");
        //initVocoder(floatSignal, bufferSize, tailleFenetre, decalageIn);
        initVocoder(floatSignal, bufferSize, tailleFenetre, decalageIn);
        Log.i("lucas", "on a init");
        waveFile = WavFile.openWavFile(new File(songPath));
        Log.i("lucas", "wav ouvert");
        framesRemainingToBeRead = waveFile.getNumFrames();
        songEnded = false;

        ts = 0;
    }

    public ByteBuffer getNextBuffer() {
        if (songEnded) {
            return null; //ou bien une exception
        }
        byteBuffer = null;
        while (byteBuffer == null) {
            synchronized (byteBuffers) {
                if (byteBuffers.size() > 0) { //ce sera toujours le cas
                    byteBuffer = byteBuffers.get(0);
                    byteBuffers.remove(0);
                    floatBuffer = byteBuffer.asFloatBuffer(); //est-ce bien necessaire ?
                } else {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        decalageOut = (int) ((float)decalageIn/ratio);
        fenetresRequises = (bufferSize+decalageOut-1-ts)/decalageOut;
        Log.i("lucas", "on va faire fill signal");
        fillSignal(fenetresRequises*decalageIn, tailleFenetre-decalageIn);
        Log.i("lucas", "on va faire next vocoder");
        ts = nextVocoder(floatBuffer, decalageOut, ts, fenetresRequises);
        Log.i("lucas", "on a fait next vocoder et ts = " + String.valueOf(ts));
        if (songEnded) {
            freeVocoder();
            try {
                waveFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteBuffer;
    }

    private void fillSignal(int numberOfFrames, int offset) {
        //Log.i("lucas", "fill signal : " + String.valueOf(numberOfFrames) + " et " + String.valueOf(offset) + " or " + String.valueOf(framesRemainingToBeRead));
        try {
            if(numberOfFrames>=framesRemainingToBeRead) {
                songEnded = true;
                numberOfFrames = (int)framesRemainingToBeRead;
            }
            //Log.i("lucas", "float signal : " + String.valueOf(floatSignal));
            waveFile.readFrames(floatSignal, offset, numberOfFrames);
            Log.i("lucas", "lu");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
    }

    public void returnBuffer(ByteBuffer buffer) {
        synchronized (byteBuffers) {
            byteBuffers.add(buffer);
        }
    }

    public boolean songEnded() {
        return songEnded;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;//attention a ratio_max
    }

    public void stop() {
        if (!songEnded) {
            freeVocoder();
            try {
                waveFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
