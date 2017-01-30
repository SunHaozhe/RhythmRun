package com.example.raphaelattali.rythmrun;

/**
 * Created by asus on 29/01/2017.
 */

public class HistoricItem {
    private int color;
    private String date;
    private String distance;

    public HistoricItem(int color, String date, String distance)
    {
        this.color= color;
        this.date= date;
        this.distance=distance;

    }

    public int getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }

    public String getDistance() {
        return distance;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
