package com.example.raphaelattali.rythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 07/12/2016.
 */

import com.example.raphaelattali.rythmrun.interfaces.music.PhaseVocoderInterface;
import com.example.raphaelattali.rythmrun.music.Music;

/**
 * <b>Le vocodeur de phase permet de compresser ou de dilater l'échelle d'une musique</b>
 *
 *
 *  @author Raphael Attali
 *  @version 1.0
 */

public class PhaseVocoder implements PhaseVocoderInterface {


    public PhaseVocoder(){

    }


    /**
     * <i>Fonction principale du vocodeur de phase.</i>
     * Modifie une musique pour qu'elle puisse avoir le tempo indiqué
     *
     * @param music
     *      La musique a modifier
     * @param tempo
     *      Le tempo a imposer à la musique
     *
     * @return La musique modifiée
     */
    @Override
    public Music modifyMusicToFitTempo(Music music, float tempo) {

        return null;
    }

}
