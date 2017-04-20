package com.telecom_paristech.pact25.rhythmrun.interfaces.music;

/**
 * Created by lucas on 04/03/17.
 */

public interface MusicManagerInterface {
    void play();
    void updateRythm(float paceFrequency);   // methode a appeler avec la frequence donnee par le podometre,
                                                    //toutes les demi-secondes par exemple
    void pause();
    void stop();
}
