package com.example.raphaelattali.rythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */


import android.graphics.Interpolator;

import com.example.raphaelattali.rythmrun.interfaces.music.PhaseVocoderInterface;
import com.example.raphaelattali.rythmrun.music.Music;
import com.example.raphaelattali.rythmrun.music.Song;
import com.example.raphaelattali.rythmrun.music.waveFileReaderLib.WavFile;

import org.apache.commons.math3.analysis.interpolation.InterpolatingMicrosphere;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;

import ru.sscc.spline.polynomial.PSplineCalculator;

/**
 * Classe permettant d'accélérer/de ralentir une chanson. Attention, le résultat ne sera pas agréable à l'oreille si
 * les différences de vitesse sont de plus de 20-30%
 */
public class SongSpeedChanger implements PhaseVocoderInterface {


    WavFile file;

    @Override
    public Music modifyMusicToFitTempo(Song song, float tempo) {

        String path = song.getPath();

        double[] musicTab = song.getPath();


        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction f = interpolator.interpolate(musicTab,song.getYTab());


        return null;
    }
}
