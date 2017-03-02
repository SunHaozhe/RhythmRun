package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
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
    private boolean isRunning=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        final Button btn_start = (Button) findViewById(R.id.buttonRunPlay);
        final Chronometer chronometer = (Chronometer) findViewById(R.id.chronometer);

        btn_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning){
                    timeAtStop = chronometer.getBase()-SystemClock.elapsedRealtime();
                    chronometer.stop();
                    btn_start.setText(R.string.run_play);
                } else {
                    chronometer.setBase(SystemClock.elapsedRealtime()+timeAtStop);
                    chronometer.start();
                    btn_start.setText(R.string.run_pause);
                }
                isRunning = !isRunning;
            }
        });

        btn_start.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(view.getContext(),SumUpActivity.class);
                startActivity(intent);
                return true;
            }
        });

    }
}
