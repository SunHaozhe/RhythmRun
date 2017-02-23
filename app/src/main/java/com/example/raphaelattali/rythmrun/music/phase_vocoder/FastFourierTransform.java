package com.example.raphaelattali.rythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 07/12/2016.
 */

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;

import java.util.List;

/**
 * Cette classe permet d'effectuer une transformée de Fourier rapide.
 */

public class FastFourierTransform {

    private final static String TAG = "Analyse de Fourier";
    private final static FastFourierTransformer mFFT = new FastFourierTransformer(DftNormalization.STANDARD);

    public static Complex[] fft(double[] music){

        return null;
    }

    public static Complex[] reverseFft(){

        return null;
    }

}
