package com.example.raphaelattali.rythmrun.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

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
    private float plage_echantillonage; //temps pendant lequel on garde les donnees, en secondes
    private float periode;
    private float[][] valeurs;
    float ax, ay, az;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean active;
    private boolean finished = false;
    private boolean abs; //acceleration en valeur absolue
    private boolean auMoinsUnTour = false;
    int k = 0;

    public boolean isActive() {
        return active;
    }

    public float getAx() {
        return ax;
    }

    public float getAy() {
        return ay;
    }

    public float getAz() {
        return az;
    }

    /**
     *
     * @param periode inverse de la frequence d'echantillonage, en secondes
     * @param nombre_echantillons taille du tableau de valeurs
     */
    public Accelerometer(float periode, int nombre_echantillons, Context context) {
        this.abs = abs;
        this.periode = periode;
        frequence_echantillonage = 1/periode;
        this.nombre_echantillons = nombre_echantillons;
        this.plage_echantillonage = nombre_echantillons/frequence_echantillonage;
        valeurs = new float[3][nombre_echantillons];
        ax = 0f; ay = 0f; az = 0f;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (mSensor == null) {
            Log.i("lucas", "pas d'accelerometre");
        }
        active = false;
        //Log.d("lucas", "min delay : " + String.valueOf(mSensor.getMinDelay()));
        //completeTab(periode);
    }

    /**
     *
     * @param periode
     * @param plage_echantillonage
     */
    public Accelerometer(float periode, float plage_echantillonage, Context context) {
        this.abs = abs;
        this.periode = periode;
        frequence_echantillonage = 1/periode;
        this.plage_echantillonage = plage_echantillonage;
        this.nombre_echantillons = (int) (plage_echantillonage*frequence_echantillonage);
        valeurs = new float[3][nombre_echantillons];
        ax = 0; ay = 0; az = 0;
        active = false;
        //Log.d("lucas", "min delay : " + String.valueOf(mSensor.getMinDelay()));
        completeTab();
    }

    /**
     *
     * @param arg0
     * @param arg1
     */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        Log.i("lucas", "accuracy changed");
    }

    /**
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            active = true;
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
        }
    }

    /**
     * Renseigne le tableau de valeurs a intervalle de temps reguliere
     *
     */
    private void completeTab()
    {
        Thread thread = new Thread(new Runnable() {
            Date date = new Date();
            long temps_debut;
            @Override
            public void run() {
                temps_debut = date.getTime();
                while(finished == false) {
                    //valeurs[0][k] = ax;
                    valeurs[0][k] = (float)Math.sin(2*3.14159*k/10.0f); //3Hz environ
                    valeurs[1][k] = ay;
                    valeurs[2][k] = az;
                    if (valeurAbsolue(ax) > 1) {
                        Log.i("lucas", "paf");
                    }
                    if (k==nombre_echantillons-1) auMoinsUnTour = true;
                    k = (k+1)%nombre_echantillons;
                    try {
                        Thread.sleep((long) (1000*periode));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void stop() {
        finished = true;
    }

    public float[][] getArray() {
        return valeurs;
    }

    private final float valeurAbsolue(float x) {
        if (x>0) {
            return x;
        }
        return -x;
    }

    public final boolean auMoinsUnTour() {
        return auMoinsUnTour;
    }

    public int getNombreEchantillons() {
        return nombre_echantillons;
    }

    public final int getCurrentIndex() {
        return k;
    }

}
