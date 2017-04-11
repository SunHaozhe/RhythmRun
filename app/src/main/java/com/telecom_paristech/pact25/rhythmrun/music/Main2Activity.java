package com.telecom_paristech.pact25.rhythmrun.music;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/*
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
*/
import com.telecom_paristech.pact25.rhythmrun.R;
import com.telecom_paristech.pact25.rhythmrun.data.TempoDataBase;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.FastFourierTransform;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.SongSpeedChanger;
import com.telecom_paristech.pact25.rhythmrun.music.tempo.Tempo;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFileException;
import com.telecom_paristech.pact25.rhythmrun.sensors.Accelerometer;
import com.telecom_paristech.pact25.rhythmrun.sensors.Podometer;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


public class Main2Activity extends AppCompatActivity {

    Button test_button = null;
    Button button2 = null;
    Button button3 = null;
    TextView textview = null;
    private int k = 0;
    private Float pacefreq = 1.0f;
    Context context;
    boolean boutonPas = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        int optionsDeDebug = 7;
        //0 : ecrit les releves de l'accelerometre dans Downloads/donnees.csv
        //1 : affiche la frequence de pas calculee par le podometre
        //2 : calcule le tempo de la musique specifiee dans le thread
        //3 : joue beep sur les pas
        //4 : test de l'interpolation
        //5 : interpolation propre
        //6 : podometre + interpolation avec MusicManager
        //7 : database test

