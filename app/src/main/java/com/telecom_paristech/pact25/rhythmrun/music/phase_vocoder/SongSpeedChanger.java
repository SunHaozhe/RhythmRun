package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */


import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;
import com.telecom_paristech.pact25.rhythmrun.music.Music;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

/**
 * Classe permettant d'accélérer/de ralentir une chanson. Attention, le résultat ne sera pas agréable à l'oreille si
 * les différences de vitesse sont de plus de 20-30%
 */
public class SongSpeedChanger {


    WavFile file;

    public Music modifyMusicToFitTempo(Song song, float tempo) {

        String path = song.getPath();




        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction f = interpolator.interpolate(null,null);


        return null;
    }
}
