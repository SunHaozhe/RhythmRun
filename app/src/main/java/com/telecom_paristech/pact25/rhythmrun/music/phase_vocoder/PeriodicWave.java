package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 07/12/2016.
 */


/**
 * Une onde périodique non sinusoïdale
 *
 * @author Raphael Attali
 *
 */
public class PeriodicWave {
    
    private double amplitude = 0;
    private double frequency = 0;

    /**
     * <b>Constructeur de SinusWave</b>
     * On peut indiquer une certaine phase
     *
     * @see PeriodicWave#amplitude
     * @see PeriodicWave#frequency
     *
     * @param frequency
     *      La fréquence de l'onde périodique
     * @param amplitude
     *      L'amplitude de l'onde périodique
     */
    public PeriodicWave(double frequency, double amplitude){
        this.frequency = frequency;
        this.amplitude = amplitude;
    }


    /**
     * Getter
     * @see PeriodicWave#amplitude
     * @return L'amplitude de l'onde
     */
    public double getAmplitude() {
        return amplitude;
    }

    /**
     * Permet de modifier l'amplitude de l'onde
     * @param amplitude
     *      Nouvelle amplitude de l'onde
     * @see PeriodicWave#amplitude
     */
    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }


    /**
     * Getter
     * @see PeriodicWave#frequency
     * @return La fréquence de l'onde
     */
    public double getFrequency() {
        return frequency;
    }


    /**
     * Permet de modifier la fréquence de l'onde
     * @param frequency
     *      Nouvelle fréquence de l'onde
     * @see PeriodicWave#frequency
     */
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }


}
