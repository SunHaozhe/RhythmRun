package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.os.Parcel;
import android.os.Parcelable;

public class Pace implements Parcelable {
    private double value; //min/km

    protected Pace(Parcel in) {
        value = in.readDouble();
    }

    public static final Creator<Pace> CREATOR = new Creator<Pace>() {
        @Override
        public Pace createFromParcel(Parcel in) {
            return new Pace(in);
        }

        @Override
        public Pace[] newArray(int size) {
            return new Pace[size];
        }
    };

    static String fancyPace(double d){
        int sec = Integer.parseInt(Double.toString(d*60).substring(0,Double.toString(d*60).indexOf(".")));
        int hrs = sec/3600;
        int min = (sec-(3600*hrs))/60;
        sec = sec-(3600*hrs)-(60*min);
        String s = "";
        if(hrs>0)
            s=hrs+":";
        if(hrs>0 && min<10)
            s=s+"0";
        s=s+min+":";
        if(sec<10)
            s=s+"0";
        s=s+sec;
        return s;
    }

    public Pace(double value){
        this.value = value;
    }

    public double getValue(){
        return value;
    }

    String toStrPace(String unit){
        return toStrPace(unit,false);
    }

    private String toStrPace(String unit, boolean displayUnits){
        String end="";
        if(displayUnits){
            end = " /"+unit;
        }
        switch (unit){
            case "km":
                return fancyPace(value)+end;
            case "mi":
                return fancyPace(value/ Distance.KM_TO_MI)+end;
        }
        return "";
    }

    String toStrSpeed(String unit){
        return toStrSpeed(unit,false);
    }

    private String toStrSpeed(String unit, boolean displayUnits){
        String end="";
        if(displayUnits){
            end=" "+unit+"/h";
        }
        return new Distance(60/value).toStr(unit)+end;
    }

    String toStr(String unit, String mode, boolean displayUnits){
        if(mode.equals("p")){
            return toStrPace(unit, displayUnits);
        }
        else{
            return toStrSpeed(unit, displayUnits);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(value);
    }
}
