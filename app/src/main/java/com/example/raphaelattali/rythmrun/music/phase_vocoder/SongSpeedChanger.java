package com.example.raphaelattali.rythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */


import com.example.raphaelattali.rythmrun.interfaces.music.PhaseVocoderInterface;
import com.example.raphaelattali.rythmrun.music.Music;

import org.apache.commons.math3.complex.Complex;

/**
 * Classe permettant d'accélérer/de ralentir une chanson. Attention, le résultat ne sera pas agréable à l'oreille si
 * les différences de vitesse sont de plus de 20-30%
 */
public class SongSpeedChanger implements PhaseVocoderInterface {


    @Override
    public Music modifyMusicToFitTempo(Music music, float tempo) {

        double[] musicTab = music.getTab();
        Complex[] fft = FastFourierTransform.fft(musicTab);

        return null;
    }
}
