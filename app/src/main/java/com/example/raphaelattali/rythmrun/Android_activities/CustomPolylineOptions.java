package com.example.raphaelattali.rythmrun.Android_activities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

public class CustomPolylineOptions implements Parcelable {

    private PolylineOptions polylineOptions;

    public CustomPolylineOptions(PolylineOptions polylineOptions){
        this.polylineOptions = polylineOptions;
    }

    protected CustomPolylineOptions(Parcel in) {
        polylineOptions = in.readParcelable(PolylineOptions.class.getClassLoader());
    }

    public static final Creator<CustomPolylineOptions> CREATOR = new Creator<CustomPolylineOptions>() {
        @Override
        public CustomPolylineOptions createFromParcel(Parcel in) {
            return new CustomPolylineOptions(in);
        }

        @Override
        public CustomPolylineOptions[] newArray(int size) {
            return new CustomPolylineOptions[size];
        }
    };

    public LatLngBounds getBounds(){
        if(polylineOptions.getPoints().size()>0){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(LatLng point : polylineOptions.getPoints()){
                builder.include(point);
            }
            return builder.build();
        }
        return null;
    }

    public PolylineOptions getPolylineOptions(){
        return polylineOptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(polylineOptions, i);
    }
}
