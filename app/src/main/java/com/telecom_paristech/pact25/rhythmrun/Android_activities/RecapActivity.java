package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;

public class RecapActivity extends AppCompatActivity {

    private View recapBarDistance;
    private View recapBarPace;
    private View recapBarMusic;
    private Button recapGOButton;

    private TranslateAnimation a;
    private TranslateAnimation b;
    private TranslateAnimation c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Recap", "Creating Recap activity.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap);

        //Used for correct display of diagonal bars (depth issues)
        findViewById(R.id.tvRecapLabelDistance).bringToFront();
        findViewById(R.id.recapSeparator1).bringToFront();
        findViewById(R.id.tvRecapLabelPace).bringToFront();
        findViewById(R.id.recapSeparator2).bringToFront();
        findViewById(R.id.tvRecapLabelMusic).bringToFront();
        findViewById(R.id.recapSeparator3).bringToFront();

        TextView paceLabel = (TextView) findViewById(R.id.tvRecapLabelPace);
        TextView tvDistance = (TextView) findViewById(R.id.tvRecapDistance);
        TextView tvPace = (TextView) findViewById(R.id.tvRecapPace);
        TextView tvMusic = (TextView) findViewById(R.id.tvRecapMusic);

        //Getting information from NewRun.
        Intent intent = getIntent();
        Distance distance = intent.getParcelableExtra(Macros.EXTRA_DISTANCE);
        Log.d("Recap","Recap distance: "+distance.getValue()+" km");
        Pace pace = intent.getParcelableExtra(Macros.EXTRA_PACE);
        Log.d("Recap","Recap pace: "+pace.getValue()+" min/km");
        String music = intent.getStringExtra(Macros.EXTRA_MUSIC);
        Log.d("Recap","Recap music: "+music);

        final CustomPolylineOptions itinerary = intent.getParcelableExtra(Macros.EXTRA_ITINERARY);
        SimpleMapFragment mapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.recapMapFragment);
        if(itinerary != null){
            Log.v("Recap","Found a custom polyline options.");
            if(itinerary.getPolylineOptions() != null){
                Log.d("Recap","Found an itinerary of "+itinerary.getPolylineOptions().getPoints().size()+" points.");
                mapFragment.drawnPolyline(itinerary.getPolylineOptions()); //Drawing the itinerary
                mapFragment.waitToAnimateCamera(itinerary.getBounds()); //Zooming on the itinerary
            } else {
                Log.d("Recap","No itinerary found.");
                mapFragment.zoomToCurrentLocation();
            }
        } else {
            Log.d("Recap","No custom polyline options found.");
            mapFragment.zoomToCurrentLocation();
        }

        //Setting up labels
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");
        String paceMode = sharedPreferences.getString("pace","p");

        tvDistance.setText(distance.toStr(unit,true));
        if(paceMode.equals("p")){
            paceLabel.setText(R.string.recap_selected_pace);
        }
        else{
            paceLabel.setText(R.string.recap_selected_speed);
        }

        //Recall; pace is set to -1 in Free mode.
        if(pace.getValue() >= 0){
            tvPace.setText(pace.toStr(unit,paceMode,true));
        }
        else{
            tvPace.setText(R.string.recap_free);
        }
        tvMusic.setText(music);

        recapGOButton = (Button) findViewById(R.id.buttonRecapGO);
        recapGOButton.setVisibility(View.INVISIBLE);
        recapGOButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Recap","Creating intent for RunActivity");
                Intent intent = new Intent(view.getContext(),RunActivity.class);
                intent.putExtra(Macros.EXTRA_ITINERARY,itinerary);
                startActivity(intent);
            }
        });

        //Animating the 3 displays bar.
        recapBarDistance = findViewById(R.id.recapBarDistance);
        recapBarPace = findViewById(R.id.recapBarPace);
        recapBarMusic = findViewById(R.id.recapBarMusic);

        recapBarDistance.setVisibility(View.GONE);
        recapBarPace.setVisibility(View.GONE);
        recapBarMusic.setVisibility(View.GONE);

        a = getLoadBarAnimation(recapBarDistance,200);
        b = getLoadBarAnimation(recapBarMusic,1000);
        c = getLoadBarAnimation(recapBarPace,600);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        /*
            Finishes the activity to be able to modify the previous run settings.
         */
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d("Recap","Recap activity has finished");
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttachedToWindow(){
        /*
            Needed to start the translate animation of the bars as late as possible.
            Otherwise, there are loading issues that messes up the timing.
         */
        super.onAttachedToWindow();

        //Starting a thread to wait 1s before animating the bars.
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v("Recap","Starting the animation waiting thread.");
                try {
                    Thread.sleep(1000);
                    Log.v("Recap","Animation waiting thread is done waiting.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("Recap","Animation waiting thread has timed out before his wait finishes.");
                }
                startLoadingAnimations();
            }
        }).start();
    }

    private void startLoadingAnimations(){
        /*
            Starts the animations of the recap bars.
         */

        //To animate widgets, it is needed to be on the main UI thread.
        //As this method is called in another thread, it needs to use runOnUiThread.
        RecapActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_zoom_in);
                final Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_zoom_out);
                animZoomIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        recapGOButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        recapGOButton.startAnimation(animZoomOut);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                animZoomIn.setInterpolator(new LinearInterpolator());
                try {
                    recapBarDistance.startAnimation(a);
                    recapBarMusic.startAnimation(b);
                    recapBarPace.startAnimation(c);
                    recapGOButton.startAnimation(animZoomIn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private TranslateAnimation getLoadBarAnimation(final View v, int h){
        /*
            Returns a translate information for a display bar.
         */
        TranslateAnimation a = new TranslateAnimation(0,0,-h-v.getHeight(),0);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(500);
        return a;
    }

}
