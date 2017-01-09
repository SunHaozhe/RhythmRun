package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;

import layout.PaceFragment;

public class RecapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap);

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

    }
}
