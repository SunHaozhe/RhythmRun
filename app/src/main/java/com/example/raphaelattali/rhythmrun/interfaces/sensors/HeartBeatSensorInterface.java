package com.example.raphaelattali.rhythmrun.interfaces.sensors;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

public interface HeartBeatSensorInterface {

    // Se connecte au capteur présent dans la ceinture du coureur
    void connect();

    // Renvoie le nombre de battements par minute
    float getInstantHeartbeat();
}
