package com.example.raphaelattali.rythmrun.Android_activities;

import android.os.Parcel;
import android.os.Parcelable;

public class Distance implements Parcelable{
    final static double KM_TO_MI = 0.621371;

    private double value; //km

    protected Distance(Parcel in) {
        value = in.readDouble();
    }

    public static final Creator<Distance> CREATOR = new Creator<Distance>() {
        @Override
        public Distance createFromParcel(Parcel in) {
            return new Distance(in);
        }

        @Override
        public Distance[] newArray(int size) {
            return new Distance[size];
        }
    };

    private static String fancyDouble(double d){
        return fancyDouble(d,1);
    }

    private static String fancyDouble(double d, int digits){
        String s = Double.toString(d);
        int dotIndex = s.indexOf('.');
        if (s.charAt(dotIndex + 1) == '0') {
            return s.substring(0, dotIndex);
        } else {
            return s.substring(0, dotIndex + 1 + digits);
        }
    }

    public Distance(double value){
        this.value = value;
    }

    public double getValue(){
        return value;
    }

    String toStr(String unit){
        return toStr(unit,false);
    }

    String toStr(String unit, boolean displayUnits){
        String end="";
        if(displayUnits){
            end=" "+unit;
        }

        switch (unit){
            case "km":
                return fancyDouble(value)+end;
            case "mi":
                return fancyDouble(value*KM_TO_MI)+end;
        }
        return "";
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
