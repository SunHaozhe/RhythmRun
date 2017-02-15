package com.example.raphaelattali.rhythmrun.Android_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.raphaelattali.rhythmrun.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

//TODO: implement the effort display

public class SumUpActivity extends AppCompatActivity {

    private Distance distance; //km
    private double elapsedTime; //ms
    private Pace pace; //min/km
    private ArrayList<RunStatus> runData;
    private CustomPolylineOptions route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("SumUp","Creating the activity SumUp.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_up);

        //Creating the expandable effect for the sum up CardView.
        final ExpandableLinearLayout effortContent=(ExpandableLinearLayout) findViewById(R.id.sumUpEffortContent);
        RelativeLayout contentHeader=(RelativeLayout) findViewById(R.id.sumUpEffortHeader);
        contentHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effortContent.toggle();
            }
        });

        //Getting the intent from the Run activity.
        Intent intent = getIntent();
        runData = intent.getParcelableArrayListExtra(Macros.EXTRA_RUN_DATA);
        Log.d("SumUp","Reading run data from RunActivity intent. "+runData.size()+" points collected.");
        RunStatus lastStatus = new RunStatus(0,null,new Distance(0),0);
        if(runData.size()>0)
            lastStatus = runData.get(runData.size()-1);

        distance = lastStatus.distance;
        pace = lastStatus.pace;
        elapsedTime = lastStatus.time;
        Log.d("SumUp","Reading run time: "+elapsedTime);
        Log.d("SumUp","Reading distance: "+distance.getValue()+" km");
        Log.d("SumUp","Reading pace: "+pace.getValue()+" min/km");

        //Getting the run polyline.
        route = intent.getParcelableExtra(Macros.EXTRA_ROUTE);
        SimpleMapFragment simpleMapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.sumUpMapFragment);
        if(route!=null && route.getPolylineOptions()!=null && route.getPolylineOptions().getPoints().size()>0){
            Log.d("SumUp","Found an itinerary.");
            simpleMapFragment.drawnPolyline(route.getPolylineOptions());
            simpleMapFragment.waitToAnimateCamera(route.getBounds());
        } else {
            Log.d("SumUp","No itinerary found");
            simpleMapFragment.zoomToCurrentLocation();
        }

        //Displaying info in TextViews.
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
                Log.i("SumUp","Discard run selected. Going to HomeActivity.");
                Intent intent = new Intent(view.getContext(),HomeActivity.class);
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SumUp","Saving run selected. Going to HistoryActivity");
                Intent intent = new Intent(view.getContext(),HistoryActivity.class);
                writeRunInfo(); //Saving run data in a file.
                startActivity(intent);
            }
        });

    }

    private void writeRunInfo(){

        /*
            Writes run data in a .run file, with the following pattern:
                date
                time
                distance
                pace
                location: lat,lng;lat,lng;lat,lng; ...
                run status 1: time;lat,lng;distance;pace;heartRate
                run status 2: time:lat,lng;distance;pace;heartRate
                ...
         */

        DateFormat df = new SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.FRANCE);
        String date = df.format(Calendar.getInstance().getTime());
        String filename = date+".run";
        Log.i("SumUp","Saving run data in "+filename);

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            PrintStream printStream = new PrintStream(outputStream); //Use of a print stream to write text

            @SuppressWarnings("SpellCheckingInspection") DateFormat df2 = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);
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
            Log.d("SumUp","Run data successfully saved!");
        } catch (Exception e) {
            Log.e("SumUp","Error in saving run data.");
            e.printStackTrace();
        }
    }

    private void printLocation(PrintStream ps){
        //Printing location with the following pattern:
        //  lat,lng;lat,lng;lat,lng ...
        if(route.getPolylineOptions().getPoints().size()==0){
            ps.print(" ");
        } else {
            for(LatLng pos : route.getPolylineOptions().getPoints())
                ps.print(pos.latitude+","+pos.longitude+";");
        }
    }
}