package com.telecom_paristech.pact25.rhythmrun.interfaces.music;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

import com.telecom_paristech.pact25.rhythmrun.music.Music;

/**
 * Interface pour la génération d'avertissements et d'information audio
 */

public interface AudioWarningsInterface {
    // Renvoie un message audio, à partir de l'alerte numérotée warningNumber
    Music generateAudioWarning(int warningNumber);
}
