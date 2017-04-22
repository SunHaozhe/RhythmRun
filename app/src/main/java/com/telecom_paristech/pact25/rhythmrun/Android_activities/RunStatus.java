package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

class RunStatus implements Parcelable {

    public final double time;
    public final LatLng location;
    public final Distance distance;
    public final Pace pace;
    final double heartRate;

    RunStatus(double time, LatLng location, Distance distance, double heartRate, Pace pace) {
        this.time = time;
        this.location = location;
        this.distance = distance;
        this.heartRate = heartRate;
        this.pace = pace;//new Pace((time/60000)/distance.getValue());
    }

    private RunStatus(Parcel in) {
        time = in.readDouble();
        location = in.readParcelable(LatLng.class.getClassLoader());
        distance = in.readParcelable(Distance.class.getClassLoader());
        pace = in.readParcelable(Pace.class.getClassLoader());
        heartRate = in.readDouble();
    }

    public static final Creator<RunStatus> CREATOR = new Creator<RunStatus>() {
        @Override
        public RunStatus createFromParcel(Parcel in) {
            return new RunStatus(in);
        }

        @Override
        public RunStatus[] newArray(int size) {
            return new RunStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(time);
        parcel.writeParcelable(location, i);
        parcel.writeParcelable(distance, i);
        parcel.writeParcelable(pace, i);
        parcel.writeDouble(heartRate);
    }
}