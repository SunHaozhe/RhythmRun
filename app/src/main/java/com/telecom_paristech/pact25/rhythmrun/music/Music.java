package com.telecom_paristech.pact25.rhythmrun.music;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;

/**
 * La classe Music représente une partie d'un morceau audio
 *
 * Ainsi on va pouvoir manipuler de la musique directement
 * et non pas manipuler des fichiers audio
 *
 *
 */

public class Music {

    private double[] xTab;
    private double[] yTab;

    public Music(double[] xTab){
             this.xTab = xTab;
             this.yTab = new double[xTab.length];
    }

    public double[] getXTab() {
        return xTab;
    }


    public double[] getYTab() {
        return yTab;
    }
}