        test_button = (Button) findViewById(R.id.test_button);
        test_button.setText("L'accelerometre s'allume...");
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                k = 1;
            }
        });

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonPas = true;
            }
        });

        button2 = (Button) findViewById(R.id.button2);

        textview = (TextView) findViewById(R.id.textview);

        context = this;
        Log.d("lucas-File Reading", "on va verifier les permissions d ecriture");
        // Permission d'ecriture
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (permission != PackageManager.PERMISSION_GRANTED) {


            Log.d("lucas-File Reading", "on va demander à l'utilisateur");

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        Log.d("lucas-File", "on a les permissions d'écriture");


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("lucas-File", "on est rentrés dans le thread");
                Accelerometer acc = new Accelerometer(0.1f, 10, context);

                    File file = new File("/storage/emulated/0/Download/donnees.csv");
                    Log.i("lucas-File", "on a créé le file");
                    try {
                        //FileOutputStream fOS = new FileOutputStream(file);
                        //Log.i("lucas", "on a ouvert le fileoutputstream");
                        //OutputStreamWriter oSW = new OutputStreamWriter(fOS);
                        PrintWriter pw = new PrintWriter(file);
                        Log.i("lucas- File Reading", "on a ouvert le fileoutputstream");
                        String x, y, z;
                        boolean caacommence = false;
                        while (k == 0) {
                            if (acc.isActive()) {
                                if (caacommence == false) {
                                    caacommence = true;
                                    setTextViewToString("On écoute.");
                                }
                                x = String.valueOf(acc.getAx());
                                //y = String.valueOf(acc.getAy());
                                if (boutonPas) {
                                    y = "1";
                                    boutonPas = false;
                                } else {
                                    y = "0";
                                }
                                z = String.valueOf(acc.getAz());
                                //oSW.append(x + "," + y + "," + z + "\n");
                                pw.write(x + "," + y + "," + z + "\n");
                            }
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                            }
                        }
                        setTextViewToString("On écoute plus.");
                        Log.i("lucas- File reading", "on est sortis du while");
                        //oSW.flush();
                        //oSW.close();
                        //fOS.close();
                        pw.close();
                        } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
            }
        });
        class monRunnable implements Runnable {
            public monRunnable() {
            }

            public void run() {
                Log.i("lucas-monRunnable class", "on est rentrés dans le thread de main2activity");
                final Podometer pod = new Podometer(context);
                for (int i = 0; i<10 && !pod.isActive(); i++)
                {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (k == 0) {
                    setTextViewToFreq(pod.getRunningPaceFrequency());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                pod.stop();
            }
        }


        Thread thread2 = new Thread(new monRunnable());
        if (optionsDeDebug == 0) {
            thread1.start();
        }
        if (optionsDeDebug == 1) {
            thread2.start();
        }
        if (optionsDeDebug == 2) {
            double tempo = Tempo.findTempoHzFast("/storage/emulated/0/Download/guitare_mono_66bpm.wav");
            textview.setText("Bpm : " + String.valueOf(60*tempo));
        }
        if (optionsDeDebug == 3) {
            Thread thread3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.tut3);
                    MediaPlayer mediaPlayer2 = MediaPlayer.create(context, R.raw.tutoctave);
                    mediaPlayer.start();
                    final Podometer pod = new Podometer(context);
                    long beginning, step;
                    float periode, frequence;
                    boolean playing;
                    long timeToWaitBeforeSyncAgainMs = 5000;
                    while (!pod.isActive()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("lucas-monRunnable class", "pod actif");
                    beginning = SystemClock.elapsedRealtime();
                    playing = false;
                    frequence = pod.getRunningPaceFrequency();
                    periode = 1/frequence;
                    setTextViewToFreq(frequence);
                    step = pod.lastStepTimeSinceBoot();
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pod.manualSync();
                        }
                    });
                    while (k == 0) {
                        if (SystemClock.elapsedRealtime() > beginning + timeToWaitBeforeSyncAgainMs) {
                            mediaPlayer2.seekTo(0);
                            mediaPlayer2.start();
                            beginning = SystemClock.elapsedRealtime();
                            frequence = pod.getRunningPaceFrequency();
                            periode = 1/frequence;
                            setTextViewToFreq(frequence);
                            step = pod.lastStepTimeSinceBoot();
                            playing = false;
                        }
                            if (playing) {
                                try {
                                    Thread.sleep((long)(periode*1000));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.seekTo(0);
                                mediaPlayer.start();
                            }
                            else {
                                /*while (abs(SystemClock.elapsedRealtime()-step-(long)periode*1000/2)%(1000*periode) > 30) {
                                    Log.i("lucas", "on attend");
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }*/
                                playing = true;
                                mediaPlayer.seekTo(0);
                                try {
                                    Thread.sleep((long)(1000*periode)-((SystemClock.elapsedRealtime()-step)%(long)(1000*periode)));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();
                            }
                    }
                    mediaPlayer.release();
                    pod.stop();
                }

                private final long abs(long x) {
                    if (x<0) {
                        return -x;
                    }
                    return x;
                }
            });
            thread3.start();
        }

        if (optionsDeDebug == 4) {
            int dureeBuffer = 1;
            int bufferSize = 44100*dureeBuffer;
            String waveFilePath = "/storage/emulated/0/Download/guitare_mono_66bpm.wav";
            MusicReader musicReader = new MusicReader(bufferSize);
            WavFile waveFile = null;
            try {
                waveFile = WavFile.openWavFile(new File(waveFilePath));
                long numberOfFrames = waveFile.getNumFrames(), framesRemaining = numberOfFrames;

                float interpolationRatio = 1f;
                int numberOfFramesToReadAtATime = (int) (bufferSize/interpolationRatio);
                double[] bufferIn = new double[numberOfFramesToReadAtATime];
                boolean isPlaying = false;

                while (framesRemaining > 0) {
                    Log.i("lucas", "Tour de boucle.");
                    if (numberOfFramesToReadAtATime < framesRemaining) {
                        waveFile.readFrames(bufferIn, numberOfFramesToReadAtATime);
                        framesRemaining -= numberOfFramesToReadAtATime;
                        Log.i("lucas", "Une lecture.");
                    } else {
                        waveFile.readFrames(bufferIn, (int)framesRemaining);
                        Log.i("lucas", "Une derniere lecture.");
                        for (int i = (int)framesRemaining; i < numberOfFramesToReadAtATime; i++) {
                            bufferIn[i] = 0;
                        }
                        framesRemaining = 0;
                    }
                    musicReader.addBuffer(SongSpeedChanger.identity(bufferIn, bufferSize));
                    if (!isPlaying) {
                        musicReader.play();
                        isPlaying = true;
                        Log.i("lucas", "play");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (WavFileException e) {
                e.printStackTrace();
            }
            try {
                waveFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (optionsDeDebug == 5) {
            (new Thread (new Runnable() {
                @Override
                        public void run() {
                    int dureeBuffer = 1;
                    int bufferSize = 44100 * dureeBuffer;
                    String waveFilePath = "/storage/emulated/0/Download/guitare_mono_66bpm.wav";
                    MusicReader musicReader = new MusicReader(bufferSize);
                    SongSpeedChanger songSpeedChanger = null;
                    try {
                        boolean first = true;
                        songSpeedChanger = new SongSpeedChanger(waveFilePath, bufferSize, 1.1);
                        while ((!songSpeedChanger.songEnded())) {
                            if (musicReader.getNumberOfBuffers() < 2) {
                                musicReader.addBuffer(songSpeedChanger.getNextBuffer());
                            }
                            if (first) {
                                first = false;
                                musicReader.play();
                            }
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("lucas", "stopAtTheEnd Main2");
                        musicReader.stopAtTheEnd();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WavFileException e) {
                        e.printStackTrace();
                    }
                }
            })).start();
        }

        if (optionsDeDebug == 6) {
            (new Thread (new Runnable() {
                @Override
                public void run() {
                    Podometer pod = new Podometer(context);
                    while (!pod.isActive()) {
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    MusicManager musicManager = new MusicManager();
                    musicManager.play();
                    float f;
                    while(true) {
                        f = pod.getRunningPaceFrequency();
                        musicManager.updateRythm(f);
                        setTextViewToString(String.valueOf(musicManager.getWantedTempoHz()*60));
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    }
            })).start();
        }

        if (optionsDeDebug == 7) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    TempoDataBase tempoDataBase = new TempoDataBase(context);
                    Log.i("lucas", "database cree");
                    double tempo = Tempo.findTempoHzFast("/storage/emulated/0/Download/guitare_mono_66bpm.wav");
                    Log.i("lucas", "tempo trouve : " + String.valueOf(tempo));
                    tempoDataBase.addSongAndTempo("/storage/emulated/0/Download/guitare_mono_66bpm.wav", tempo);
                    Log.i("lucas", "tempo ajoute");
                    Log.i("lucas", String.valueOf(tempoDataBase.getTempo("/storage/emulated/0/Download/guitare_mono_66bpm.wav")));
                }
            })).start();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /*public int min(int a, long b) {
        if (a < b) {
            return a;
        }
        return (int)b;
    }*/


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
/*
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main2 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }*/

    /*@Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

    }*/

    @Override
    public void onStop() {
        super.onStop();

    }

    private final void setTextViewToFreq(float freq) {
        runOnUiThread(new RunnableForTextView(String.valueOf((int)(freq*60)) + "\nbpm"));
    }

    class RunnableForTextView implements Runnable {
        private String string;
        public RunnableForTextView(String string){
            this.string = string;
        }
        @Override
        public void run() {
            textview.setText(string);
        }
    }

    private final void setTextViewToString(String string) {
        runOnUiThread(new RunnableForTextView(string));
    }

}

