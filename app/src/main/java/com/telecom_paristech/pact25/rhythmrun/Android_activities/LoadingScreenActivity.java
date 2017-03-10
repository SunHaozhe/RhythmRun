package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.Client_serveur.login.SessionConfiguration;
import com.telecom_paristech.pact25.rhythmrun.R;
import com.telecom_paristech.pact25.rhythmrun.data.TempoDataBase;

public class LoadingScreenActivity extends AppCompatActivity {

    private TextView loadingLabel;

    private boolean isTempoDatabaseLoaded = false;
    private boolean areMusicFilesLoaded = false;
    private boolean isConnectionChecked = false;

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
                    Thread.sleep(100);
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

        new TaskLoadTempoDatabase().execute(this);
        new TaskCheckIfConnected().execute(this);

    }


    public void goToHome(){
        if(isTempoDatabaseLoaded && areMusicFilesLoaded && isConnectionChecked){
            Log.i("Loading","Done loading. Going to home.");
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    private class TaskLoadTempoDatabase extends AsyncTask<Context, Void, Void> {
        private long t;

        @Override
        protected void onPreExecute(){
            t = System.currentTimeMillis();
            loadingLabel.setText("Loading tempo database...");
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            HomeActivity.initDB(contexts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            t = System.currentTimeMillis() - t;
            Log.d("Loading","Done loading tempo database in "+t+" ms.");
            loadingLabel.setText("Loading tempo database... Done.");
            isTempoDatabaseLoaded = true;
            new TaskLoadMusicFiles().execute();
        }

    }

    private class TaskLoadMusicFiles extends AsyncTask<Void, Void, Void>{
        private long t;

        @Override
        protected void onPreExecute(){
            t = System.currentTimeMillis();
            loadingLabel.setText("Loading music files...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MusicManager.init();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            t = System.currentTimeMillis() - t;
            Log.d("Loading","Done loading music files in "+t+" ms.");
            areMusicFilesLoaded = true;
            loadingLabel.setText("Loading music files... Done.");
            goToHome();
        }

    }

    private class TaskCheckIfConnected extends AsyncTask<Context, Void, Boolean>{
        private long t;

        @Override
        protected void onPreExecute(){
            t = System.currentTimeMillis();
            loadingLabel.setText("Checking if already logged in...");
        }

        @Override
        protected Boolean doInBackground(Context... contexts) {
            SessionConfiguration session = new SessionConfiguration(getApplicationContext());
            return session.isLoggedIn();
        }

        @Override
        protected  void onPostExecute(Boolean bool){
            Log.d("Loading","Session connection is "+bool);
            t = System.currentTimeMillis() - t;
            Log.d("Loading","Done checking connection in "+t+" ms.");
            isConnectionChecked = true;
            loadingLabel.setText("Checking if already logged in... Done.");
            goToHome();
        }
    }


}
