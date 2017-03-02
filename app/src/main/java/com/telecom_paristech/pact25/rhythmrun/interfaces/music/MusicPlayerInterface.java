package com.telecom_paristech.pact25.rhythmrun.interfaces.music;

import com.telecom_paristech.pact25.rhythmrun.music.Music;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

 /*
    * Interface permettant de jouer la musique et de l'arrêter
 */

public interface MusicPlayerInterface {
    //jouer la musique qui se trouve à fileName
    void playMusic(String fileName);

    //jouer le morceau donné en paramètre
    void playMusic(Music music);

    //arrêter la musique
    void stopMusic();
}
