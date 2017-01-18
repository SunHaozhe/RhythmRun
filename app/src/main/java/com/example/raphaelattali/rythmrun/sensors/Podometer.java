package com.example.raphaelattali.rythmrun.sensors;

import com.example.raphaelattali.rythmrun.interfaces.sensors.PodometerInterface;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lucas on 12/12/16.
 */

public class Podometer implements PodometerInterface, SensorEventListener {

    Context context;
    float paceFrequency = 1.0f;
    private int min, max; //on restreint le domaine de la tfd car on a un ODG des frequences cherchees
    private final float alpha = 0.25f;
    private boolean[] pas;
    private int numberOfValues;
    private float[][] values;
    private float[][] accValues;
    private float tfd[][];
    private float tempsReleves;
    private float periodeEchantillonage;
    private Accelerometer acc;
    private boolean fini;
    private final float pi = 3.14159265359f;
    private long oneStepTime;

    public Podometer(Context context) {
        oneStepTime = 0;
        Log.i("lucas", "constructeur du podometre");
        this.context = context;
        tempsReleves = 5.0f;
        periodeEchantillonage = 0.03f;
        acc = new Accelerometer(periodeEchantillonage, tempsReleves, context);
        numberOfValues = acc.getNombreEchantillons();
        values = new float[3][numberOfValues];
        accValues = acc.getArray();
        while (!acc.isActive()) {
            sleep(500);
            Log.i("lucas", "attente...");
        }
        min = 1; //pour eviter le fond continu de 1g
        max = numberOfValues/2;
        tfd = new float[max-min][2]; //2 pour les complexes
        fini = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("lucas", "on est rentrés dans le thread du podometre");
                while (!fini) {
                    computePacePeriod();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { //osef
    }
    public void onSensorChanged(SensorEvent event) { //osef
    }

    //Récupérer la fréquence de pas actuelle du coureur
    public float getRunningPaceFrequency() {
        return paceFrequency;
    }

    public long lastStepTimeSinceBoot() {
        return oneStepTime;
    }

    private void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final void computePacePeriod() {
        int debut = acc.getCurrentIndex();
        long timeOfLastValue = SystemClock.elapsedRealtime();
        System.arraycopy(accValues[0], debut, values[0], 0, numberOfValues-debut);
        System.arraycopy(accValues[0], 0, values[0], numberOfValues-debut, debut);
        computeTFD(0);
        //Log.i("lucas", "on va calculer l'indice max");
        int indiceDuMaxi = indiceMaxi(tfd, max-min);
        int indice = min + indiceDuMaxi;
        paceFrequency = indice/tempsReleves;
        computeOneStepTime(indiceDuMaxi, timeOfLastValue);
        //Log.i("lucas", "on a calcule une frequence de " + String.valueOf(paceFrequency));
    }

    private final void computeTFD(int index) { //index : 0 pour x, 1 pour y ou 2 pour z, a remplacer par une fft
        float r = 0, i = 0;
        for (int k = min; k<max; k++) {
            r = 0; i = 0;
            for (int n = 0; n<numberOfValues; n++) {
                r += values[index][n]*Math.cos(2*pi*k*n/numberOfValues);
                i -= values[index][n]*Math.sin(2*pi*k*n/numberOfValues);
            }
            tfd[k-min][0] = r;
            tfd[k-min][1] = i;
        }
        //Log.i("lucas", "on a calcule une tfd");
        //Log.i("lucas", "numberOfValues : " + String.valueOf(numberOfValues));
    }

    private final int indiceMaxi(float t[][], int len) {
        //Log.i("lucas", "on calcule l'indice maxi");
        int k = 0;
        float m = -1;
        float temp = 0;
        for (int n = 0; n<len; n++) {
            temp = t[n][0]*t[n][0]+t[n][1]*t[n][1];
            if (temp > m) {
                m = temp;
                k = n;
            }
        }
        //Log.i("lucas", "max : " + String.valueOf(m));
        return k;
    }

    public final void stop() {
        fini = true;
        acc.stop();
    }

    private final float phase(float real, float imag) {
        if (Math.abs(real) <= 0.0001) {
            if (imag >= 0) {
                return pi/2;
            }
            return 3*pi/2;
        }
        float phase = (float)Math.atan((double)(imag/real));
        if (real>=0&&imag>=0) return phase;
        if (real>=0&&imag<0) return phase+2*pi;
        return phase+pi;
    }

    private final void computeOneStepTime(int indiceDuMaxiDansLaTFD, long timeOfLastValue) {
        float maPhase = phase(tfd[indiceDuMaxiDansLaTFD][0], tfd[indiceDuMaxiDansLaTFD][1]);
        int n = (int)(numberOfValues*maPhase/(2*pi*(indiceDuMaxiDansLaTFD+min)));
        /*if (n<0) {
            n = 0;
            Log.i("lucas", "probleme de steptime trop grand"+String.valueOf(n-numberOfValues) + " phase : " + String.valueOf(maPhase) + " indicedumaxi : " + String.valueOf(indiceDuMaxiDansLaTFD));
           }
        if (n>=numberOfValues) {
            Log.i("lucas", "probleme de steptime trop grand"+String.valueOf(n-numberOfValues) + " phase : " + String.valueOf(maPhase) + " indicedumaxi : " + String.valueOf(indiceDuMaxiDansLaTFD));
            n = numberOfValues-1;
        }*/
        oneStepTime = timeOfLastValue-(long)(periodeEchantillonage*(numberOfValues-n));
    }
}
