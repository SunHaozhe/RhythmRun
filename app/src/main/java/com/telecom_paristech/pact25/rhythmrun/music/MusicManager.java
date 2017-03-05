package com.telecom_paristech.pact25.rhythmrun.music;

import android.util.Log;

import java.io.File;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by lucas on 04/03/17.
 */

public class MusicManager {
    private float[] paceFrequency;
    private final int l = 5; //longueur de paceFrequency
    int indice; //indice courant dans le tableau circulaire paceFrequency
    boolean premierTour;
    String songPath;
    float wantedTempoHz, songTempoHz;
    boolean trackPlaying;

    public MusicManager() {
        paceFrequency = new float[l];
        indice = 0;
        premierTour = true;
        wantedTempoHz = -1;
        trackPlaying = false;
    }

    private final void loadNewTrack() {
        float[][] tempoIntervalsHz = {{wantedTempoHz*0.9f, wantedTempoHz*1.1f}, {2*wantedTempoHz*0.9f, 2*wantedTempoHz*1.1f}};
        String songPath = ""; //a completer avec la BDD en utilisant la ligne precedente si possible
        //(on veut le chemin d'acces d'une musique de la bdd avec un tempo dans les intervalles de tempoIntervalsHz)
        songTempoHz = -1; //pareil
    }

    private void computeTempo()
    {
        double ecartType = 0, variance = 0; // on met a jour le tempo si l'ecart type des frequences de pas relevees est petite
        double moyenne = 0;
        for (int i = 0; i<l; i++) {
            moyenne += paceFrequency[i];
        }
        moyenne /= l;
        for (int i = 0; i<l; i++) {
            variance += pow((double)(paceFrequency[i]-moyenne), 2);
        }
        variance /= l;
        ecartType = sqrt(variance);

        if (ecartType <= moyenne / 10) {
            wantedTempoHz = (float)moyenne;
        }
        if (!trackPlaying) {
            loadNewTrack();
        }
        Log.i("ComputeTempo classe", "Sortie de la classe");
    }

    public void updateRythm(float paceFrequency)
    {
        this.paceFrequency[indice] = paceFrequency;
        if (indice == l-1)
        {
            premierTour = false;
        }
        indice = (indice +1)%l;
        if (premierTour)
        {
            computeTempo();
            Log.i("MusicManager","premier tour in the updateRythm");
        }
    }

    public float getWantedTempo()
    {
        Log.i("MusicManager","retour du tempo voulu");
        return wantedTempoHz;
    }

    public String getSongPath()
    {
        return songPath;
    }

    public void songEnded() {
        trackPlaying = false;
    }
}
