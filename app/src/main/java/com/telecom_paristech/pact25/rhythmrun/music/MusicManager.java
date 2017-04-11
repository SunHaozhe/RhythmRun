package com.telecom_paristech.pact25.rhythmrun.music;

import android.util.Log;


import com.telecom_paristech.pact25.rhythmrun.interfaces.music.ByteBufferPool;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.ByteBufferSupplier;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.FloatArrayPool;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.FloatArraySupplier;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.MusicManagerInterface;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.NativeVocoder;
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
    private final int l = 2; //longueur de paceFrequency, i.e. le nombre de valeurs prises en compte pour le calcul du tempo voulu
    private int indice; //indice courant dans le tableau circulaire paceFrequency
    private boolean premierTour;
    private String songPath;
    private float wantedTempoHz, songTempoHz;
    private boolean aSongIsSelected, playing;
    private Thread thread;
    //private MusicReader musicReader;
    private final int dureeBuffer = 1;
    private final int bufferSize = 44100 * dureeBuffer;
    private Player player;
    private boolean CVocoder = true;

    public MusicManager(boolean CVocoder) {
        paceFrequency = new float[l];
        indice = 0;
        premierTour = true;
        wantedTempoHz = -1;
        aSongIsSelected = false;
        playing = false;
        this.CVocoder = CVocoder;
        //musicReader = new MusicReader(bufferSize, 0);
    }

    private final void loadNewTrack() {
        while(wantedTempoHz<0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        float[][] tempoIntervalsHz = {{wantedTempoHz*0.9f, wantedTempoHz*1.1f}, {2*wantedTempoHz*0.9f, 2*wantedTempoHz*1.1f}}; //par ex
        //TODO :
        //tenir une liste des moreaux deja joues
        songPath = ""; //a completer avec la BDD en utilisant la ligne precedente si possible
        //(on veut le chemin d'acces d'une musique de la bdd avec un tempo dans les intervalles de tempoIntervalsHz)
        songTempoHz = -1; //pareil

        songPath = "/storage/emulated/0/Download/guitare_mono_70bpm.wav"; // pour tester
        songTempoHz = 1.1f;
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

    private final FloatArraySupplier getNewFloatArraySupplier() {
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

    private final ByteBufferSupplier getNewByteBufferSupplier() {
        loadNewTrack();
        try {
            return new NativeVocoder(songPath, bufferSize, 3);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) { //faire quelque chose si on ne peut pas ouvrir le fichier
            e.printStackTrace();
        }
        return null;
    }

    private class Player implements Runnable {
        MusicReader musicReader;
        FloatArraySupplier floatArraySupplier = null;
        ByteBufferSupplier byteBufferSupplier = null;
        public Player() {
            if (CVocoder) {
                musicReader = new MusicReader(bufferSize, 1);
            } else {
                musicReader = new MusicReader(bufferSize, 0);
            }
        }

        @Override
        public void run() {
            if (CVocoder) {
                byteBufferSupplier = getNewByteBufferSupplier();
                musicReader.setByteBufferPool((ByteBufferPool)byteBufferSupplier); //pour retourner les buffers, pour ne pas en allouer d'autres
            } else {
                floatArraySupplier = getNewFloatArraySupplier();
                musicReader.setFloatArrayPool((FloatArrayPool)floatArraySupplier); //pour retourner les buffers, pour ne pas en allouer d'autres
            }
            Log.i("lucas", "bufferSupplier loaded");
            musicReader.play();
            while(playing) {
                if (CVocoder) {
                    if (!byteBufferSupplier.songEnded()) {
                        if (musicReader.getNumberOfBuffers() < 2) {
                            musicReader.addBuffer(byteBufferSupplier.getNextBuffer());
                        }
                    } else {
                        while (musicReader.getNumberOfBuffers() > 0) { //on attend que tous les buffers aient ete retournes (sinon fuite de memoire...)
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        byteBufferSupplier = getNewByteBufferSupplier();
                        musicReader.setByteBufferPool((ByteBufferPool)byteBufferSupplier);
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!floatArraySupplier.songEnded()) {
                        if (musicReader.getNumberOfBuffers() < 2) {
                            musicReader.addBuffer(floatArraySupplier.getNextBuffer());
                        }
                    } else {
                        floatArraySupplier = getNewFloatArraySupplier();
                        musicReader.setFloatArrayPool((FloatArrayPool)floatArraySupplier);
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            musicReader.stop();
        }

        public void pause() {
            musicReader.pause();
        }
    }

}
