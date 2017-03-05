package com.telecom_paristech.pact25.rhythmrun.sensors;

import com.telecom_paristech.pact25.rhythmrun.interfaces.sensors.PodometerInterface;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by lucas on 12/12/16.
 */

public class Podometer implements PodometerInterface, SensorEventListener {

    Context context;
    private float paceFrequency = -1.0f, lastPaceFrequency = -1.0f;
    private final float minPaceFrequency = 0.5f, maxPaceFrequency = 3f; // il faut max>=2*min
    //private int min, max; //on restreint le domaine de la tfd car on a un ODG des frequences cherchees
    private final float alpha = 0.25f;
    private boolean[] pas;
    private int numberOfValues, numberOfValuesAfterZeroPadding;
    private float[][] values;
    private float[][] accValues;
    private float tfd[][];
    private float tempsReleves, tempsRelevesApresZeroPadding;
    private float periodeEchantillonage;
    private Accelerometer acc;
    private boolean fini;
    private final float pi = 3.14159265359f;
    private long oneStepTime;
    private boolean manualSyncRequested;
    long timeOfLastValue;

    public Podometer(Context context) {
        oneStepTime = 0;
        Log.i("lucas", "constructeur du podometre");
        this.context = context;
        tempsReleves = 4.0f;
        periodeEchantillonage = 0.03f;
        acc = new Accelerometer(periodeEchantillonage, tempsReleves, context);
        numberOfValues = acc.getNombreEchantillons();

        //zero padding
        int minTime = 120; //pour avoir une precision d'un demi bpm
        int numberMinOfValuesAfterZeroPadding = (int)((float)minTime/periodeEchantillonage);
        numberOfValuesAfterZeroPadding = 1;
        while (numberOfValuesAfterZeroPadding<numberMinOfValuesAfterZeroPadding || numberOfValuesAfterZeroPadding<numberOfValues) {
            numberOfValuesAfterZeroPadding *= 2; //on veut un puissance de 2 pour faire la fft
        }
        tempsRelevesApresZeroPadding = numberOfValuesAfterZeroPadding*periodeEchantillonage;

        values = new float[3][numberOfValues];
        accValues = acc.getArray();
        while (!acc.isActive()) {
            sleep(500);
            Log.i("lucas", "attente...");
        }
        tfd = new float[numberOfValuesAfterZeroPadding][2]; //2 pour les complexes
        fini = false;
        manualSyncRequested = true;
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

    public boolean isActive() {
        return acc.isActive()&&(paceFrequency>=0);
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
        timeOfLastValue = acc.getLastTime();
        synchronized (accValues) {
            System.arraycopy(accValues[0], debut, values[0], 0, numberOfValues-debut);
            System.arraycopy(accValues[0], 0, values[0], numberOfValues-debut, debut);
        }
        computeTFD(0);
        //Log.i("lucas", "on va calculer l'indice max");
        int indice = indiceMaxiEntre(tfd, (int)(minPaceFrequency*tempsRelevesApresZeroPadding), (int)(maxPaceFrequency*tempsRelevesApresZeroPadding));
        lastPaceFrequency = paceFrequency;
        paceFrequency = indice/tempsRelevesApresZeroPadding;

        //on double ou divise par 2 la frequence trouvee pour etre dans un intervalle raisonnable
        /*while (paceFrequency > maxPaceFrequency) {paceFrequency /= 2;}
        if (paceFrequency > 0) {
            while (paceFrequency < minPaceFrequency) {
                paceFrequency *= 2;
            }
        }*/
        //float moyenne = tfd[0][0]/numberOfValues;
        if (manualSyncRequested) {
            computeOneStepTime(0);
            manualSyncRequested = false;
        } else { //temporaire
            computeOneStepTime2(0);
        }
        //Log.i("lucas", "on a calcule une frequence de " + String.valueOf(paceFrequency));
    }

    private final void computeTFD(int index) { //index : 0 pour x, 1 pour y ou 2 pour z, a remplacer par une fft
        float r = 0, i = 0;
        for (int k = 0; k<numberOfValuesAfterZeroPadding; k++) {
            r = 0; i = 0;
            for (int n = 0; n<numberOfValues; n++) {
                r += values[index][n]*Math.cos(2*pi*k*n/numberOfValuesAfterZeroPadding);
                i -= values[index][n]*Math.sin(2*pi*k*n/numberOfValuesAfterZeroPadding);
            }
            tfd[k][0] = r;
            tfd[k][1] = i;
        }
        //Log.i("lucas", "on a calcule une tfd");
        //Log.i("lucas", "numberOfValues : " + String.valueOf(numberOfValues));
    }

    private final int indiceMaxiEntre(float t[][], int mini, int maxi) {
        //Log.i("lucas", "on calcule l'indice maxi");
        int k = 0;
        float m = -1;
        float temp = 0;
        for (int n = mini; n<maxi; n++) { //on part cherche les frequence dans un certain intervalle
            temp = t[n][0]*t[n][0]+t[n][1]*t[n][1];
            if (temp > m) {
                m = temp;
                k = n;
            }
        }
        return k;
    }

    public final void stop() {
        fini = true;
        acc.stop();
    }

    private final void computeOneStepTime(int index) {
        int periodeDansLeTableau = (int)(numberOfValues/(paceFrequency*tempsReleves));
        int decalageMax = 0;
        float maxi = 0;
        float[] derivee = derivee(values[index]);
        for (int decalage = 0, intercorrelation = 0, indice = 0; decalage<periodeDansLeTableau; decalage++) {
            for (int k = 0; (indice = numberOfValues-1 - (int) ((k * numberOfValues) / (paceFrequency * tempsReleves))) >= periodeDansLeTableau; k++) {
                //if (indice-decalage<0 || indice-decalage>=numberOfValues){
                //    Log.i("lucas", "nimporte quoi : " + String.valueOf(indice-decalage) + " et " + numberOfValues);
                //} else {
                    //intercorrelation += values[index][indice - decalage];
                intercorrelation += derivee[indice - decalage];
                //}
            }
            if (intercorrelation > maxi) {
                maxi = intercorrelation;
                decalageMax = decalage;
            }
        }
        oneStepTime = (long)(timeOfLastValue - decalageMax*periodeEchantillonage);
    }

    public final void manualSync() {
        manualSyncRequested = true;
    }

    private final void computeOneStepTime2(int index) {
        computeOneStepTime(index);
    }

    private final float max(float a, float b) {
        if (b>a) {
            return b;
        }
        return a;
    }

    private final float[] derivee(float[] tab) {
        float[] derivee = new float[tab.length];
        derivee[0] = tab[0];
        for (int i = 1; i<tab.length; i++) {
            derivee[i] = tab[i]-tab[i-1];
        }
        return derivee;
    }
}
