package com.example.raphaelattali.rythmrun.Android_activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

//TODO: Check if Location is enabled and ask if necessary

public class RunMapFragment extends SimpleMapFragment implements OnMapReadyCallback {

    private static ArrayList<LatLng> journey;
    private PolylineOptions journeyPolylineOptions;
    private Polyline journeyPolyline;

    private LatLng lastLatLng=null;

    static {
        journey = new ArrayList<>();
    }

    public RunMapFragment() {
        // Required empty public constructor
    }

    public PolylineOptions getJourneyPolylineOptions(){
        return journeyPolylineOptions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("Itinerary","Initialization of RunMapFragment.");

        journeyPolylineOptions = new PolylineOptions();
        if(journey.size() > 0){ //If a run has already begun.
            Log.d("RunMap","Redrawing pre-existing polyline.");
            journeyPolylineOptions.addAll(journey);
            drawPolyline();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("RunMap","Location has changed to "+location+".");
                lastLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                journey.add(lastLatLng); //Adding the point to the list.
                if(googleMap != null){
                    Log.v("RunMap","Adding last location to the polyline.");
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng));
                    journeyPolylineOptions.add(lastLatLng);
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

        super.onMapReady(map);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);
            if(location != null)
                journey.add(new LatLng(location.getLatitude(), location.getLongitude())); //Adding of the first location.
        }
        googleMap.getUiSettings().setAllGesturesEnabled(true); //Enabling scrolling.

        zoomToCurrentLocation();
        startContinuousLocation();

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

    public Distance getDistance(){
        if(journeyPolyline == null)
            return new Distance(0);
        return new Distance(ItineraryFragment.distanceOfPolyline(journeyPolylineOptions)/1000);
    }

    public LatLng getPosition(){
        return lastLatLng;
    }

}
