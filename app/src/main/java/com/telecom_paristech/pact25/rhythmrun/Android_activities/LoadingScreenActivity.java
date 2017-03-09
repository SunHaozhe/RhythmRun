package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;
import com.telecom_paristech.pact25.rhythmrun.data.TempoDataBase;

public class LoadingScreenActivity extends AppCompatActivity {

    private TextView loadingLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        loadingLabel = (TextView) findViewById(R.id.loadingTextView);

    }

    @Override
    public void onResume(){
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            requestPermissions();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }

    private void requestPermissions() throws InterruptedException {
        //Always requests permissions at the beginning
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i("Loading","Requesting permissions");

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Macros.PERMISSION_GLOBAL_REQUEST);
        } else {
            load();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("Loading","Got permission result.");
        switch (requestCode) {
            case Macros.PERMISSION_GLOBAL_REQUEST: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Loading","Permissions granted.");
                } else {
                    Log.d("Loading","Permissions denied.");
                }
            }
        }
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    load();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private void load() throws InterruptedException {
        Log.i("Loading","Initiating loading.");

        final Context context = this;

        Thread initMusicManager = new Thread(new Runnable() {
            @Override
            public void run() {
                MusicManager.init();
            }
        });


        Thread initMusicDatabase = new Thread(new Runnable() {
            @Override
            public void run() {
                HomeActivity.initDB(context);
            }
        });

        loadingLabel.setText("Loading songs database...");
        initMusicDatabase.start();
        initMusicDatabase.join();

        //HomeActivity.initDB(context);

        loadingLabel.setText("Loading music files...");
        initMusicManager.start();
        initMusicManager.join();

        goToHome();

    }


    public void goToHome(){
        Log.i("Loading","Done loading. Going to home.");
        startActivity(new Intent(this, HomeActivity.class));
    }


}
