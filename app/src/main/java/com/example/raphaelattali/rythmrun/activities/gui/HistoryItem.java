package com.example.raphaelattali.rythmrun.activities.gui;

public class HistoryItem {
    private CustomPolylineOptions route;
    private String date;
    private String distance;
    private String place;
    private String time;

    public HistoryItem(String date, String place, String time, String distance, CustomPolylineOptions route) {
        this.time = time;
        this.route = route;
        this.date = date;
        this.distance = distance;
        this.place = place;
    }

    public CustomPolylineOptions getRoute() {
        return route;
    }

    public String getDate() {
        return date;
    }

    public String getDistance() {
        return distance;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }
}
