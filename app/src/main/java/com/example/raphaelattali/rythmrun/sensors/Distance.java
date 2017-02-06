package com.example.raphaelattali.rythmrun.sensors;

public class Distance {
    public final static double KM_TO_MI = 0.621371;

    double value; //always in kilometers

    public static String fancyDouble(double d){
        return fancyDouble(d,1);
    }

    public static String fancyDouble(double d, int digits){
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

    public String toStr(String unit){
        return toStr(unit,false);
    }

    public String toStr(String unit, boolean displayUnits){
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
}
