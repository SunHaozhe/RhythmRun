package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
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

    FormattedSeries fsPace;
    FormattedSeries fsDistance;
    FormattedSeries fsHeart;

    //Series mainSeries;
    //Series secondSeries;

    GraphView graph;

    private class FormattedSeries {
        private LineGraphSeries<DataPoint> series;
        private ArrayList<DataPoint> seriesList;
        private int[] minMax;
        private LabelFormatter labelFormatter;

        FormattedSeries(LineGraphSeries<DataPoint> series, ArrayList<DataPoint> seriesList, LabelFormatter labelFormatter){
            this.series = series;
            this.seriesList = seriesList;
            this.minMax = getMinMax(seriesList);
            this.labelFormatter = labelFormatter;
        }

        public void display(){
            graph.removeAllSeries();
            graph.addSeries(this.series);
            graph.getGridLabelRenderer().setLabelVerticalWidth(90);
            graph.getGridLabelRenderer().setTextSize((float) 25);
            graph.getViewport().setMinY(this.minMax[0]);
            graph.getViewport().setMaxY(this.minMax[1]);
            if (this.labelFormatter != null)
                graph.getGridLabelRenderer().setLabelFormatter(labelFormatter);
        }
    }

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
        graph.getViewport().setYAxisBoundsManual(true);

        new TaskLoadRunData().execute(runData);

        final RadioButton rbDistance = (RadioButton) findViewById(R.id.cbDistance);
        final RadioButton rbPace = (RadioButton) findViewById(R.id.cbPace);
        final RadioButton rbHeart = (RadioButton) findViewById(R.id.cbHeartRate);

        rbDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fsDistance.display();
            }
        });

        rbPace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fsPace.display();
            }
        });

        rbHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fsHeart.display();
            }
        });

        rbPace.setChecked(true);
        //displaySeries(seriesPace, getMinMax(seriesPaceList));
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

            fsPace = new FormattedSeries(seriesPace, seriesPaceList, new LabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX)
                        return new Pace(value/60).toStr("km", "p", false);
                    return new Pace(value).toStr("km", "p", false);
                }

                @Override
                public void setViewport(Viewport viewport) {

                }
            });
            fsDistance = new FormattedSeries(seriesDistance, seriesDistanceList, new LabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX)
                        return new Pace(value/60).toStr("km", "p", false);
                    if(value < 1)
                        return Integer.toString((int) (value*1000)) + "m";
                    return Double.toString(value) + "km";
                    //return Double.toString(value);
                }

                @Override
                public void setViewport(Viewport viewport) {

                }
            });
            fsHeart = new FormattedSeries(seriesHeartRate, seriesHeartRateList, new LabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX)
                        return new Pace(value/60).toStr("km", "p", false);
                    return Integer.toString((int) value);
                }

                @Override
                public void setViewport(Viewport viewport) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            fsPace.display();
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

    /*private boolean displaySeries(Series series, int[] minMax){
        if (mainSeries != null)
            graph.removeSeries(mainSeries);
        graph.addSeries(series);
        mainSeries = series;
        return true;
    }*/

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
