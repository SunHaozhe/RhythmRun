package com.example.raphaelattali.rythmrun.Android_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Chronometer;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Timer;
import java.util.TimerTask;

public class RunActivity extends AppCompatActivity {

    public static final int UPDATE_IDLE = 1000; //ms

    public static final String EXTRA_DISTANCE = "distance";
    public static final String EXTRA_PACE = "pace";
    public static final String EXTRA_ROUTE = "route";
    public static final String EXTRA_TIME = "time";

    private long timeAtStop;
    private boolean isRunning=false;
    private boolean isDrawerExpanded=false;

    private RunMapFragment runMapFragment;
    private TextView tvDistance;
    private TextView tvPace;
    private TextView tvHeartRate;
    private TextView tvBPM;
    private TextView tvCurrentSong;

    private double distance;
    private double pace;
    private double heartRate;
    private double elapsedTime; //s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        runMapFragment = (RunMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentRun);

        tvDistance = (TextView) findViewById(R.id.runDistance);
        tvPace = (TextView) findViewById(R.id.runPace);
        tvHeartRate = (TextView) findViewById(R.id.runHeartRate);
        tvBPM = (TextView) findViewById(R.id.runBPM);
        tvCurrentSong = (TextView) findViewById(R.id.runCurrentSong);

        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
        final FloatingActionButton buttonStart = (FloatingActionButton) findViewById(R.id.runPlay);
        final FloatingActionButton buttonStop = (FloatingActionButton) findViewById(R.id.runStop);

        Intent intent = getIntent();
        if(intent != null){
            final CustomPolylineOptions itinerary = intent.getParcelableExtra(NewRunActivity.EXTRA_ITINERARY);
            if(itinerary != null)
                runMapFragment.drawnPolyline(itinerary.getPolylineOptions());
        }

        buttonStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    timeAtStop = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop();
                    buttonStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
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
                } else {
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeAtStop);
                    chronometer.start();
                    buttonStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
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
                }
                isRunning = !isRunning;
            }
        });
        buttonStart.bringToFront();

        buttonStop.setVisibility(View.VISIBLE);
        buttonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SumUpActivity.class);
                intent.putExtra(EXTRA_DISTANCE,distance);
                intent.putExtra(EXTRA_PACE,pace);
                intent.putExtra(EXTRA_ROUTE,new CustomPolylineOptions(getRoute()));
                intent.putExtra(EXTRA_TIME,elapsedTime);
                startActivity(intent);
            }
        });

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        final int screenHeight = size.y;
        final View fragmentView = findViewById(R.id.fragmentRun);
        final View dataView = findViewById(R.id.dataView);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.runToggleButton);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isDrawerExpanded = !isDrawerExpanded;
                if(isDrawerExpanded){
                    Animation a = ViewAnimationUtils.expand(dataView);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fragmentView.getLayoutParams().height=screenHeight-ViewAnimationUtils.dpToPx(ViewAnimationUtils.expandedHeight);
                            fragmentView.requestLayout();
                            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_down_arrow));
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    dataView.startAnimation(a);
                } else {
                    Animation a = ViewAnimationUtils.collapse(dataView);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            fragmentView.getLayoutParams().height=screenHeight;
                            fragmentView.requestLayout();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
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

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RunActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        },0,UPDATE_IDLE);

    }

    public void update(){
        if(isRunning) {
            distance = getDistance();
            pace = getPace();
            heartRate = getHeartRate();
            Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();

            updateDisplay();
        }
    }

    public void updateDisplay(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String paceMode = sharedPreferences.getString("pace","p");
        final String unit = sharedPreferences.getString("unit","km");

        tvDistance.setText(new Distance(distance).toStr(unit,true));
        tvPace.setText(new Pace(pace).toStr(unit,paceMode,true));
        tvHeartRate.setText(String.format(getString(R.string.run_heart_rate),heartRate));
        tvBPM.setText(String.format(getString(R.string.run_bpm),getBPM()));
        tvCurrentSong.setText(getCurrentSong());
    }

    @Override
    public void onStop(){
        runMapFragment.stopLocationUpdates();
        super.onStop();
    }

    private double getDistance(){
        return runMapFragment.getDistance();
    }

    private double getPace(){
        return elapsedTime/(60*distance);
    }

    private int getHeartRate(){
        return 120;
    }

    private String getCurrentSong(){
        return "I'am A Believer - The Monkees";
    }

    private int getBPM(){
        return 68;
    }

    private PolylineOptions getRoute(){
        return runMapFragment.getJourneyPolylineOptions();
    }

}
