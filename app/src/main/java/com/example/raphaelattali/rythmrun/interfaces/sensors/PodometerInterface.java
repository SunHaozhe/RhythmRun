package com.example.raphaelattali.rythmrun.interfaces.sensors;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

public interface PodometerInterface {
    //Récupérer la fréquence de pas actuelle du coureur
    public float getRunningPaceFrequency();
    //Pour connaitre le temps ecoule depuis le dernier pas avec comme reference l'allumage du terminal -voir elapsedRealTime()
    public float lastStepTimeSinceBoot();
}
