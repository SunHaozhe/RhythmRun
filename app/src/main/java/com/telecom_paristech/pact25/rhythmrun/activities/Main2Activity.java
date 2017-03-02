package com.telecom_paristech.pact25.rhythmrun.activities.;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;
import com.telecom_paristech.pact25.rhythmrun.music.tempo.Tempo;
import com.telecom_paristech.pact25.rhythmrun.sensors.Accelerometer;
import com.telecom_paristech.pact25.rhythmrun.sensors.Podometer;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DOCUMENTS;


public class Main2Activity extends AppCompatActivity {

    Button test_button = null;
    TextView textview = null;
    private int k = 0;
    private Float pacefreq = 1.0f;
    Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        int optionsDeDebug = 2; //0 : accelerometre, 1 : tester podo, 3 : tempo musique

        test_button = (Button) findViewById(R.id.test_button);
        test_button.setText("L'accelerometre s'allume...");
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                k = 1;
            }
        });

        textview = (TextView) findViewById(R.id.textview);

        context = this;
        Log.d("lucas", "on va verifier les permissions d ecriture");
        // Permission d'ecriture
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (permission != PackageManager.PERMISSION_GRANTED) {


            Log.d("lucas", "on va demander à l'utilisateur");

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        Log.d("lucas", "on a les permissions d'écriture");


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("lucas", "on est rentrés dans le thread");
                Accelerometer acc = new Accelerometer(0.1f, 10, context);

                    File file = new File("/storage/emulated/0/Download/donnees.csv");
                    Log.i("lucas", "on a créé le file");
                    try {
                        //FileOutputStream fOS = new FileOutputStream(file);
                        //Log.i("lucas", "on a ouvert le fileoutputstream");
                        //OutputStreamWriter oSW = new OutputStreamWriter(fOS);
                        PrintWriter pw = new PrintWriter(file);
                        Log.i("lucas", "on a ouvert le fileoutputstream");
                        String x, y, z;
                        while (k == 0) {
                            if (acc.isActive()) {
                                x = String.valueOf(acc.getAx());
                                y = String.valueOf(acc.getAy());
                                z = String.valueOf(acc.getAz());
                                //oSW.append(x + "," + y + "," + z + "\n");
                                pw.write(x + "," + y + "," + z + "\n");
                            }
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                            }
                        }
                        Log.i("lucas", "on est sortis du while");
                        //oSW.flush();
                        //oSW.close();
                        //fOS.close();
                        pw.close();
                        } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
        class monRunnable implements Runnable {
            public monRunnable() {
            }

            public void run() {
                Log.i("lucas", "on est rentrés dans le thread de main2activity");
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

    }

    private final void setTextViewToFreq(float freq) {
        runOnUiThread(new RunnableForTextView(freq));
    }

    class RunnableForTextView implements Runnable {
        private float freq;
        public RunnableForTextView(float freq){
            this.freq = freq;
        }
        @Override
        public void run() {
            textview.setText(String.valueOf(freq));
        }
    }

}

