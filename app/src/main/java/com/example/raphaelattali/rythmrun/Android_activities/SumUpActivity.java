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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SumUpActivity extends AppCompatActivity {

    private Distance distance;
    private double elapsedTime;
    private Pace pace;
    private ArrayList<RunStatus> runData;
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
        runData = intent.getParcelableArrayListExtra(RunActivity.EXTRA_RUN_DATA);
        RunStatus lastStatus = runData.get(runData.size()-1);
        distance = lastStatus.distance;
        pace = lastStatus.pace;
        elapsedTime = lastStatus.time;

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
        tvDistance.setText(distance.toStr(unit,true));
        tvPace.setText(pace.toStr(unit,paceMode,true));

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

            DateFormat df2 = new SimpleDateFormat("EEEE d MMMM yyyy");
            printStream.print(df2.format(Calendar.getInstance().getTime())+"\n");
            printStream.print(elapsedTime+"\n");
            printStream.print(distance.getValue()+"\n");
            printStream.print(pace.getValue()+"\n");
            printLocation(printStream);

            for(RunStatus status : runData){
                printStream.print("\n"+
                        status.time+";"+
                        status.location.latitude+","+
                        status.location.longitude+","+
                        status.distance.getValue()+";"+
                        status.pace.getValue()+";"+
                        status.heartRate
                );
            }

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printLocation(PrintStream ps){
        if(route.getPolylineOptions().getPoints().size()==0){
            ps.print(" ");
        } else {
            for(LatLng pos : route.getPolylineOptions().getPoints())
                ps.print(pos.latitude+","+pos.longitude+";");
        }
    }
}