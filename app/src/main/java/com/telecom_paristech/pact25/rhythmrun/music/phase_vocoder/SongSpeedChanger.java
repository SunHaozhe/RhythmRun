package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */


import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;
import com.telecom_paristech.pact25.rhythmrun.music.Music;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavProcess;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

/**
 * Classe permettant d'accélérer/de ralentir une chanson. Attention, le résultat ne sera pas agréable à l'oreille si
 * les différences de vitesse sont de plus de 20-30%
 */
public class SongSpeedChanger {
        String songPath;
    float songTempoHz;
    public SongSpeedChanger(String songPath, float songTempoHz) {
        this.songPath = songPath;
        this.songTempoHz = songTempoHz;
    }

    public static double[] modifyBufferSpeed(double[] buffer, double coef){

        Log.i("PROCESSING", "Interpolation en cours ...");
        int N = buffer.length;

        return SelfMadeInterpolator.interp1(
                SelfMadeInterpolator.getIndexesTab(N),
                buffer,
                SelfMadeInterpolator.getIndexesInterpTab(N,coef));
    }



    public double[] modifyMusicToFitTempo(Song song, float tempo) {

        String path = song.getPath();


        // y = audioread(...)

        int N = 5;//length(y)

        // t = (0:N-1)/N
        double[] t = SelfMadeInterpolator.getIndexesTab(N);
//TODO : a compléter avec les données SQLlite
        double coef = tempo;// /freq;


        double[] tInterpol = SelfMadeInterpolator.getIndexesInterpTab(N,coef);

        return SelfMadeInterpolator.interp1(t,song.getValues(),tInterpol);
    }
}
