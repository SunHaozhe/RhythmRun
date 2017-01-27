package com.example.raphaelattali.rythmrun.activities.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class RunMapFragment extends SimpleMapFragment implements OnMapReadyCallback {


    public RunMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);
        if(ItineraryFragment.itinerary != null){
            map.addPolyline(ItineraryFragment.itinerary);
        }
    }

}
