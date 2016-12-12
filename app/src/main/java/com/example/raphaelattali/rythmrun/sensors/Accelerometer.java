package com.example.raphaelattali.rythmrun.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Date;

/**
 * Created by lucas on 12/12/16.
 */

/**
 *
 */
public class Accelerometer implements SensorEventListener {
    private float frequence_echantillonage;
    private int nombre_echantillons;
    private float plage_echantillonage; //temps pendant lequel on garde les donnees
    private float periode;
    private float[][] valeurs;
    float ax, ay, az;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    public float getAx() {
        return ax;
    }

    /**
     *
     * @param periode
     * @param nombre_echantillons
     */
    public Accelerometer(float periode, int nombre_echantillons, Context context) {
        this.periode = periode;
        frequence_echantillonage = 1/periode;
        this.nombre_echantillons = nombre_echantillons;
        this.plage_echantillonage = nombre_echantillons/frequence_echantillonage;
        valeurs = new float[3][nombre_echantillons];
        ax = 21f; ay = 0; az = 0;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (mSensor == null) {
            ax = 42.0f;
        }

        //completeTab(periode);
    }

    /**
     *
     * @param periode
     * @param plage_echantillonage
     */
    public Accelerometer(float periode, float plage_echantillonage) {
        this.periode = periode;
        frequence_echantillonage = 1/periode;
        this.plage_echantillonage = plage_echantillonage;
        this.nombre_echantillons = (int) (plage_echantillonage*frequence_echantillonage);
        valeurs = new float[3][nombre_echantillons];
        ax = 0; ay = 0; az = 0;
        //completeTab(periode);
    }

    /**
     *
     * @param arg0
     * @param arg1
     */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { //osef
    }

    /**
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
        }
    }

    /**
     * Renseigne le tableau de valeurs a intervalle de temps reguliere
     *
     * @param delaySec
     *      Delai d'actualisation
     */
    public void completeTab(float delaySec)
    {
        Thread thread = new Thread(new Runnable() {
            Date date = new Date();
            long temps_debut;
            @Override
            public void run() {
                temps_debut = date.getTime();
                valeurs[0][0] = (float)0.0;
            }
        });
        thread.start();
    }





}
