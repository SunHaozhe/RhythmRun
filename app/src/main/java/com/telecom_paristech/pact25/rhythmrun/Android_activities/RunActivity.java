package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Chronometer;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.telecom_paristech.pact25.rhythmrun.data.TempoDataBase;
import com.telecom_paristech.pact25.rhythmrun.interfaces.music.MusicManagerInterface;
import com.telecom_paristech.pact25.rhythmrun.music.MusicReader;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.SongSpeedChanger;
import com.telecom_paristech.pact25.rhythmrun.music.tempo.Tempo;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFileException;
import com.telecom_paristech.pact25.rhythmrun.sensors.Podometer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("deprecation")
public class RunActivity extends AppCompatActivity {

    private long timeAtStop;
    private boolean isRunning=false;
    private boolean isDrawerExpanded=false;

    private RunMapFragment runMapFragment;
    private TextView tvDistance;
    private TextView tvPace;
    private TextView tvHeartRate;
    private TextView tvBPM;
    private TextView tvCurrentSong;
    private TextView tvHeartMessage;

    private HeartBeatCoach heartBeatCoach;

    private Podometer podometer=null;

    //private MusicManagerInterface musicManagerInterface;

    private com.telecom_paristech.pact25.rhythmrun.music.MusicManager musicManager;
    private TempoDataBase tempoDataBase;

    private Timer t;

    private ArrayList<RunStatus> runData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Run","Creating activity Run.");
        long t = System.currentTimeMillis();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        runMapFragment = (RunMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentRun);

        tvDistance = (TextView) findViewById(R.id.runDistance);
        tvPace = (TextView) findViewById(R.id.runPace);
        tvHeartRate = (TextView) findViewById(R.id.runHeartRate);
        tvBPM = (TextView) findViewById(R.id.runBPM);
        tvCurrentSong = (TextView) findViewById(R.id.runCurrentSong);
        tvHeartMessage = (TextView) findViewById(R.id.heartBeatMessage);

        //Initializing TextViews
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String paceMode = sharedPreferences.getString("pace","p");
        final String unit = sharedPreferences.getString("unit","km");
        RunStatus lastStatus = new RunStatus(0, null, new Distance(0),0, new Pace(0));
        tvDistance.setText(lastStatus.distance.toStr(unit,true));
        tvPace.setText(lastStatus.pace.toStr(unit,paceMode,true));
        tvHeartRate.setText(String.format(getString(R.string.run_heart_rate),lastStatus.heartRate));

        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        final FloatingActionButton buttonStart = (FloatingActionButton) findViewById(R.id.runPlay);
        final FloatingActionButton buttonStop = (FloatingActionButton) findViewById(R.id.runStop);

        //Reading the possible intent from Recap.
        Intent intent = getIntent();
        if(intent != null){
            final CustomPolylineOptions itinerary = intent.getParcelableExtra(Macros.EXTRA_ITINERARY);
            if(itinerary != null){
                Log.d("Run","Found an itinerary in the intent.");
                runMapFragment.drawnPolyline(itinerary.getPolylineOptions());
            }
            //MusicManager.playCurrentSong();
        }

