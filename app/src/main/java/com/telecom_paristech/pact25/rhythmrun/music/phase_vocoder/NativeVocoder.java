package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.interfaces.music.ByteBufferPool;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.ByteBufferSupplier;
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

public class NativeVocoder implements ByteBufferSupplier, ByteBufferPool{ //n'y acceder que depuis un seul thread (ou rajouter un flag pour le stop)

    static {
        System.loadLibrary("vocoder");
        Log.i("lucas", "lib chargee");
    }
    private native void freeVocoder();
    private native int nextVocoder(FloatBuffer floatBuffer, int decalageOut, int fenetresRequises, int ts);
    private native int initVocoder(FloatBuffer floatSignal, int lenSignal, int bufferSize, int tailleFenetre, int tagada);
    private native void test(FloatBuffer bufferOut);//, FloatBuffer bufferOut

    private String songPath;
    private ArrayList<ByteBuffer> byteBuffers;
    private ByteBuffer byteBuffer;
    private FloatBuffer floatBuffer;
    private int bufferSize;
    private final int bytesPerFloat = 4;
    private final int tailleFenetre = 1024, decalageIn = tailleFenetre/4;
    private int lenSignal;
    private ByteBuffer byteSignal;
    private FloatBuffer floatSignal;
    private WavFile waveFile;

    private boolean songEnded;
    private long framesRemainingToBeRead;

    private float ratio;
    private final float ratio_max = 3.0f;
    private final float ratio_min = 0.7f;
    private int decalageOut, ts, fenetresRequises;

    public NativeVocoder(String songPath, int bufferSize, int numberOfBuffersToHold) throws IOException, WavFileException {
        this.bufferSize = bufferSize;
        this.songPath = songPath;
        byteBuffers = new ArrayList<ByteBuffer>();
        for(int k=0; k<numberOfBuffersToHold; k++) {
            byteBuffers.add(ByteBuffer.allocateDirect(bytesPerFloat*(bufferSize+tailleFenetre)).order(ByteOrder.nativeOrder())); //les bits d'un float sont a l'envers en C;
        }
        ratio = 1.0f;

        int decalageOutMin = (int)((float)decalageIn/ratio_max);
        lenSignal = ((bufferSize-1)/decalageOutMin)*decalageIn+tailleFenetre;
        byteSignal = ByteBuffer.allocateDirect(bytesPerFloat*lenSignal).order(ByteOrder.nativeOrder());
        byteSignal.clear();
        floatSignal = byteSignal.asFloatBuffer();
        //Log.i("lucas","len signal : " + String.valueOf(lenSignal));
        //Log.i("lucas","capacity : " + String.valueOf(floatSignal.capacity()));
        //Log.i("lucas","limit : " + String.valueOf(floatSignal.limit()));
        //Log.i("lucas","position : " + String.valueOf(floatSignal.position()));
        //Log.i("lucas","on va init");
        //initVocoder(floatSignal, bufferSize, tailleFenetre, decalageIn);
        int i = initVocoder(floatSignal, lenSignal, bufferSize, tailleFenetre, decalageIn);
        //Log.i("lucas", "init : " + String.valueOf(i));
        //Log.i("lucas", "on a init");
        waveFile = WavFile.openWavFile(new File(songPath));
        //Log.i("lucas", "wav ouvert");
        framesRemainingToBeRead = waveFile.getNumFrames();
        fillSignal(tailleFenetre-decalageIn, 0);
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
                    byteBuffer.position(0);
                    floatBuffer = byteBuffer.asFloatBuffer(); //est-ce bien necessaire ?
                } else {
                    //Log.i("lucas", "mais ou sont les buffers");
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
        //Log.i("lucas", "on va faire fill signal et nenetresRequises = " + String.valueOf(fenetresRequises));
        fillSignal(fenetresRequises*decalageIn, tailleFenetre-decalageIn);
        //Log.i("lucas", "on va faire next vocoder");
        nextVocoder(floatBuffer, decalageOut, fenetresRequises, ts);
        //Log.i("lucas", "on a fait next vocoder");
        //floatBuffer.position(0);
        //Log.i("lucas", "floatBuffer[0] : " + String.valueOf(floatBuffer.get()));
        //Log.i("lucas", "on a fait next vocoder et ts = " + String.valueOf(ts));
        if (songEnded) {
            freeVocoder();
            //Log.i("lucas", "vocoder libere");
            try {
                waveFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //byteBuffer.position(0);
        //Log.i("lucas", String.valueOf(floatBuffer.get()));
        byteBuffer.position(0);
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
                framesRemainingToBeRead -= numberOfFrames;
            //Log.i("lucas", "lu");
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
        if (ratio <= ratio_max && ratio >= ratio_min) {
            this.ratio = ratio;
        }
        else {
            if(ratio > ratio_max) {
                this.ratio = ratio_max;
            } else {
                this.ratio = ratio_min;
            }
        }
        //Log.i("lucas", "vocoder ratio : " + String.valueOf(this.ratio));
        //this.ratio = 1.0f;///////test
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
