package com.example.raphaelattali.rythmrun.activities.gui;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.PolylineOptions;

public class PolylineOptionsParcelable implements Parcelable {

    private PolylineOptions polylineOptions;

    public PolylineOptionsParcelable(PolylineOptions polylineOptions){
        this.polylineOptions = polylineOptions;
    }

    protected PolylineOptionsParcelable(Parcel in) {
        polylineOptions = in.readParcelable(PolylineOptions.class.getClassLoader());
    }

    public static final Creator<PolylineOptionsParcelable> CREATOR = new Creator<PolylineOptionsParcelable>() {
        @Override
        public PolylineOptionsParcelable createFromParcel(Parcel in) {
            return new PolylineOptionsParcelable(in);
        }

        @Override
        public PolylineOptionsParcelable[] newArray(int size) {
            return new PolylineOptionsParcelable[size];
        }
    };

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