        //Setting up the play/pause button
        buttonStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) { //Pausing the run
                    //Saving the chronometer value when stopped.
                    timeAtStop = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();

                    //Displaying the play drawable.
                    buttonStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));

                    //Animation to show the stop button.
                    TranslateAnimation a = new TranslateAnimation(0, 0, (float) 1.5 * buttonStop.getHeight(), 0);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            buttonStop.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    a.setInterpolator(new AccelerateInterpolator());
                    a.setDuration(300);
                    buttonStop.startAnimation(a);

                    Log.i("Run","Pausing the run at "+timeAtStop);

                } else { //Resuming the run

                    //Resuming the chronometer. Needs to be rebased with the time at stop to be accurate.
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeAtStop);
                    chronometer.start();

                    //Displaying the pause drawable.
                    buttonStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));

                    //Animation to hide the stop button.
                    TranslateAnimation a = new TranslateAnimation(0, 0, 0, (float) 1.5 * buttonStop.getHeight());
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            buttonStop.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    a.setInterpolator(new AccelerateInterpolator());
                    a.setDuration(300);
                    buttonStop.startAnimation(a);

                    Log.i("Run","Resuming the run.");
                }

                //Inverting the status of the run.
                isRunning = !isRunning;
            }
        });
        buttonStart.bringToFront(); //Play button is on top of stop button.

        buttonStop.setVisibility(View.VISIBLE);
        buttonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Run","Creating intent to SumUp activity. End of run.");
                Intent intent = new Intent(view.getContext(), SumUpActivity.class);
                intent.putExtra(Macros.EXTRA_ROUTE,new CustomPolylineOptions(getRoute()));
                intent.putParcelableArrayListExtra(Macros.EXTRA_RUN_DATA,runData);

                //Reset of static Run data
                runMapFragment.reset();


                startActivity(intent);
            }
        });

        //Getting the screen height in pixels for the animation of the bottom drawing panel.
        Point size = new Point(); //Just a container with x and y public attributes.
        getWindowManager().getDefaultDisplay().getSize(size); //Getting the screen dimensions.
        final int screenHeight = size.y;
        Log.v("Run","Found screen height: "+screenHeight+" px.");

        final View fragmentView = findViewById(R.id.fragmentRun); //The map view
        final View dataView = findViewById(R.id.dataView); //The drawing panel
        final ImageButton imageButton =
                (ImageButton) findViewById(R.id.runToggleButton); //The up arrow that toggles the drawing panel

        //Setting up the toggling animation
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isDrawerExpanded = !isDrawerExpanded;

                if(isDrawerExpanded){ //Expanding the drawer
                    Log.d("Run","Expanding the bottom drawer");

                    Animation a = ViewAnimationUtils.expand(dataView); //Creation of the animation
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Log.v("Run","End of expanding animation, resizing the map view.");
                            //Adapting the height of the map view
                            fragmentView.getLayoutParams().height=screenHeight-ViewAnimationUtils.dpToPx(ViewAnimationUtils.expandedHeight);
                            fragmentView.requestLayout();

                            //Changing the drawable to a down arrow
                            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_down_arrow));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    dataView.startAnimation(a);
                } else { //Collapsing the drawer
                    Log.d("Run","Collapsing the bottom drawer");

                    Animation a = ViewAnimationUtils.collapse(dataView); //Creation of the animation
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //Resizing the map view to full height
                            fragmentView.getLayoutParams().height=screenHeight;
                            fragmentView.requestLayout();
                            Log.v("Run","Beginning of collapsing animation, map view resized.");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Changing the drawable to an up arrow.
                            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_less_black_24dp));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    dataView.startAnimation(a);
                }
            }
        });

        //Initialization of run data.
        runData = new ArrayList<>();

        final Context context = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                podometer = new Podometer(context);
                while(!podometer.isActive()){
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        t = System.currentTimeMillis() - t;
        Log.d("Run","End of RunActivity creation. Took "+t+" ms.");

    }

    @Override
    public void onResume(){
        super.onResume();

        heartBeatCoach = new HeartBeatCoach(this);

        //Creation of a timer for collecting run data and updating the display.
        //As it is cancelled when the activity is paused, it needs to be reset when resuming.
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //As we're updating views, we need to execute the code on the main UI thread.
                RunActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        },0,Macros.UPDATE_IDLE);



        (new Thread(new Runnable() {
            @Override
            public void run() {
                // This database contains all songs in /storage/emulated/0/Music
                tempoDataBase = HomeActivity.getDB();

                musicManager = new com.telecom_paristech.pact25.rhythmrun.music.MusicManager(tempoDataBase, true);
                Log.i("RunActivity", "MusicManager created.");

                musicManager.play();
                Log.i("RunActivity", "MusicManager plays.");
            }
        })).start();

    }

    @Override
    public void onPause(){
        t.cancel(); //Stopping the run updates.
        super.onPause();
    }

    @Override
    public void onStop(){
        runMapFragment.stopLocationUpdates(); //Stopping location updates.

        // Ending music playing
        musicManager.stop();
        tempoDataBase.close();

        if(podometer != null)
            podometer.stop();
        super.onStop();
    }

    private int max_int(int a, int b){
        if(a>=b)
            return a;
        return b;
    }

    public Pace getPace(){
        double distance = 0;
        double time = 0;

        if(runData.size()==0)
            return new Pace(0);

        RunStatus lastRun = runData.get(runData.size()-1);
        for (int i=runData.size()-2; i >= max_int(runData.size()-6, 0); i--){
            float[] results = new float[1];
            if (lastRun.location != null && runData.get(i).location != null){
                Location.distanceBetween(
                        lastRun.location.latitude,
                        lastRun.location.longitude,
                        runData.get(i).location.latitude,
                        runData.get(i).location.longitude,
                        results);
                distance += results[0];
            }
            time += lastRun.time - runData.get(i).time;
            lastRun = runData.get(i);
        }

        return new Pace(time/distance/60);
    }

    public void update(){
        /*
            Two tasks handled by update:
             -Adding the current run data to memory
             -Updating the display of those info
         */

        if(isRunning) {
            Pace pace = getPace();
            runData.add(new RunStatus(
                    getElapsedTime(),
                    getPosition(),
                    getDistance(),
                    getHeartRate(),
                    pace
            ));
            //musicManagerInterface.updateRythm(getHeartRate());
            if(pace.getValue() < 15) // Not updating rhythm under 4 km/h
                musicManager.updateRythm(getRunnerRhythm());
            Log.v("Run","Updating the run status while running is "+isRunning+" ("+runData.size()+" points collected).");
            updateDisplay();
        }
    }

    public void updateDisplay(){
        Log.v("Run","Updating display of run info.");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String paceMode = sharedPreferences.getString("pace","p");
        final String unit = sharedPreferences.getString("unit","km");

        //At the beginning, no status has been recorded. Showing blank information.
        RunStatus lastStatus = new RunStatus(0, null, new Distance(0),0, new Pace(0));
        if(runData.size()>0){
            lastStatus = runData.get(runData.size()-1);
        }

        tvDistance.setText(lastStatus.distance.toStr(unit,true));
        tvPace.setText(lastStatus.pace.toStr(unit,paceMode,true));
        tvHeartRate.setText(String.format(getString(R.string.run_heart_rate),lastStatus.heartRate));

        tvBPM.setText(String.format(getString(R.string.run_bpm),getBPM()));
        tvCurrentSong.setText(getCurrentSong());

        tvHeartMessage.setText(heartBeatCoach.messageDuringRun());
    }

    private Distance getDistance(){
        /*
            Returns the traveled distance at this time.
         */
        return runMapFragment.getDistance();
	}

    private LatLng getPosition(){
        /*
            Returns the current location, or null if not is found.
         */
        return runMapFragment.getPosition();
    }

    private double getElapsedTime(){
        /*
            Returns the run time in milliseconds.
         */
        Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        return SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    private float getHeartRate(){
        /*
            Fetches the heart rate from the Bluetooth belt.
         */
        if(podometer == null)
            return -1;
        return podometer.getRunningPaceFrequency()*60;
    }

    private float getRunnerRhythm(){
        if(podometer == null)
            return 1;
        return podometer.getRunningPaceFrequency();
    }

    private String getCurrentSong(){
        return MusicManager.getCurrentSong().getString();
    }

    private int getBPM(){
        return 68;
    }

    private PolylineOptions getRoute(){
        /*
            Just a way to get the reference of the polyline from the map fragment.
         */
        return runMapFragment.getJourneyPolylineOptions();
    }

}
