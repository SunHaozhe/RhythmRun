package com.example.raphaelattali.rythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */


import com.example.raphaelattali.rythmrun.interfaces.music.PhaseVocoderInterface;
import com.example.raphaelattali.rythmrun.music.Music;

/**
 * Classe permettant d'accélérer/de ralentir une chanson. Attention, le résultat ne sera pas agréable à l'oreille si
 * les différences de vitesses sont de plus de 20-30%
 */
public class SongSpeedChanger implements PhaseVocoderInterface {


    @Override
    public Music modifyMusicToFitTempo(Music music, float tempo) {

        byte[] musicTab = music.getTab();
        
        return null;
    }
}
