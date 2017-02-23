package com.example.raphaelattali.rythmrun.music;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

/**
 * La classe Music représente une partie d'un morceau audio
 *
 * Ainsi on va pouvoir manipuler de la musique directement
 * et non pas manipuler des fichiers audio
 *
 *
 */

public class Music {
    private double[] tab;

    public Music(double[] tab){
        this.tab = tab;
    }

    public double[] getTab() {
        return tab;
    }
}
