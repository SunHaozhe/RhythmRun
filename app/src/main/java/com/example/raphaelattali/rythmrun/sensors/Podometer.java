package com.example.raphaelattali.rythmrun.sensors;

import com.example.raphaelattali.rythmrun.interfaces.sensors.PodometerInterface;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by lucas on 12/12/16.
 */

public class Podometer implements PodometerInterface, SensorEventListener {

    Context context;
    float paceFrequency = 1.0f;
    float min, max;
    private final float alpha = 0.25f;
    private boolean[] pas;
    private int numberOfValues;
    private float[][] values;
    private float[][] accValues;
    Accelerometer acc;

    public Podometer(Context context) {
        this.context = context;
        acc = new Accelerometer(0.03f, 5.0f, context, false);
        numberOfValues = acc.getNombreEchantillons();
        values = new float[3][numberOfValues];
        accValues = acc.getArray();
        if (!acc.isActive()) {
            return;
        }
        sleep(5000);
        while (!acc.auMoinsUnTour()) {
            sleep(100);
        }


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
        boolean signe = true; //positif
        ArrayList<Float> stepTimes = new ArrayList<Float>();
    }
}
