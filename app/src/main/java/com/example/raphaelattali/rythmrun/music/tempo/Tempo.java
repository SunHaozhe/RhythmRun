package com.example.raphaelattali.rythmrun.music.tempo;

import android.util.Log;

import com.example.raphaelattali.rythmrun.music.waveFileReaderLib.WavFile;
import com.example.raphaelattali.rythmrun.music.waveFileReaderLib.WavFileException;

import java.io.File;
import java.io.IOException;

/**
 * Created by lucas on 22/01/17.
 */

public class Tempo {
    public final static double findTempoHz(String waveFilePath) {//resultat negatif si le morceau ne convient pas
        Log.i("lucas", "on est rentrés dans findTempoHz");
        double tempo = -1;
        try {
            Log.i("lucas", "on est rentrés dans le try");
            WavFile waveFile = WavFile.openWavFile(new File(waveFilePath)); //on suppose le fichier a 44100Hz

            int numberOfChannels = waveFile.getNumChannels();
            long numberOfFrames = waveFile.getNumFrames();
            long sampleRate = waveFile.getSampleRate();
            //int numberOfSecondsToProceed = 5; // on n'analyse pas tout le morceau pour gagner du temps
            int numberOfFramesToRead = (int)numberOfFrames;//(int) (numberOfSecondsToProceed*sampleRate);
            double[] buffer = new double[numberOfFramesToRead*numberOfChannels];

            /*if (numberOfFrames/sampleRate < numberOfSecondsToProceed) {
                Log.i("lucas", "on veut traiter trop de secondes");
                Log.i("lucas", "numberOfFrames : " + String.valueOf(numberOfFrames));
                Log.i("lucas", "sampleRate : " + String.valueOf(sampleRate));
                Log.i("lucas", "numberOfSecondsToProceed : " + String.valueOf(numberOfSecondsToProceed));
                return -1f;
            }*/

            waveFile.readFrames(buffer, numberOfFramesToRead);
            if (numberOfChannels == 2) { //on convertit en mono si necessaire
                double[] tempBuffer = new double[numberOfFramesToRead];
                for (int i = 0; i<numberOfFramesToRead; i++) {
                    tempBuffer[i] = buffer[2*i] + buffer[2*i+1]; //on additionne gauche et droite
                }
                buffer = tempBuffer;
            }

            double m = 0;
            for (double x : buffer) {
                if (x>m||(-x)>m) {
                    m = x;
                    if (x<0) {
                        m = -x;
                    }
                }
            }
            Log.i("lucas", "max du buffer : " + String.valueOf(m));

            tempo = findTempo(buffer, numberOfFramesToRead, sampleRate);

            waveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
        return tempo;
    }

    private final static double findTempo(double buffer[], int numberOfFrames, long sampleRate) {
        double a = 0.9998;
        int N = 8;

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

        double[] tfd = new double[l];
        TFDModule(d, tfd, l);
        int indiceDuMax = 0;
        double max = 0;
        for (int i = 1; i<l/2; i++) { // la valeur en 0 n'est  pas interessante
            if (tfd[i] > max) {
                indiceDuMax = i;
                max = tfd[i];
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
            m = min(k+1, N);
            for (int i = 0; i<m; i++) {
                x += in[k-i];
            }
            m = min(k+1, N+N);
            for (int i = N; i<m; i++) {
                x -= in[k-i];
            }
            out[k] = x;
        }
    }

    private final static int min(int a, int b) {
        if (a>b) {
            return b;
        }
        return a;
    }
}
