package com.example.raphaelattali.rythmrun.activities.gui;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.view.View.OnClickListener;

import com.example.raphaelattali.rythmrun.R;

public class RunActivity extends AppCompatActivity {

    private long timeAtStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        Button btn_start = (Button) findViewById(R.id.buttonRunPlay);
        Button btn_stop = (Button) findViewById(R.id.buttonRunStop);
        Button btn_reset = (Button) findViewById(R.id.buttonRunReset);
        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);

        btn_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime()+timeAtStop);
                chronometer.start();
            }
        });

        btn_stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timeAtStop = chronometer.getBase()-SystemClock.elapsedRealtime();
                chronometer.stop();
            }
        });

        btn_reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                timeAtStop=0;
            }
        });
    }
}
