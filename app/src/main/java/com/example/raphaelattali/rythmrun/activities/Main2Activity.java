package com.example.raphaelattali.rythmrun.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        // Permission d'ecriture
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Ca va pas marcher
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Accelerometer acc = new Accelerometer(0.1f, 10, mContext);
                OutputStreamWriter o;
                boolean f = false;
                if (f) {
                    try {
                        o = new OutputStreamWriter(mContext.openFileOutput("donnees.txt", Context.MODE_WORLD_READABLE));


                        while (k == 0) {
                            final String x = String.valueOf(acc.getAx());
                            o.write(x);
                            o.write(" ");
                            test_button.post(new Runnable() {
                                @Override
                                public void run() {
                                    test_button.setText(x);
                                }


                            });
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                            }
                        }
                        o.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                        test_button.post(new Runnable() {
                            @Override
                            public void run() {
                                test_button.setText("fait1");
                            }
                        });
                        File path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS);
                        File file = new File(path, "donnees.txt");

                        test_button.post(new Runnable() {
                            @Override
                            public void run() {
                                test_button.setText("fait2");
                            }
                        });
                        try {
                            //file.mkdirs();
                            //OutputStream os = new FileOutputStream(file);
                            PrintWriter pw = new PrintWriter(file);
                            //byte[] bytes;

                            test_button.post(new Runnable() {
                                @Override
                                public void run() {
                                    test_button.setText("fait3");
                                }
                            });
                            while (k == 0) {
                                final String x = String.valueOf(acc.getAx());
                                //bytes = x.getBytes();
                                //os.write(bytes);
                                //os.write(" ".getBytes());


                                try {
                                    Thread.sleep(30);
                                } catch (InterruptedException e) {
                                }
                            }

                            test_button.post(new Runnable() {
                                @Override
                                public void run() {
                                    test_button.setText("fait4");
                                }
                            });

                            //os.flush();
                            //os.close();
                            pw.close();
                            } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
