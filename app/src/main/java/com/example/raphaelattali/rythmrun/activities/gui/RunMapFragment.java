package com.example.raphaelattali.rythmrun.activities.gui;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class RunMapFragment extends SimpleMapFragment implements OnMapReadyCallback {

    private static ArrayList<LatLng> journey;
    private PolylineOptions journeyPolylineOptions;
    private Polyline journeyPolyline;

    static {
        journey = new ArrayList<>();
    }

    public RunMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        journeyPolylineOptions = new PolylineOptions();
        if(journey.size() > 0){
            journeyPolylineOptions.addAll(journey);
            drawPolyline();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);
    }

    @Override
    public LocationListener getCustomLocationListener(){
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                journey.add(coordinates);
                if(googleMap != null){
                    journeyPolylineOptions.add(coordinates);
                    drawPolyline();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    private void drawPolyline(){
        journeyPolylineOptions.color(Color.BLUE);
        if(googleMap != null){
            if(journeyPolyline==null){
                journeyPolyline = googleMap.addPolyline(journeyPolylineOptions);
            }
            else{
                journeyPolyline.remove();
                journeyPolyline = googleMap.addPolyline(journeyPolylineOptions);
            }
        }
    }

    public double getDistance(){
        if(journeyPolyline == null)
            return 0;
        return (double) ItineraryFragment.distanceOfPolyline(journeyPolylineOptions)/1000;
    }

}
