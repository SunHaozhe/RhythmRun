package com.telecom_paristech.pact25.rhythmrun.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by lucas on 12/12/16.
 */

/**
 *
 */
public class Accelerometer implements SensorEventListener {
    private final float frequenceEchantillonage;
    private final int nombreEchantillons;
    private final float plageEchantillonage; //temps pendant lequel on garde les donnees, en secondes
    private final float periode;
    private final float[][] valeurs;
    float ax, ay, az;
    private final SensorManager sensorManager;
    private final Sensor sensor;
    private boolean active;
    private boolean auMoinsUnTour = false;
    private int k = 0;
    private final ScheduledExecutorService scheduler;
    private final ScheduledFuture<?> scheduledTabCompleter;
    private Long tempsDebut;
    private Long dernierTemps;

    public boolean isActive() {
        return active&&auMoinsUnTour;
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

    public long getLastTime() {
        return dernierTemps;
    }

    /**
     *
     * @param periode inverse de la frequence d'echantillonage, en secondes
     * @param nombre_echantillons taille du tableau de valeurs
     */
    public Accelerometer(float periode, int nombre_echantillons, Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (sensor == null) {
            Log.i("lucas", "pas d'accelerometre");
        }
        Log.d("lucas", "min delay : " + String.valueOf(sensor.getMinDelay()));
        active = false;
        this.periode = periode;
        frequenceEchantillonage = 1/periode;
        this.nombreEchantillons = nombre_echantillons;
        this.plageEchantillonage = nombre_echantillons/frequenceEchantillonage;
        valeurs = new float[3][nombre_echantillons];
        scheduler = Executors.newScheduledThreadPool(1);
        tempsDebut = (long)0;
        scheduledTabCompleter = scheduler.scheduleAtFixedRate(new tabCompleter(tempsDebut), 10L, (long) (1000*periode), TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @param periode
     * @param plage_echantillonage
     */
    public Accelerometer(float periode, float plage_echantillonage, Context context) {
        this(periode, (int)(plage_echantillonage/periode), context);
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
     * Renseigne le tableau de valeurs a intervalle de temps reguliers
     *
     */
    private void completeTab()
    {

    }

    public final void stop() {
        scheduledTabCompleter.cancel(true);
    }

    public final float[][] getArray() {
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

    public final int getNombreEchantillons() {
        return nombreEchantillons;
    }

    public final int getCurrentIndex() {
        return k;
    }

    class tabCompleter implements Runnable {
        boolean firstRun = true;
        long tempsDebut;
        tabCompleter(Long tempsDebut) {
            this.tempsDebut = tempsDebut;
        }
        @Override
        public void run() {
            dernierTemps = SystemClock.elapsedRealtime();
            if (firstRun) {
                tempsDebut = dernierTemps;
                firstRun = false;
            }
            synchronized (valeurs) {
                valeurs[0][k] = ax;
                valeurs[1][k] = ay;
                valeurs[2][k] = az;
            }
            if (k==nombreEchantillons-1) auMoinsUnTour = true;
            k = (k+1)%nombreEchantillons;
        }

    }
}
