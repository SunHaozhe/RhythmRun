package com.telecom_paristech.pact25.rhythmrun.Android_activities;

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

import com.telecom_paristech.pact25.rhythmrun.R;
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
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("History","Creating activity history.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dataManager = new DataManager(this);

        //Initialization of the list view.
        ListView listView;
        listView = (ListView) findViewById(R.id.listView);
        HistoryAdapter adapter = new HistoryAdapter(HistoryActivity.this, dataManager.getRuns());
        listView.setAdapter(adapter);
    }

    public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

        HistoryAdapter(Context context, List<HistoryItem> history) {
            super(context, 0, history);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            Log.d("History","Creating of the history view at position "+position);

            //Initialization of an history row layout.
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_history, parent, false);
            }

            //Creation of the views holder.
            HistoricViewHolder viewHolder = (HistoricViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new HistoricViewHolder();
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(viewHolder);
            }

            final HistoryItem history = getItem(position);
            if(history!=null){
                Log.v("History","Setting the view content of item "+position+".");
                viewHolder.date.setText(history.getDate());
                viewHolder.distance.setText(history.getDistance());
                viewHolder.time.setText(history.getTime());

                //Setting up the listener to display full information of a run.
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("History","Creating intent to show all information of run "+position);
                        Intent intent = new Intent(view.getContext(), HistoryRunActivity.class);
                        intent.putExtra(Macros.EXTRA_HISTORY_ITEM,history);
                        startActivity(intent);
                    }
                });

            } else {
                Log.e("History","History item "+position+" is null.");
            }

            return convertView;
        }

        private class HistoricViewHolder {
            //A simple holder for an history row.
            TextView date;
            public TextView distance;
            public TextView time;
        }
    }

    /*
    private List<String> getFileNames(){

        ArrayList<String> fileNames = new ArrayList<>();
        Log.d("History","Exploring local data folder.");

        File directory = getFilesDir();
        Log.d("History","Run files directory: "+directory.getPath());
        File[] files = directory.listFiles();

        if (files != null) {
            Log.v("History", "Found "+files.length+" files.");
            for (File file:files) {
                //Checks if it is a .run file
                if(file.getName().endsWith(".run")){
                    Log.v("History","Found file "+file.getName());
                    fileNames.add(file.getName());
                }
            }
            Log.d("History","Found "+fileNames.size()+" run files.");
        } else {
            Log.w("History", "No run files found.");
        }
        return fileNames;
    }

    private List<HistoryItem> getRuns(){

        Log.i("History","creating the history list...");
        ArrayList<HistoryItem> historyItems = new ArrayList<>();
        for(String filename: getFileNames()){
                historyItems.add(getRun(filename));
        }
        return historyItems;
    }

    private HistoryItem getRun(String filename){


        Log.d("History","loading run from "+filename+"...");

        //All those initializations are needed to be able to return something.
        String date=null;
        double time=0;
        Distance distance=null;
        Pace pace=null;
        CustomPolylineOptions location=null;

        //A string builder to create the string that contains the file text.
        StringBuilder stringBuilder = new StringBuilder();

        try {
            //Opening a reader input stream to read the run file.
            FileInputStream fileInputStream = openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            Log.v("History","Reading "+filename+" with input stream "+reader);

            String line;
            while((line=reader.readLine()) != null){ //While there is a line
                stringBuilder.append(line).append("\n");
            }
            fileInputStream.close();
            Log.v("History","Closing the input stream.");

            String result = stringBuilder.toString();

            String[] splitResult = result.split("\n");
            if(splitResult.length >= 5){ //At least 5 lines for basic information (see writing in SumUp).
                date = splitResult[0];
                time = Double.parseDouble(splitResult[1]);
                distance = new Distance(Double.parseDouble(splitResult[2]));
                pace = new Pace(Double.parseDouble(splitResult[3]));
                location = new CustomPolylineOptions(getPolylineFromString(splitResult[4]));
                Log.d("History","Successful load of the run file.");
            } else {
                Log.w("History","Error while loading file "+filename+". No enough lines.");
            }

        } catch (Exception e) {
            Log.e("History","Failed to load the run file "+filename+".");
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");
        String paceMode = sharedPreferences.getString("pace","p");

        if(distance==null){
            distance = new Distance(0);
            Log.w("History","Distance of "+filename+" is null.");
        }
        if(pace==null){
            pace = new Pace(0);
            Log.w("History","Pace of "+filename+" is null.");
        }

        return new HistoryItem(filename,
                date,
                new Pace(time/60000).toStr(unit,"p",false),
                distance.toStr(unit,true),
                pace.toStr(unit,paceMode,true),
                location);
    }

    public PolylineOptions getPolylineFromString(String string){

        PolylineOptions polylineOptions = new PolylineOptions();
        for(String lat_lng : string.split(";")){
            if(!lat_lng.equals("") && !lat_lng.equals(" ")){
                double lat = Double.parseDouble(lat_lng.split(",")[0]);
                double lng = Double.parseDouble(lat_lng.split(",")[1]);
                polylineOptions.add(new LatLng(lat,lng));
            }
        }
        return polylineOptions;
    }*/
}

