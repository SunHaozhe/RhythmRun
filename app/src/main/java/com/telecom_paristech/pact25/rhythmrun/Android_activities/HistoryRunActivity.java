package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;
import com.telecom_paristech.pact25.rhythmrun.R;

import java.io.File;
import java.util.ArrayList;

//TODO: implement share feature

public class HistoryRunActivity extends AppCompatActivity {

    HistoryItem historyItem;
    DataManager dataManager;

    ArrayList<DataPoint> seriesDistanceList = new ArrayList<>();
    ArrayList<DataPoint> seriesPaceList = new ArrayList<>();
    ArrayList<DataPoint> seriesHeartRateList = new ArrayList<>();

    LineGraphSeries<DataPoint> seriesDistance;
    LineGraphSeries<DataPoint> seriesPace;
    LineGraphSeries<DataPoint> seriesHeartRate;

    Series mainSeries;
    Series secondSeries;

    GraphView graph;

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

        ArrayList<RunStatus> runData = dataManager.getRunData(historyItem.getFilename());
        graph = (GraphView) findViewById(R.id.graph);

        new TaskLoadRunData().execute(runData);

        final CheckBox cbDistance = (CheckBox) findViewById(R.id.cbDistance);
        final CheckBox cbPace = (CheckBox) findViewById(R.id.cbPace);
        final CheckBox cbHeart = (CheckBox) findViewById(R.id.cbHeartRate);

        cbDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(!displaySeries(seriesDistance, getMinMax(seriesDistanceList))){
                        cbDistance.setChecked(false);
                    }
                } else {
                    deleteSeries(seriesDistance);
                }
            }
        });

        cbPace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(!displaySeries(seriesPace, getMinMax(seriesPaceList))){
                        cbPace.setChecked(false);
                    }
                } else {
                    deleteSeries(seriesPace);
                }
            }
        });

        cbHeart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(!displaySeries(seriesHeartRate, getMinMax(seriesHeartRateList))){
                        cbHeart.setChecked(false);
                    }
                } else {
                    deleteSeries(seriesHeartRate);
                }
            }
        });

        /*graph.addSeries(seriesPace);
        graph.getSecondScale().addSeries(seriesHeartRate);
        int[] minMax = getMinMax(seriesHeartRateList);
        graph.getSecondScale().setMinY(minMax[0]);
        graph.getSecondScale().setMaxY(minMax[1]);*/

    }

    private class TaskLoadRunData extends AsyncTask<ArrayList<RunStatus>, Void, Void>{

        @Override
        protected Void doInBackground(ArrayList<RunStatus>... runData) {
            for(RunStatus runStatus : runData[0]){
                seriesDistanceList.add(new DataPoint(runStatus.time/1000, runStatus.distance.getValue()));
                seriesPaceList.add(new DataPoint(runStatus.time/1000, runStatus.pace.getValue()));
                seriesHeartRateList.add(new DataPoint(runStatus.time/1000, runStatus.heartRate));
            }

            seriesDistance = new LineGraphSeries<>(seriesDistanceList.toArray(new DataPoint[seriesDistanceList.size()]));
            seriesPace = new LineGraphSeries<>(seriesPaceList.toArray(new DataPoint[seriesPaceList.size()]));
            seriesHeartRate = new LineGraphSeries<>(seriesHeartRateList.toArray(new DataPoint[seriesHeartRateList.size()]));

            return null;
        }
    }

    private int[] getMinMax(ArrayList<DataPoint> series){
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for(DataPoint dataPoint : series){
            if((int) dataPoint.getY() < min)
                min = (int) dataPoint.getY();
            if((int) dataPoint.getY() > max)
                max = (int) dataPoint.getY();
        }
        return new int[]{min,max+1};
    }

    private boolean displaySeries(Series series, int[] minMax){
        if(mainSeries == null){
            graph.addSeries(series);
            mainSeries = series;
            return true;
        } else if (secondSeries == null){
            graph.getSecondScale().addSeries(series);
            graph.getSecondScale().setMinY(minMax[0]);
            graph.getSecondScale().setMaxY(minMax[1]);
            secondSeries = series;
            return true;
        }
        return false;
    }

    private void deleteSeries(Series series){
        if(series == mainSeries) {
            graph.removeSeries(series);
            mainSeries = null;
        } else if (series == secondSeries){
            graph.getSecondScale().removeSeries(series);
            secondSeries = null;
        }
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
