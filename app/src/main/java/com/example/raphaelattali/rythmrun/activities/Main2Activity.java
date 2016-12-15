package com.example.raphaelattali.rythmrun.activities;

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

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.sensors.Accelerometer;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
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
    int k = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        test_button = (Button) findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test_button.setText("hop");
                k = 1;
            }
        });

        final Context mContext = this;
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


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("lucas", "on est rentrés dans le thread");
                Accelerometer acc = new Accelerometer(0.1f, 10, mContext);

                    File file = new File("/storage/emulated/0/Download/donnees.csv");
                    Log.i("lucas", "on a créé le file");
                    try {
                        FileOutputStream fOS = new FileOutputStream(file);
                        Log.i("lucas", "on a ouvert le fileoutputstream");
                        OutputStreamWriter oSW = new OutputStreamWriter(fOS);

                        Log.i("lucas", "on a ouvert l'outputstreamwriter");
                        int c = 0;
                        int m = 0;
                        String x, y, z;

                        while (k == 0&&c<1000&&m<300000) {
                            if (acc.isActive()) {
                                c++;
                                x = String.valueOf(acc.getAx());
                                y = String.valueOf(acc.getAy());
                                z = String.valueOf(acc.getAz());
                                oSW.append(x + "," + y + "," + z + "\n");
                            }
                            m++;
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                            }
                        }
                        Log.i("lucas", "on est sortis du while");
                        oSW.flush();
                        oSW.close();
                        fOS.close();
                        } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
        thread.start();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
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
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
