package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class DataManager {

    private static List<HistoryItem> runs;

    private OnRunsLoadedListener onRunsLoadedListener;
    private final Context context;

    DataManager(Context context){
        this.context = context;
        if(runs==null)
            loadRuns();
    }

    List<HistoryItem> getRuns(){
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

        for(String filename: getFileNames()){
            runs.add(loadRun(filename));
        }
        sortRunsByDate();
        if(onRunsLoadedListener != null){
            onRunsLoadedListener.onRunsLoaded();
            onRunsLoadedListener = null;
        }
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
                new Pace(time/60000).toStr("km","p",false),
                distance,
                pace,
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

    private void sortRunsByDate(){
        /*
            Last runs will be at index 0.
         */
        for(int i=1;i<runs.size();i++){
            int j=i;
            while(j>0 && (runs.get(i).getFilename().compareTo(runs.get(j-1).getFilename())>0)){
                j-=1;
            }
            HistoryItem temp = runs.get(i);
            runs.set(i,runs.get(j));
            runs.set(j,temp);
        }
    }

    interface OnRunsLoadedListener{
        void onRunsLoaded();
    }

    boolean deleteSong(String filename){
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

    boolean deleteAllSongs(){
        boolean success = false;
        for(HistoryItem run : runs)
            success &= deleteSong(run.getFilename());
        return success;
    }

    private List<HistoryItem> getLastWeekRuns(){
        List<HistoryItem> weekRuns = new ArrayList<>();
        Log.d("DataManager","Getting last week runs.");

        java.util.Calendar calendar;
        calendar = java.util.Calendar.getInstance();

        calendar.add(java.util.Calendar.DAY_OF_MONTH, -7);
        Date weekStart = calendar.getTime();
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String weekStartString = df.format(weekStart)+".run";

        Log.d("DataManager","Week start string: "+weekStartString);

        for(HistoryItem run: runs){
            if(run.getFilename().compareTo(weekStartString)>0){
                Log.v("DataManager","Run in last week: "+run.getFilename());
                weekRuns.add(run);
            }
        }
        return weekRuns;
    }

    Distance getLastWeekDistance(){
        double distance=0;
        for(HistoryItem run: getLastWeekRuns())
            distance += run.getDistance().getValue();
        return new Distance(distance);
    }

    Pace getLastWeekPace(){
        double pace=0;
        int counter=0;
        for(HistoryItem run: getLastWeekRuns()){
            if(!Double.isInfinite(run.getPace().getValue())){
                pace += run.getPace().getValue();
                counter++;
            }
        }
        return new Pace(pace/counter);
    }

    public void writeRunInfo(double elapsedTime, Distance distance, Pace pace, List<RunStatus> runData, CustomPolylineOptions route){
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

        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.FRANCE);
        String date = df.format(java.util.Calendar.getInstance().getTime());
        String filename = date+".run";
        Log.i("SumUp","Saving run data in "+filename);

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            PrintStream printStream = new PrintStream(outputStream); //Use of a print stream to write text

            @SuppressWarnings("SpellCheckingInspection") java.text.DateFormat df2 = new java.text.SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);
            printStream.print(df2.format(java.util.Calendar.getInstance().getTime())+"\n");
            printStream.print(elapsedTime+"\n");
            printStream.print(distance.getValue()+"\n");
            printStream.print(pace.getValue()+"\n");
            printLocation(printStream, route);

            for(RunStatus status : runData){
                if(status.location==null){
                    printStream.print("\n"+
                            status.time+";"+
                            "0,"+
                            "O,"+
                            status.distance.getValue()+";"+
                            status.pace.getValue()+";"+
                            status.heartRate
                    );} else {
                printStream.print("\n"+
                        status.time+";"+
                        status.location.latitude+","+
                        status.location.longitude+","+
                        status.distance.getValue()+";"+
                        status.pace.getValue()+";"+
                        status.heartRate
                );}
            }
            outputStream.close();

            runs.add(new HistoryItem(
                    filename,
                    df2.format(java.util.Calendar.getInstance().getTime()),
                    new Pace(elapsedTime/60000).toStr("km","p",false),
                    distance,
                    pace,
                    route
            ));

            Log.d("SumUp","Run data successfully saved!");
        } catch (Exception e) {
            Log.e("SumUp","Error in saving run data.");
            e.printStackTrace();
        }

    }

    private void printLocation(PrintStream ps, CustomPolylineOptions route){
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
