package com.telecom_paristech.pact25.rhythmrun.Android_activities;

class HistoryItem {
    private CustomPolylineOptions route;
    private String date;
    private String distance;
    private String filename;
    private String time;
    private String pace;

    HistoryItem(String filename, String date, String time, String distance, String pace, CustomPolylineOptions route) {
        this.time = time;
        this.route = route;
        this.date = date;
        this.distance = distance;
        this.filename = filename;
        this.pace = pace;
    }

    CustomPolylineOptions getRoute() {
        return route;
    }

    String getDate() {
        return date;
    }

    public String getDistance() {
        return distance;
    }

    public String getFilename() {
        return filename;
    }

    public String getTime() {
        return time;
    }

    public String getPace() {return pace;}
}
