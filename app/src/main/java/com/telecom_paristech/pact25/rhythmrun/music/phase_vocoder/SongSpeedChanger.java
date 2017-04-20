package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */


import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.interfaces.music.FloatArraySupplier;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;

import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavProcess;

import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFileException;

<<<<<<< HEAD

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

=======
>>>>>>> distant_server/SaousanKad
import java.io.File;
import java.io.IOException;

/**
 * Classe permettant d'accélérer/de ralentir une chanson. Attention, le résultat ne sera pas agréable à l'oreille si
 * les différences de vitesse sont de plus de 20-30%
 */
<<<<<<< HEAD
public class SongSpeedChanger {
        String songPath;
    float songTempoHz;
    public SongSpeedChanger(String songPath, float songTempoHz) {
        this.songPath = songPath;
        this.songTempoHz = songTempoHz;
    }

    public static double[] modifyBufferSpeed(double[] buffer, double coef){

        Log.i("PROCESSING", "Interpolation en cours ...");
        int N = buffer.length;

        return SelfMadeInterpolator.interp1(
                SelfMadeInterpolator.getIndexesTab(N),
                buffer,
                SelfMadeInterpolator.getIndexesInterpTab(N,coef));
    }
=======
public class SongSpeedChanger implements FloatArraySupplier {
>>>>>>> distant_server/SaousanKad

    WavFile waveFile;
    int bufferSize;
    double y1, y2, f, r;
    long framesRemainingToBeRead;
    boolean songEnded;

    public SongSpeedChanger(String path, int bufferSize, double ratio) throws IOException, WavFileException {
        waveFile = WavFile.openWavFile(new File(path));
        r = ratio;
        this.bufferSize = bufferSize;
        framesRemainingToBeRead = waveFile.getNumFrames();
        double[] temp = new double[2];
        waveFile.readFrames(temp, 2);
        framesRemainingToBeRead -= 2;
        y1 = temp[0];
        y2 = temp[1];
        f = 0;
        songEnded = false;
    }

    public final boolean songEnded() {
        return songEnded;
    }

    public final float[] getNextBuffer() {
        float[] result = new float[bufferSize];
        int numberOfFramesToRead = (int)Math.round(Math.ceil(f + (bufferSize-1)*r))-2;
        Log.i("lucas", "numberOfFrames... : " + String.valueOf(f) + " " + String.valueOf(bufferSize) + " " + String.valueOf(Math.ceil(f + (bufferSize-1)*r)) + " ");
        if (numberOfFramesToRead > framesRemainingToBeRead) {
            numberOfFramesToRead = (int)framesRemainingToBeRead;
        }
        double[] bufferIn = new double[numberOfFramesToRead];
        try {
            waveFile.readFrames(bufferIn, numberOfFramesToRead);
            Log.i("lucas", "on lit " + String.valueOf((float)numberOfFramesToRead/(float)44100) + "s");
            framesRemainingToBeRead -= numberOfFramesToRead;
            if (framesRemainingToBeRead == 0) {
                songEnded = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
        double fAuDebut = f;
        int indiceDey2 = -1;
        for (int k = 0; k<bufferSize; k++) {
            result[k] = (float)(f*y1+(1-f)*y2);
            //puis on prepare la prochaine interpolation
            f = (fAuDebut + (k+1)*r)%1;
            int d;
            if (  (d = ( (int)(fAuDebut + (k+1)*r) - (int)(fAuDebut + k*r) ))  > 0) { // si les deux valeurs entre lesquelles on interpole doivent etre changees
                while (d > 0) {
                    y1 = y2;
                    if (indiceDey2 < numberOfFramesToRead-1) {
                        y2 = bufferIn[indiceDey2 + 1];
                        indiceDey2++;
                    } else {
                        if (framesRemainingToBeRead > 0) {
                            double[] temp = new double[1];
                            try {
                                waveFile.readFrames(temp, 1);
                                framesRemainingToBeRead --;
                                y2 = temp[0];
                                indiceDey2++;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (WavFileException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    d--;
                }
            }
        }
        //close wave
        Log.i("lucas", "ca doit etre 1 : " + String.valueOf(indiceDey2-numberOfFramesToRead));
        return result;
        /*int n = bufferSize;
        if (n > framesRemainingToBeRead) {
            n = (int)framesRemainingToBeRead;
        }
        double[] bufferIn = new double[n];
        try {
            waveFile.readFrames(bufferIn, n);
            framesRemainingToBeRead -= n;
            if (framesRemainingToBeRead == 0) {
                songEnded = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
        return identity(bufferIn, bufferSize);*/
    }


    public static float[] identity(double[] bufferIn, int sizeOut) {
        float[] bufferOut = new float[sizeOut];
        float r = ((float) sizeOut)/((float)bufferIn.length);
        for (int i = 0; i < sizeOut; i++) {
            bufferOut[i] = (float)bufferIn[i];
            //bufferOut[k] = (float)(bufferIn[(int)(k/r)]*( ((float) k)/r) - ((float)k/r) ) + (float)(bufferIn[(int)(k/r)+1]*( 1 - ((float) k)/r) + ((float)k/r) );
        }
        return bufferOut;
    }


    /*WavFile file;

    public double[] modifyMusicToFitTempo(Song song, float tempo) {

        String path = song.getPath();


        // y = audioread(...)

        int N = 5;//length(y)

        // t = (0:N-1)/N
        double[] t = SelfMadeInterpolator.getIndexesTab(N);
        double coef = tempo;// /freq;


        double[] tInterpol = SelfMadeInterpolator.getIndexesInterpTab(N,coef);

        return SelfMadeInterpolator.interp1(t,song.getValues(),tInterpol);
    }*/
}
