package com.example.raphaelattali.rythmrun.interfaces.music;

/**
 * Created by Raphael Attali on 01/12/2016.
 *
 */

import com.example.raphaelattali.rythmrun.music.Music;

/**
 * Interface liée au vocodeur de phase
 * Il faut qu'il puisse modifier la musique et la renvoyer
 *
 * @see com.example.raphaelattali.rythmrun.music.phase_vocoder.PhaseVocoder
 *
 * @author Raphael Attali
 * @version 1.0
 */

public interface PhaseVocoderInterface {
    /**
     * Doit renvoyer une musique modifiée, telle que le tempo soit
     * désormais celui demandé
     * On donne en argument le rythme (en bmp) et la musique d'origine,
     * et la méthode la transforme et la renvoie
     */
    Music modifyMusicToFitTempo(Music music, float tempo);

}
