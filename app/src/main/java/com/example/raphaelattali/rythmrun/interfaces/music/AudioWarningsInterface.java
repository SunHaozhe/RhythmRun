package com.example.raphaelattali.rythmrun.interfaces.music;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

import com.example.raphaelattali.rythmrun.music.Music;

/**
 * Interface pour la génération d'avertissements et d'information audio
 */

public interface AudioWarningsInterface {
    // Renvoie un message audio, à partir de l'alerte numérotée warningNumber
    Music generateAudioWarning(int warningNumber);
}
