package com.example.raphaelattali.rythmrun.sensors;

import com.example.raphaelattali.rythmrun.interfaces.sensors.PodometerInterface;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
    private float tfd[];
    private float tempsReleves;
    private float periodeEchantillonage;
    private Accelerometer acc;

    public Podometer(Context context) {
        Log.i("lucas", "constructeur du podometre");
        this.context = context;
        tempsReleves = 5.0f;
        periodeEchantillonage = 0.03f;
        acc = new Accelerometer(periodeEchantillonage, tempsReleves, context);
        numberOfValues = acc.getNombreEchantillons();
        values = new float[3][numberOfValues];
        accValues = acc.getArray();
        while (!acc.auMoinsUnTour()) {
            sleep(500);
            Log.i("lucas", "attente...");
        }
        min = 0; max = numberOfValues/2;
        tfd = new float[max-min];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("lucas", "on est rentrés dans le thread du podometre");
                for (int i = 0; i<10; i++) {
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
        return 0.0f;
    }

    public float lastStepTimeSinceBoot() {
        return 0.0f;
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
        System.arraycopy(accValues[0], debut, values[0], 0, numberOfValues-debut);
        System.arraycopy(accValues[0], 0, values[0], numberOfValues-debut, debut);
        computeTFD(0);
        Log.i("lucas", "on va calculer l'indice max");
        int indice = min + indiceMaxi(tfd, max-min);
        paceFrequency = indice/tempsReleves;
        Log.i("lucas", "on a calcule une frequence de " + String.valueOf(paceFrequency));
    }

    private final void computeTFD(int index) { //index : 0 pour x, 1 pour y ou 2 pour z, a remplacer par une fft
        float r = 0, i = 0, pi = 3.14159265359f;
        for (int k = min; k<max; k++) {
            r = 0; i = 0;
            for (int n = 0; n<numberOfValues; n++) {
                r += values[index][n]*Math.cos(2*pi*k*n/numberOfValues);
                i -= values[index][n]*Math.sin(2*pi*k*n/numberOfValues);
            }
            tfd[k-min] = r*r+i*i;
        }
        Log.i("lucas", "on a calcule une tfd");
        Log.i("lucas", "numberOfValues : " + String.valueOf(numberOfValues));
    }

    private final int indiceMaxi(float t[], int len) {
        Log.i("lucas", "on calcule l'indice maxi");
        int k = 0;
        float m = -1;
        for (int n = 0; n<len; n++) {
            if (t[n] > m) {
                m = t[n];
                k = n;
            }
        }
        Log.i("lucas", "max : " + String.valueOf(m));
        return k;
    }
}
