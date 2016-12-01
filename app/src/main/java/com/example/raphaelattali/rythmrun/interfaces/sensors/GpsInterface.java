package com.example.raphaelattali.rythmrun.interfaces.sensors;

import android.location.Location;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

public interface GpsInterface {

    //Renvoie la localisation du coureur
    Location getLocation();

    //Renvoie la vitesse actuelle du coureur
    float getInstantSpeed();

    //Renvoie la vitesse moyenne du coureur
    float getAverageSpeed();

    //Renvoie la distance parcouru en mètres
    int metersCrossed();
}
