package com.example.raphaelattali.rythmrun.activities.gui;

import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

public class RecapMapFragment extends SimpleMapFragment {
    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);
        if(ItineraryFragment.itinerary != null){
            map.addPolyline(ItineraryFragment.itinerary);
        }
    }
}
