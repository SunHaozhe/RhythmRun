package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;

import layout.PaceFragment;

public class RecapActivity extends AppCompatActivity {

    private boolean shownWarning = false;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap);

        linearLayout = (LinearLayout) findViewById(R.id.RecapToHide);

        TextView tvDistance = (TextView) findViewById(R.id.tvRecapDistance);
        TextView tvPace = (TextView) findViewById(R.id.tvRecapPace);
        TextView tvMusic = (TextView) findViewById(R.id.tvRecapMusic);

        Intent intent = getIntent();
        double distance = intent.getDoubleExtra(NewRunActivity.EXTRA_DISTANCE,0.0);
        double pace = intent.getDoubleExtra(NewRunActivity.EXTRA_PACE,0.0);
        String music = intent.getStringExtra(NewRunActivity.EXTRA_MUSIC);

        tvDistance.setText(distance+" km");
        if(pace >= 0){
            tvPace.setText(PaceFragment.fancyPace(pace)+" /km");
        }
        else{
            tvPace.setText("free");
        }
        tvMusic.setText(music);

        Button button = (Button) findViewById(R.id.buttonRecapGO);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),RunActivity.class);
                startActivity(intent);
            }
        });

    }

    public boolean toggleWarning(){
        shownWarning = !shownWarning;
        Log.i("toggle","Entering the toggle method");
        TranslateAnimation a;

        if(shownWarning){
            Log.i("toggle","Opening the menu");
            a = new TranslateAnimation(-linearLayout.getWidth(),0,0,0);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    linearLayout.setVisibility(View.VISIBLE);
                    Log.i("animation","Start of opening animation.");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.i("animation","End of opening animation");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        else{
            Log.i("toggle","Closing the menu");
            a = new TranslateAnimation(0,-linearLayout.getWidth(),0,0);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.i("animation","Start of closing animation");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    linearLayout.setVisibility(View.GONE);
                    Log.i("animation","End of closing animation");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        a.setDuration(300);
        a.setInterpolator(new AccelerateInterpolator());
        linearLayout.startAnimation(a);

        return shownWarning;
    }

}
