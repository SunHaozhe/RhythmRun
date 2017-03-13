package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.telecom_paristech.pact25.rhythmrun.R;

import java.io.File;
import java.util.ArrayList;

//TODO: implement share feature

public class HistoryRunActivity extends AppCompatActivity {

    HistoryItem historyItem;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_run);

        TextView tvDate = (TextView) findViewById(R.id.historyRunDate);
        TextView tvDistance = (TextView) findViewById(R.id.historyRunDistance);
        TextView tvPace = (TextView) findViewById(R.id.historyRunPace);
        TextView tvTime = (TextView) findViewById(R.id.historyRunTime);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");
        String paceMode = sharedPreferences.getString("pace","p");

        Intent intent = getIntent();
        historyItem = intent.getParcelableExtra(Macros.EXTRA_HISTORY_ITEM);
        tvDate.setText(historyItem.getDate());
        tvDistance.setText(historyItem.getDistance().toStr(unit,true));
        tvPace.setText(historyItem.getPace().toStr(unit,paceMode,true));
        tvTime.setText(historyItem.getTime());

        dataManager = new DataManager(this);

        SimpleMapFragment mapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.historyRunMap);
        CustomPolylineOptions route = historyItem.getRoute();
        if(route != null && route.getPolylineOptions()!=null && route.getPolylineOptions().getPoints().size()>0){
            mapFragment.drawnPolyline(route.getPolylineOptions());
            mapFragment.waitToAnimateCamera(route.getBounds());
        } else {
            mapFragment.zoomToCurrentLocation();
        }

        findViewById(R.id.historyRunShareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Upcoming feature",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.historyRunDiscardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataManager.deleteSong(historyItem.getFilename())){
                    Intent intent = new Intent(view.getContext(),HistoryActivity.class);
                    startActivity(intent);
                }
            }
        });

        //TODO: put this in a AsyncTask
        ArrayList<RunStatus> runData = dataManager.getRunData(historyItem.getFilename());
        GraphView graph = (GraphView) findViewById(R.id.graph);

        ArrayList<DataPoint> dataDistanceOverTime = new ArrayList<>();
        for(RunStatus runStatus : runData){
            dataDistanceOverTime.add(new DataPoint(runStatus.time, runStatus.distance.getValue()));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataDistanceOverTime.toArray(new DataPoint[dataDistanceOverTime.size()]));
        //LineGraphSeries<DataPoint> series = new LineGraphSeries<>((DataPoint[]) dataDistanceOverTime.toArray() );
        graph.addSeries(series);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
