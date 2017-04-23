package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MusicManager{

    private static ArrayList<Song> songs;
    private static MediaPlayer mediaPlayer;
    private static boolean songs_loaded=false;
    private static int currentSong=0;

    private MusicManager(){

    }

    public static void init(){
        loadSongs();
        initMediaPlayer();
    }

    static boolean loadSongs(){
        songs = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory().toString() + "/Music";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            Log.d("Files", "Size: " + files.length);
            for (File file:files) {
                //Adds the song to the list
                if (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav")) {
                    Log.d("Files", "FileName:" + file.getName());
                    songs.add(new Song(file));
                }
            }
            sortSongs();
            songs_loaded=true;
        } else {
            Log.d("Files", "files is null: no files found ?");
        }

        return songs_loaded;
    }

    public static boolean areSongsLoaded(){
        return songs_loaded;
    }

    private static void sortSongs(){
        for(int i=1;i<songs.size();i++){
            int j=i;
            while(j>0 && songs.get(i).getArtist()!=null && songs.get(j-1).getArtist()!=null &&
                    songs.get(i).getTitle()!=null && songs.get(j-1).getTitle()!=null &&
                    (songs.get(i).getArtist().compareTo(songs.get(j-1).getArtist())<0 ||
                    (songs.get(i).getArtist().compareTo(songs.get(j).getArtist()) == 0 &&
                            songs.get(i).getTitle().compareTo(songs.get(j).getTitle())<0))){
                j-=1;
            }
            Song temp = songs.get(i);
            songs.set(i,songs.get(j));
            songs.set(j,temp);
        }
    }

    static ArrayList<Song> getSongs(){
        return songs;
    }

    public static List<Song> getSongsByGenre(String genre){
        if(songs==null)
            loadSongs();
        List<Song> songsOfGenre = new ArrayList<>();
        for(Song song:songs){
            if(song.getGenre() != null){
                if(song.getGenre().equals(genre))
                    songsOfGenre.add(song);
            }
        }
        return songsOfGenre;
    }

    static ArrayList<String> getAllGenres(){
        if(songs==null)
            loadSongs();
        ArrayList<String> genres = new ArrayList<>();
        for(Song song:songs){
            if(song.getGenre() != null && !genres.contains(song.getGenre()))
                genres.add(song.getGenre());
        }
        return genres;
    }

    public static void setCurrentSong(int i){
        if(i>=0 && i<songs.size())
            currentSong = i;
    }

    public static Song getCurrentSong(){
        if(songs != null && songs.size()>0)
            return songs.get(currentSong);
        return null;
    }

    private static void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public static void playCurrentSong(){
        Song cs = getCurrentSong();
        if(cs == null)
            return;
        try {
            mediaPlayer.setDataSource(cs.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopPlaying(){
        mediaPlayer.reset();
    }
}
