package com.example.raphaelattali.rythmrun.interfaces.sensors;

import android.location.Location;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

public interface GpsInterface {

    //Renvoie la localisation du coureur
    public Location getLocation();

    //Renvoie la vitesse actuelle du coureur
    public float getInstantSpeed();

    //Renvoie la vitesse moyenne du coureur
    public float getAverageSpeed();

    //Renvoie la distance parcouru en mètres
    public int metersCrossed();
}
