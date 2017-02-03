package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.Distance;
import com.example.raphaelattali.rythmrun.Pace;
import com.example.raphaelattali.rythmrun.R;

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
        Log.d("RecapActivity", "created RecapActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap);

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

        Intent intent = getIntent();
        Distance distance = new Distance(intent.getDoubleExtra(NewRunActivity.EXTRA_DISTANCE,0.0)/1000);
        Pace pace = new Pace(intent.getDoubleExtra(NewRunActivity.EXTRA_PACE,0.0));
        String music = intent.getStringExtra(NewRunActivity.EXTRA_MUSIC);

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
                Intent intent = new Intent(view.getContext(),RunActivity.class);
                startActivity(intent);
            }
        });

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
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    startLoadingAnimations();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startLoadingAnimations(){
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
        Log.d("Anim",Integer.toString(h));
        a.setDuration(500);
        return a;
    }

}
