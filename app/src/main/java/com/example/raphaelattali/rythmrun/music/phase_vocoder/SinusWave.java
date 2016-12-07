package com.example.raphaelattali.rythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 07/12/2016.
 */

import com.example.raphaelattali.rythmrun.music.phase_vocoder.PhaseVocoder;

/**
 * Onde sinusoïdale, de fréquence f, d'amplitude A et avec un certain dephasage.
 * Objet idéal pour une transformée de Fourier.
 * Hérite de PeriodicWave
 *
 * @see PeriodicWave
 * @see FourierAnalysis
 * @see PhaseVocoder
 *
 * @author Raphael Attali
 * @version 1.0
 */
public class SinusWave extends PeriodicWave{

    // La phase à l'origine est importante dans les calculs suivants
    private double phaseAtZero = 0;


    /**
     * <b>Constructeur de SinusWave</b>
     * Onde sinusoidale avec une phase nulle à l'origine
     *
     * @see PeriodicWave#frequency
     * @see PeriodicWave#amplitude
     * @see SinusWave#phaseAtZero
     *
     * @param frequency
     *      La fréquence de la sinusoïde
     * @param amplitude
     *      L'amplitude de la sinusoïde
     */

    public SinusWave(double frequency, double amplitude){
        super(frequency, amplitude);
        phaseAtZero = 0;
    }


    /**
     * <b>Constructeur de SinusWave</b>
     * Contrairement à une onde périodique, on peut désormais donner la phase à l'origine
     *
     * @see PeriodicWave#frequency
     * @see PeriodicWave#amplitude
     * @see SinusWave#phaseAtZero
     *
     * @param frequency
     *      La fréquence de la sinusoïde
     * @param amplitude
     *      L'amplitude de la sinusoïde
     * @param phaseAtZero
     *      La phase du sinus à l'origine
     */
    
    public SinusWave(double frequency, double amplitude, double phaseAtZero){
        super(frequency, amplitude);
        this.phaseAtZero = phaseAtZero;
    }

    /**
     * <b>Evalue l'onde en un temps donné t</b>
     *
     * @param t
     *      Le temps auquel on évalue l'onde
     * @return
     *      La valeur A*sin(2*pi*f*t + phi)
     */
    public double valuation(double t){

        double parameterInSinus = 2*Math.PI*getFrequency()*t + phaseAtZero;
        // A * sin( 2*pi*f*t + phi)
        return getAmplitude()*Math.sin(parameterInSinus);
    }



}
