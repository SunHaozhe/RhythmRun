package com.example.raphaelattali.rythmrun.interfaces.music;

import com.example.raphaelattali.rythmrun.music.Music;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

 /*
    * Interface permettant de jouer la musique et de l'arrêter
 */

public interface MusicPlayerInterface {
    //jouer la musique qui se trouve à fileName
    public void playMusic(String fileName);

    //jouer le morceau donné en paramètre
    public void playMusic(Music music);

    //arrêter la musique
    public void stopMusic();
}
