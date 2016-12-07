package com.example.raphaelattali.rythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 07/12/2016.
 */

import com.example.raphaelattali.rythmrun.music.phase_vocoder.PhaseVocoder;

/**
 * Décrit une onde sinusoïdale, idéal pour une transformée de Fourier
 *
 * @see PhaseVocoder
 *
 * @author Raphael Attali
 * @version 1.0
 */
public class SinusWave {

    private double frequency = 0;
    private double amplitude = 0;
    private double phaseAtZero = 0;

    /**
     * <b>Constructeur de SinusWave</b>
     * On considère que le sinus n'est pas du tout déphasé,
     * c'est à dire phaseAtZero = 0
     *
     * @see SinusWave#frequency
     * @see SinusWave#amplitude
     * @see SinusWave#phaseAtZero
     *
     * @param frequency
     *      La fréquence de la sinusoïde
     * @param amplitude
     *      L'amplitude de la sinusoïde
     */
    public SinusWave(int frequency, int amplitude){
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    /**
     * <b>Constructeur de SinusWave</b>
     * On peut indiquer une certaine phase
     *
     * @see SinusWave#frequency
     * @see SinusWave#amplitude
     * @see SinusWave#phaseAtZero
     *
     * @param frequency
     *      La fréquence de la sinusoïde
     * @param amplitude
     *      L'amplitude de la sinusoïde
     * @param phaseAtZero
     *      La phase du sinus à l'origine
     */
    public SinusWave(int frequency, int amplitude, int phaseAtZero){
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.phaseAtZero = phaseAtZero;
    }

    /**
     * <b>Evalue le sinus en une abscisse x</b>
     *
     * @param t
     *      Le temps auquel on évalue l'onde
     * @return
     *      La valeur A*sin(2*pi*f*t + phi)
     */
    public double valuation(double t){

        double parameterOfSinusWave = 2*Math.PI*frequency*t + phaseAtZero;

        // A * sin( 2*pi*f*t + phi)
        return amplitude*Math.sin(parameterOfSinusWave);
    }



}
