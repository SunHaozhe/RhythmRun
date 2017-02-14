package com.example.raphaelattali.rythmrun.Android_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SumUpActivity extends AppCompatActivity {

    private double distance;
    private double elapsedTime;
    private double pace;
    private CustomPolylineOptions route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_up);

        final ExpandableLinearLayout effortContent=(ExpandableLinearLayout) findViewById(R.id.sumUpEffortContent);
        RelativeLayout contentHeader=(RelativeLayout) findViewById(R.id.sumUpEffortHeader);
        contentHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effortContent.toggle();
            }
        });

        Intent intent = getIntent();
        distance = intent.getDoubleExtra(RunActivity.EXTRA_DISTANCE,0);
        pace = intent.getDoubleExtra(RunActivity.EXTRA_PACE,0);
        elapsedTime = intent.getDoubleExtra(RunActivity.EXTRA_TIME,0);

        Distance distanceToPrint = new Distance(distance/1000);
        Pace paceToPrint = new Pace(pace);

        route = intent.getParcelableExtra(RunActivity.EXTRA_ROUTE);
        if(route!=null){
            SimpleMapFragment simpleMapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.sumUpMapFragment);
            simpleMapFragment.drawnPolyline(route.getPolylineOptions());
            simpleMapFragment.waitToAnimateCamera(route.getBounds());
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");
        String paceMode = sharedPreferences.getString("pace","p");

        TextView tvDistance = (TextView) findViewById(R.id.sumUpDistance);
        TextView tvPace = (TextView) findViewById(R.id.sumUpPace);
        TextView tvTime = (TextView) findViewById(R.id.sumUpTime);

        tvTime.setText(new Pace(elapsedTime/60000).toStr("km","p",false));
        tvDistance.setText(distanceToPrint.toStr(unit,true));
        tvPace.setText(paceToPrint.toStr(unit,paceMode,true));

        Button discardButton = (Button) findViewById(R.id.sumUpDiscardButton);
        Button saveButton = (Button) findViewById(R.id.sumUpSaveButton);

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),HomeActivity.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),HistoryActivity.class);
                writeRunInfo();
                startActivity(intent);
            }
        });

    }

    private void writeRunInfo(){
        /*   PRINT MODEL
        date
        time
        distance
        pace
        location
         */

        DateFormat df = new SimpleDateFormat("yyyy-MM-d-HH-mm-ss");
        String date = df.format(Calendar.getInstance().getTime());
        String filename = date+".run";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            PrintStream printStream = new PrintStream(outputStream);

            printDate(printStream);
            printTime(printStream);
            printDistance(printStream);
            printPace(printStream);
            printLocation(printStream);

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printDate(PrintStream ps){
        DateFormat df = new SimpleDateFormat("EEEE d MMMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        ps.print(date+"\n");
    }

    private void printTime(PrintStream ps){
        ps.print(elapsedTime+"\n");
    }

    private void printDistance(PrintStream ps){
        ps.print(distance+"\n");
    }

    private void printPace(PrintStream ps){
        ps.print(pace+"\n");
    }

    private void printLocation(PrintStream ps){
        for(LatLng pos : route.getPolylineOptions().getPoints()){
            ps.print(pos.latitude+","+pos.longitude+";");
        }
    }
}