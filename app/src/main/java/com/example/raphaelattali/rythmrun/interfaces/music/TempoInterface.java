package com.example.raphaelattali.rythmrun.interfaces.music;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

import com.example.raphaelattali.rythmrun.music.Music;

/**
 * Interface liée à la gestion du tempo dans une musique
 */


public interface TempoInterface {
    /**
     * On peut récupérer la musique dans un fichier fileName et renvoyer son tempo en bmp
     */
    float determineTempoOf(String fileName);

    /**
     * On peut récupérer une musique de classe Music et renvoyer son tempo en bmp
     */
    float determineTempoOf(Music music);
}
