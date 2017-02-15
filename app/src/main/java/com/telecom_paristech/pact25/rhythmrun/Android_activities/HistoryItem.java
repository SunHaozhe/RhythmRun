package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.os.Parcel;
import android.os.Parcelable;

class HistoryItem implements Parcelable{
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

    protected HistoryItem(Parcel in) {
        route = in.readParcelable(CustomPolylineOptions.class.getClassLoader());
        date = in.readString();
        distance = in.readString();
        filename = in.readString();
        time = in.readString();
        pace = in.readString();
    }

    public static final Creator<HistoryItem> CREATOR = new Creator<HistoryItem>() {
        @Override
        public HistoryItem createFromParcel(Parcel in) {
            return new HistoryItem(in);
        }

        @Override
        public HistoryItem[] newArray(int size) {
            return new HistoryItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(route, i);
        parcel.writeString(date);
        parcel.writeString(distance);
        parcel.writeString(filename);
        parcel.writeString(time);
        parcel.writeString(pace);
    }
}
