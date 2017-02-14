package com.example.raphaelattali.rythmrun.Android_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity
{
    public static final String EXTRA_DISTANCE = "distance";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_PACE = "pace";
    public static final String EXTRA_ROUTE = "route";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView) findViewById(R.id.listView);
        //List<HistoryItem> samples = generateSample();
        List<HistoryItem> samples = getRuns();

        HistoryAdapter adapter = new HistoryAdapter(HistoryActivity.this, samples);
        listView.setAdapter(adapter);
    }

    private List<HistoryItem> generateSample()
    {
        List<HistoryItem> samples = new ArrayList<>();
        samples.add(new HistoryItem("01/02/2017 19h30","Paris","10 km","47:18",null));
        samples.add(new HistoryItem("30/01/2017 07h44","Paris","9.5 km","43:56",null));
        samples.add(new HistoryItem("27/01/2017 12h34","Paris","16.7 km","1:23:45",null));
        return samples;
    }

    public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

        HistoryAdapter(Context context, List<HistoryItem> history) {
            super(context, 0, history);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_history, parent, false);
            }

            HistoricViewHolder viewHolder = (HistoricViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new HistoricViewHolder();
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
                viewHolder.place = (TextView) convertView.findViewById(R.id.place);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                //viewHolder.mapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.historyMap);
                convertView.setTag(viewHolder);
            }

            final HistoryItem history = getItem(position);

            viewHolder.date.setText(history.getDate());
            viewHolder.distance.setText(history.getDistance());
            viewHolder.place.setText("- "+history.getPlace());
            viewHolder.time.setText(history.getTime());
            /*if(history.getRoute() != null){
                viewHolder.mapFragment.drawnPolyline(history.getRoute().getPolylineOptions());
                viewHolder.mapFragment.waitToAnimateCamera(history.getRoute().getBounds());
            }*/


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), HistoryRunActivity.class);
                    intent.putExtra(EXTRA_DATE,history.getDate());
                    intent.putExtra(EXTRA_DISTANCE,history.getDistance());
                    intent.putExtra(EXTRA_PACE,history.getPlace());
                    intent.putExtra(EXTRA_TIME,history.getTime());
                    intent.putExtra(EXTRA_ROUTE,history.getRoute());
                    startActivity(intent);
                }
            });


            return convertView;

        }

        private class HistoricViewHolder {
            public TextView date;
            public TextView distance;
            public TextView place;
            public TextView time;
            public SimpleMapFragment mapFragment;
        }
    }

    private List<String> getFileNames(){
        ArrayList<String> fileNames = new ArrayList<>();

        File directory = getFilesDir();
        Log.d("Directory","Run files directory: "+directory.getPath());
        File[] files = directory.listFiles();
        if (files != null) {
            Log.d("Files", "Size: " + files.length);
            for (File file:files) {
                if(file.getName().endsWith(".run")){
                    fileNames.add(file.getName());
                }
                Log.d("File",file.getName());
            }
        } else {
            Log.d("Files", "files is null: no files found ?");
        }

        return fileNames;
    }

    private List<HistoryItem> getRuns(){
        ArrayList<HistoryItem> historyItems = new ArrayList<>();
        for(String filename: getFileNames()){
                historyItems.add(getRun(filename));
        }
        return historyItems;
    }

    private HistoryItem getRun(String filename){
        StringBuilder stringBuilder = new StringBuilder();
        String result = null;

        String date=null;
        double time=0;
        double distance=0;
        double pace=0;
        CustomPolylineOptions location=null;

        try {
            FileInputStream fileInputStream = openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line=null;
            while((line=reader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            fileInputStream.close();
            result = stringBuilder.toString();

            String[] splitResult = result.split("\n");
            if(splitResult.length >= 5){
                date = splitResult[0];
                time = Double.parseDouble(splitResult[1]);
                distance = Double.parseDouble(splitResult[2]);
                pace = Double.parseDouble(splitResult[3]);
                location = new CustomPolylineOptions(getPolylineFromString(splitResult[4]));
            }
            else{
                Log.d("Run reading","No enough lines found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");

        return new HistoryItem(date,"",
                new Distance(distance).toStr(unit,true),
                new Pace(time/60000).toStr(unit,"p",false),
                location);
    }

    public PolylineOptions getPolylineFromString(String string){
        PolylineOptions polylineOptions = new PolylineOptions();
        for(String latlng : string.split(";")){
            if(latlng != ""){
                double lat = Double.parseDouble(latlng.split(",")[0]);
                double lng = Double.parseDouble(latlng.split(",")[1]);
                polylineOptions.add(new LatLng(lat,lng));
            }
        }
        return polylineOptions;
    }
}

