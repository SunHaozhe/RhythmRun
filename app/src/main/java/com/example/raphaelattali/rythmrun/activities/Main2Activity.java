package com.example.raphaelattali.rythmrun.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.sensors.Accelerometer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;


public class Main2Activity extends AppCompatActivity {

    Button test_button = null;
    int k = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        test_button = (Button)findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test_button.setText("hop");
                k = 1;
            }
        });

        final Context mContext = this;


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Accelerometer acc = new Accelerometer(0.1f, 10, mContext);
                OutputStreamWriter o;
                try {
                    o = new OutputStreamWriter(mContext.openFileOutput("donnees.txt", Context.MODE_WORLD_READABLE));


                    while (k == 0) {
                        final String x = String.valueOf(acc.getAx());
                        o.write(x);
                        o.write(" ");
                        test_button.post(new Runnable() {
                            @Override
                            public void run() {
                                test_button.setText(x);
                            }


                        });
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                        }
                    }
                    o.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        }


    }
