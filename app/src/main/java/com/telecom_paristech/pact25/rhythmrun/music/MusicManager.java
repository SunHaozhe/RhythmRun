package com.telecom_paristech.pact25.rhythmrun.music;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;


import com.telecom_paristech.pact25.rhythmrun.interfaces.music.MusicManagerInterface;

import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavProcess;


import java.io.File;
import java.io.IOException;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by lucas on 04/03/17.
 */


public class MusicManager implements MusicManagerInterface {

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
        //TODO :
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



    public static void readMusic(final String path){
        //TODO : remplacer path par l'attribut songPath

        new Thread(new Runnable() {
            @Override
            public void run() {
                int compteur = new WavProcess(path).wavRead();
                Log.i("READ","Fin de la procédure");

                MediaPlayer player = new MediaPlayer();
                for (int i = 0; i<compteur;i++) {
                    try {
                        player.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/Rhythm Run Samples/modified sample "
                                + String.valueOf(i) + ".wav");
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(25000/441);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player.stop();
                    player.release();
                }
            }
        }).start();


    }

}
