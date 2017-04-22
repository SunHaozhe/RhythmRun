package com.telecom_paristech.pact25.rhythmrun.Android_activities;

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

import com.telecom_paristech.pact25.rhythmrun.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.ArrayList;

//TODO: implement the effort display

public class SumUpActivity extends AppCompatActivity
{

    private Distance distance; //km
    private double elapsedTime; //ms
    private Pace pace; //min/km
    private ArrayList<RunStatus> runData;
    private CustomPolylineOptions route;
    private DataManager dataManager;
    private HeartBeatCoach heartBeatCoach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("SumUp","Creating the activity SumUp.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_up);

        dataManager = new DataManager(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");
        String paceMode = sharedPreferences.getString("pace","p");
        boolean recordHistory = sharedPreferences.getBoolean("recordHistoryOnOff",true);

        //Creating the expandable effect for the sum up CardView.
        final ExpandableLinearLayout effortContent=(ExpandableLinearLayout) findViewById(R.id.sumUpEffortContent);
        RelativeLayout contentHeader=(RelativeLayout) findViewById(R.id.sumUpEffortHeader);
        contentHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effortContent.toggle();
            }
        });
        findViewById(R.id.sumUpEffortDownArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effortContent.toggle();
            }
        });

        //Getting the intent from the Run activity.
        Intent intent = getIntent();
        runData = intent.getParcelableArrayListExtra(Macros.EXTRA_RUN_DATA);
        Log.d("SumUp","Reading run data from RunActivity intent. "+runData.size()+" points collected.");
        RunStatus lastStatus = new RunStatus(0,null,new Distance(0),0,new Pace(0));
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

        heartBeatCoach = new HeartBeatCoach(this);

        //Displaying info in TextViews.

        TextView tvHeart = (TextView) findViewById(R.id.sumUpHeartBeat);
        TextView tvDistance = (TextView) findViewById(R.id.sumUpDistance);
        TextView tvPace = (TextView) findViewById(R.id.sumUpPace);
        TextView tvTime = (TextView) findViewById(R.id.sumUpTime);

        tvTime.setText(new Pace(elapsedTime/60000).toStr("km","p",false));
        tvDistance.setText(distance.toStr(unit,true));
        tvPace.setText(pace.toStr(unit,paceMode,true));
        tvHeart.setText(heartBeatCoach.messageOneMinuteAfterRun());

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
                dataManager.writeRunInfo(elapsedTime,distance,pace,runData,route); //Saving run data in a file.
                startActivity(intent);
            }
        });

        if(!recordHistory)
            saveButton.setVisibility(View.GONE);



    }
}