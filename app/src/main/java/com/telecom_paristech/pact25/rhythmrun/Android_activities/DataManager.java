package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static List<HistoryItem> runs;
    private static boolean runsLoaded=false;

    private OnRunsLoadedListener onRunsLoadedListener;
    private final Context context;

    public DataManager(Context context){
        this.context = context;
        if(runs==null)
            loadRuns();
    }

    public List<HistoryItem> getRuns(){
        return runs;
    }

    public void setOnRunsLoadedListener(OnRunsLoadedListener onRunsLoadedListener){
        this.onRunsLoadedListener = onRunsLoadedListener;
    }

    private void loadRuns(){
        /*
            Creating the list of history item from the .run files.
         */
        runs = new ArrayList<>();
        runsLoaded = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(String filename: getFileNames()){
                    runs.add(loadRun(filename));
                }
                if(onRunsLoadedListener != null){
                    onRunsLoadedListener.onRunsLoaded();
                    onRunsLoadedListener = null;
                }
                runsLoaded=true;
            }
        }).start();
    }

    private List<String> getFileNames(){
        /*
            Returns the filenames of the .run files
         */
        ArrayList<String> fileNames = new ArrayList<>();
        Log.d("DataManager","Exploring local data folder.");

        File directory = context.getFilesDir();
        Log.d("DataManager","Run files directory: "+directory.getPath());
        File[] files = directory.listFiles();

        if (files != null) {
            Log.v("DataManager", "Found "+files.length+" files.");
            for (File file:files) {
                //Checks if it is a .run file
                if(file.getName().endsWith(".run")){
                    Log.v("History","Found file "+file.getName());
                    fileNames.add(file.getName());
                }
            }
            Log.d("DataManager","Found "+fileNames.size()+" run files.");
        } else {
            Log.w("DataManager", "No run files found.");
        }
        return fileNames;
    }

    private HistoryItem loadRun(String filename){
        /*
            Creating a run history item from a file reading.
         */

        Log.d("DataManager","loading run from "+filename+"...");

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
            FileInputStream fileInputStream = context.openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            Log.v("DataManager","Reading "+filename+" with input stream "+reader);

            String line;
            while((line=reader.readLine()) != null){ //While there is a line
                stringBuilder.append(line).append("\n");
            }
            fileInputStream.close();
            Log.v("DataManager","Closing the input stream.");

            String result = stringBuilder.toString();

            String[] splitResult = result.split("\n");
            if(splitResult.length >= 5){ //At least 5 lines for basic information (see writing in SumUp).
                date = splitResult[0];
                time = Double.parseDouble(splitResult[1]);
                distance = new Distance(Double.parseDouble(splitResult[2]));
                pace = new Pace(Double.parseDouble(splitResult[3]));
                location = new CustomPolylineOptions(getPolylineFromString(splitResult[4]));
                Log.d("DataManager","Successful load of the run file.");
            } else {
                Log.w("DataManager","Error while loading file "+filename+". No enough lines.");
            }

        } catch (Exception e) {
            Log.e("DataManager","Failed to load the run file "+filename+".");
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unit = sharedPreferences.getString("unit_list","km");
        String paceMode = sharedPreferences.getString("pace","p");

        if(distance==null){
            distance = new Distance(0);
            Log.w("DataManager","Distance of "+filename+" is null.");
        }
        if(pace==null){
            pace = new Pace(0);
            Log.w("DataManager","Pace of "+filename+" is null.");
        }

        return new HistoryItem(filename,
                date,
                new Pace(time/60000).toStr(unit,"p",false),
                distance.toStr(unit,true),
                pace.toStr(unit,paceMode,true),
                location);
    }

    private PolylineOptions getPolylineFromString(String string){
        /*
            Returns a polyline from a string representation.
         */
        PolylineOptions polylineOptions = new PolylineOptions();
        for(String lat_lng : string.split(";")){
            if(!lat_lng.equals("") && !lat_lng.equals(" ")){
                double lat = Double.parseDouble(lat_lng.split(",")[0]);
                double lng = Double.parseDouble(lat_lng.split(",")[1]);
                polylineOptions.add(new LatLng(lat,lng));
            }
        }
        return polylineOptions;
    }

    interface OnRunsLoadedListener{
        void onRunsLoaded();
    }

    public boolean deleteSong(String filename){
        boolean deleted=false;
        int i=-1;
        for(int j=0;j<runs.size();j++){
            if(runs.get(j).getFilename().equals(filename))
                i=j;
        }
        if(i!=-1){
            String dir = context.getFilesDir().getAbsolutePath();
            File file = new File(dir, filename);
            deleted = file.delete();
            Log.i("DataManager",filename+" deleted: "+deleted);
            runs.remove(i);
        } else {
            Log.i("DataManager",filename+" illegal filename.");
        }
        return deleted;
    }

    public boolean deleteAllSongs(){
        boolean success = false;
        for(HistoryItem run : runs)
            success &= deleteSong(run.getFilename());
        return success;
    }
}
