package com.telecom_paristech.pact25.rhythmrun;

public class Pace {
    private double value; //min/km

    public static String fancyPace(double d){
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

    public String toStrPace(String unit){
        return toStrPace(unit,false);
    }

    public String toStrPace(String unit, boolean displayUnits){
        String end="";
        if(displayUnits){
            end = " /"+unit;
        }
        switch (unit){
            case "km":
                return fancyPace(value)+end;
            case "mi":
                return fancyPace(value/Distance.KM_TO_MI)+end;
        }
        return "";
    }

    public String toStrSpeed(String unit){
        return toStrSpeed(unit,false);
    }

    public String toStrSpeed(String unit, boolean displayUnits){
        String end="";
        if(displayUnits){
            end=" "+unit+"/h";
        }
        return new Distance(60/value).toStr(unit)+end;
    }

    public String toStr(String unit, String mode, boolean displayUnits){
        if(mode.equals("p")){
            return toStrPace(unit, displayUnits);
        }
        else{
            return toStrSpeed(unit, displayUnits);
        }
    }

}
