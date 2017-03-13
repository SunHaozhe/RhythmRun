package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.telecom_paristech.pact25.rhythmrun.Client_serveur.login.SessionConfiguration;
import com.telecom_paristech.pact25.rhythmrun.R;
import com.telecom_paristech.pact25.rhythmrun.data.TempoDataBase;

public class LoadingScreenActivity extends AppCompatActivity {

    private TextView loadingLabel;

    private boolean isTempoDatabaseLoaded = false;
    private boolean areMusicFilesLoaded = false;
    private boolean isConnectionChecked = false;
    private boolean areRunsLoaded = false;
    private boolean isDummyMapLoaded = false;

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
        new TaskLoadRuns().execute(this);
        new TaskCheckIfConnected().execute(this);

        /*
            INITIALIZATION OF A DUMMY MAP
            =============================
            The Play Service APIs have to load a client service and a package service.
            Unfortunately, the "package" one takes about a second to load and using the
            MapsInitializer only will get us the "client".
            So here we init a dummy map, so that maps created later load faster.
         */
        MapFragment dummyMapInitializer = (MapFragment) getFragmentManager().findFragmentById(R.id.loadingDummyMap);
        dummyMapInitializer.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("Loading", "Dummy map loaded.");
                isDummyMapLoaded = true;
                goToHome();
            }
        });

    }


    public void goToHome(){
        if(isTempoDatabaseLoaded && areMusicFilesLoaded && isConnectionChecked && areRunsLoaded && isDummyMapLoaded){
            Log.i("Loading","Done loading. Going to home.");
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    private abstract class LoadingTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
        private long t;
        private String text;

        public LoadingTask(String text){
            this.text = text;
        }

        @Override
        protected  void onPreExecute(){
            t = System.currentTimeMillis();
            loadingLabel.setText(text);
        }

        @Override
        protected void onPostExecute(Result result){
            t = System.currentTimeMillis() - t;
            loadingLabel.setText(text+" Done.");
            Log.d("Loading",text+" finished in "+t+" ms.");
        }

    }

    private class TaskLoadTempoDatabase extends LoadingTask<Context, Void, Void> {

        TaskLoadTempoDatabase(){
            super("Loading tempo database...");
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            HomeActivity.initDB(contexts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            super.onPostExecute(voids);
            isTempoDatabaseLoaded = true;
            new TaskLoadMusicFiles().execute();
        }

    }

    private class TaskLoadMusicFiles extends LoadingTask<Void, Void, Void>{

        TaskLoadMusicFiles(){
            super("Loading music files...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MusicManager.init();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            super.onPostExecute(voids);
            areMusicFilesLoaded = true;
            goToHome();
        }

    }

    private class TaskCheckIfConnected extends LoadingTask<Context, Void, Boolean>{

        TaskCheckIfConnected(){
            super("Checking connection...");
        }

        @Override
        protected Boolean doInBackground(Context... contexts) {
            SessionConfiguration session = new SessionConfiguration(getApplicationContext());
            return session.isLoggedIn();
        }

        @Override
        protected  void onPostExecute(Boolean bool){
            super.onPostExecute(bool);
            isConnectionChecked = true;
            goToHome();
        }
    }

    private class TaskLoadRuns extends LoadingTask<Context, Void, Void>{
        TaskLoadRuns(){
            super("Loading runs...");
        }

        @Override
        protected Void doInBackground(Context... contexts){
            new DataManager(contexts[0]); //Automatically loads runs and weekly runs
            return null;
        }

        @Override
        protected void onPostExecute(Void voids){
            super.onPostExecute(voids);
            areRunsLoaded = true;
            goToHome();
        }

    }

    //TODO: load podometer
    //TODO : Handle connection result


}
