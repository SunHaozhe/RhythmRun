package com.telecom_paristech.pact25.rhythmrun.music;

import android.util.Log;


import com.telecom_paristech.pact25.rhythmrun.data.TempoDataBase;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.BufferSupplier;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.MusicManagerInterface;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.SongSpeedChanger;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFileException;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavProcess;


import java.io.IOException;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by lucas on 04/03/17.
 */


public class MusicManager implements MusicManagerInterface {

    private float[] paceFrequency;
    private final int l = 2; //longueur de paceFrequency
    private int indice; //indice courant dans le tableau circulaire paceFrequency
    private boolean premierTour;
    private String songPath;
    private float wantedTempoHz, songTempoHz;
    private boolean aSongIsSelected, playing;
    private Thread thread;
    private MusicReader musicReader;
    private final int dureeBuffer = 1;
    private final int bufferSize = 44100 * dureeBuffer;
    private Player player;
    TempoDataBase tempoDataBase;

    final float minRatioStep = 0.05f;
    final float maxRatioStep = 0.1f;
    final int kmax = 5; // voir loadNewTrack

    public MusicManager(TempoDataBase tempoDataBase) {
        paceFrequency = new float[l];
        indice = 0;
        premierTour = true;
        wantedTempoHz = -1;
        aSongIsSelected = false;
        playing = false;
        musicReader = new MusicReader(bufferSize);
        this.tempoDataBase = tempoDataBase;
    }

    private final void loadNewTrack() {
        while(wantedTempoHz<0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //TODO : lucas va s'en occupper
        //tenir une liste des moreaux deja joues (et penser a verifier les intervalles (je me comprends))
        PathAndTempo song = null;

        for(int k=0;k<kmax; k++) {
            if ((song = tempoDataBase.getSongThatFit((double)(wantedTempoHz-k*minRatioStep), (double)(wantedTempoHz+k*maxRatioStep))) != null) {
                break;
            }
            if ((song = tempoDataBase.getSongThatFit((double)(wantedTempoHz-k*minRatioStep)*2, (double)(wantedTempoHz+k*maxRatioStep)*2)) != null) {
                song.tempoHz /= 2;
                break;
            }
        }
        if (song == null) {
            song = tempoDataBase.getASong();
        }
        //lever une exception si la bdd est vide
        songPath = song.path;
        songTempoHz = song.tempoHz;
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
            variance += pow((paceFrequency[i]-moyenne), 2);
        }
        variance /= l;
        ecartType = sqrt(variance);

        if (ecartType <= moyenne / 10) {
            wantedTempoHz = (float)moyenne;
        }
        if (!aSongIsSelected) {
            loadNewTrack();
        }
        //Log.i("ComputeTempo classe", "Sortie de la classe");
    }

    public void updateRythm(float paceFrequency)
    {
        this.paceFrequency[indice] = paceFrequency;
        if (indice == l-1)
        {
            premierTour = false;
        }
        indice = (indice +1)%l;
        if (!premierTour)
        {
            computeTempo();
        }
    }

    public float getWantedTempoHz()
    {
        //Log.i("MusicManager","retour du tempo voulu");

        return wantedTempoHz;
    }

    public void play() {
        if (!playing) {
            player = new Player();
            thread = new Thread(player);
            thread.start();
        }
        playing = true;
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        playing = false;
    }

    public String getSongPath()
    {
        return songPath;
    }

    public void songEnded() {
        aSongIsSelected = false;
    }



    public static void readMusic(final String path){
        //TODO : remplacer path par l'attribut songPath

        new Thread(new Runnable() {
            @Override
            public void run() {
                WavProcess.wavRead(path);
                Log.i("READ","Fin de la procédure");
            }
        }).start();


    }

    private final BufferSupplier getNewBufferSupplier() {
        loadNewTrack();
        try {
            return new SongSpeedChanger(songPath, bufferSize, 1);//wantedTempoHz/songTempoHz
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) { //faire quelque chose si on ne peut pas ouvrir le fichier
            e.printStackTrace();
        }
        return null;
    }

    private class Player implements Runnable {
        MusicReader musicReader;
        BufferSupplier bufferSupplier;
        public Player() {
            musicReader = new MusicReader(bufferSize);
        }

        @Override
        public void run() {
            bufferSupplier = getNewBufferSupplier();
            Log.i("lucas", "buferSupplier loaded");
            musicReader.play();
            while(playing) {
                if(!bufferSupplier.songEnded()) {
                    if (musicReader.getNumberOfBuffers() < 2) {
                        musicReader.addBuffer(bufferSupplier.getNextBuffer());
                    }
                } else {
                    bufferSupplier = getNewBufferSupplier();
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            musicReader.stop();
        }

        public void pause() {
            musicReader.pause();
        }
    }

}
