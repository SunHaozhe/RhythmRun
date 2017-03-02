package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 07/12/2016.
 */

import java.util.List;

/**
 * La classe possède tous les outils pour faire une transformée de Fourier
 * Elle passe d'un signal périodique à une liste de sinusoïdes, et vice versa
 *
 * @see SinusWave
 *
 * @author Raphael Attali
 * @version 1.0
 */

public class FourierAnalysis {

    /**
     * <b>Permet de décomposer une onde périodique en une somme d'ondes sinusoidales</b>
     *
     * @param rawWave
     *      L'onde périodique brute, à décomposer
     * @return La liste des ondes sinuoidales qui composent l'onde initiale
     */
    public static List<SinusWave> decompose(PeriodicWave rawWave){

        // Ne fonctionne pas encore
        return null;
    }

}
