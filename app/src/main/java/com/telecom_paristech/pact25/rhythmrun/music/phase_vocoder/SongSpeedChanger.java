package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */


import android.graphics.Interpolator;

import com.telecom_paristech.pact25.rhythmrun.interfaces.music.PhaseVocoderInterface;
import com.telecom_paristech.pact25.rhythmrun.music.Music;
import com.telecom_paristech.pact25.rhythmrun.music.Song;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;

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
public class SongSpeedChanger {


    WavFile file;

    public Music modifyMusicToFitTempo(Song song, float tempo) {

        String path = song.getPath();

        double musicTab = song.getFreq();


        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction f = interpolator.interpolate(null,null);


        return null;
    }
}
