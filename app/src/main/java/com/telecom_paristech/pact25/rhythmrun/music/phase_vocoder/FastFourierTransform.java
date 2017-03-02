package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 07/12/2016.
 */

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.List;

/**
 * Cette classe permet d'effectuer une transformée de Fourier rapide.
 */

class FastFourierTransform {

    private final static String TAG = "Analyse de Fourier";
    private final static FastFourierTransformer mFFT = new FastFourierTransformer(DftNormalization.STANDARD);

    static Complex[] fft(double[] music){
        return mFFT.transform(music, TransformType.FORWARD);
    }

    static Complex[] fftReversed(double[] music){
        return mFFT.transform(music,TransformType.INVERSE);
    }

}
