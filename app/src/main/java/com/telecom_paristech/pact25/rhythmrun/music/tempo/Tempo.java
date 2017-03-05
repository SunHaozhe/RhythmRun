package com.telecom_paristech.pact25.rhythmrun.music.tempo;

import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFileException;

import org.apache.commons.math3.complex.Complex;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.FastFourierTransform;

import java.io.File;
import java.io.IOException;

/**
 * Created by lucas on 22/01/17.
 */

public class Tempo { //faire le tri entre les int et les long

    private final static double findTempoHzBetweenFrames(WavFile waveFile, long firstFrame, long lastFrame) {
        Log.i("lucas", "on est rentrés dans findTempoHzBetweenFrames");
        double tempo = -1;
        int numberOfFramesToRead = (int)(lastFrame - firstFrame);
        long bufferSize = numberOfFramesToRead*waveFile.getNumChannels();
        double[] buffer = new double[(int)bufferSize];
        long sampleRate = waveFile.getSampleRate();

        try {
            long framesRemainingToBeRead = firstFrame; //on doit lire les frames avant firstFrame
            while (framesRemainingToBeRead > 0) {
                waveFile.readFrames(buffer, (int)min(bufferSize, framesRemainingToBeRead));
                framesRemainingToBeRead -= bufferSize;
            }

            waveFile.readFrames(buffer, numberOfFramesToRead);

            if (waveFile.getNumChannels() == 2) { //on convertit en mono si necessaire
                double[] tempBuffer = new double[numberOfFramesToRead];
                for (int i = 0; i<numberOfFramesToRead; i++) {
                    tempBuffer[i] = buffer[2*i] + buffer[2*i+1]; //on additionne gauche et droite
                }
                buffer = tempBuffer;

            }
            tempo = findTempo(buffer, numberOfFramesToRead, sampleRate);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }

        return tempo;
    }

    public final static double findTempoHz(String waveFilePath) {//resultat negatif si le morceau ne convient pas
        Log.i("lucas", "on est rentrés dans findTempoHz");
        return findTempoHzFast(waveFilePath);
        /*double tempo = -1;
        try {
            WavFile waveFile = WavFile.openWavFile(new File(waveFilePath)); //on suppose le fichier a 44100Hz

            tempo = findTempoHzBetweenFrames(waveFile, 0, waveFile.getNumFrames());

            waveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
        return tempo;*/
    }

    public final static double findTempoHzFast(String waveFilePath) {
        double tempo = -1;
        try {
            Log.i("lucas", "on est rentrés dans le try");
            WavFile waveFile = WavFile.openWavFile(new File(waveFilePath)); //on suppose le fichier a 44100Hz

            long numberOfFrames = waveFile.getNumFrames();
            long sampleRate = waveFile.getSampleRate();
            int numberOfSecondsToProceed = 10; // on n'analyse pas tout le morceau pour gagner du temps mais quelques secondes au milieu
            long numberOfFramesToRead = numberOfSecondsToProceed*sampleRate;

            tempo = findTempoHzBetweenFrames(waveFile, max((numberOfFrames-numberOfFramesToRead)/2, 0), min((numberOfFrames+numberOfFramesToRead)/2, numberOfFrames));

            waveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
        return tempo;
    }

    private final static double findTempo(double buffer[], int numberOfFrames, long sampleRate) {
        Log.i("lucas", "on est rentrés dans findTempo");
        double a = 0.9998;
        int N = 8; //parametre calcule pour fech = 44100kHz, on est toujours dans ce cas

        double[] energie = new double[numberOfFrames];
        energie[0] = carre(buffer[0]);
        for (int i = 1; i<numberOfFrames; i++) {
            energie[i] = a*energie[i-1] + carre(buffer[i]);
        }

        double dt = 0.01; //on re-echantillone tous les centiemes de secondes pour faire moins de calculs
        int pas = (int) (dt*sampleRate);
        int l = numberOfFrames/pas;
        double[] energieReEchantillonee = new double[l];
        for (int i = 0; i<l; i++) {
            energieReEchantillonee[i] = energie[pas*i];
        }

        double[] d = new double[l];
        diff(energieReEchantillonee, d, N, l);

        //faisons le zero-padding
        int tpsMin = 60; //pour avoir une precision inférieure au bpm
        int l2 = (int)(tpsMin/dt);
        int k = 1;
        while (k<l2 || k<l) {
            k *= 2;
        }
        double[] d2 = new double[k];
        System.arraycopy(d, 0, d2, 0, l);
        l = k;
        d = d2;

        //double[] tfd = new double[l];
        //TFDModule(d, tfd, l);
        Complex[] fft = FastFourierTransform.fft(d);
        int indiceDuMax = 0;
        double max = 0;
        for (int i = 1; i<l/2; i++) { // la valeur en 0 n'est  pas interessante
            if (fft[i].abs() > max) {
                indiceDuMax = i;
                max = fft[i].abs();
            }
        }
        Log.i("lucas", "l : " + String.valueOf(l));
        Log.i("lucas", "indiceDuMax : " + String.valueOf(indiceDuMax));

        return indiceDuMax/(l*dt);
    }

    private final static double carre(double racine) {
        return racine*racine;
    }

    private final static void TFDModule(double[] in, double[] out, int dim) {
        double r = 0, i = 0, pi = 3.14159265359;
        for (int k = 0; k<dim; k++) {
            r = 0; i = 0;
            for (int n = 0; n<dim; n++) {
                r += in[n]*Math.cos(((double)(2*pi*k*n))/((double)dim));
                i -= in[n]*Math.sin(((double)(2*pi*k*n))/((double)dim));
            }
            out[k] = carre(r) + carre(i);
        }
    }

    private final static void diff(double[] in, double[] out, int N, int dim) {
        double x;
        int m;
        for (int k = 0; k<dim; k++) {
            x = 0;
            m = (int)min(k+1, N);
            for (int i = 0; i<m; i++) {
                x += in[k-i];
            }
            m = (int)min(k+1, N+N);
            for (int i = N; i<m; i++) {
                x -= in[k-i];
            }
            out[k] = x;
        }
    }

    private final static long min(long a, long b) {
        if (a>b) {
            return b;
        }
        return a;
    }

    private final static long max(long a, long b) {
        if (a<b) {
            return b;
        }
        return a;
    }
}
