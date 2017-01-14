package com.example.raphaelattali.rythmrun.music;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.example.raphaelattali.rythmrun.Pace;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Song {

    private int color;
    private Bitmap bitmap;
    private String title;
    private String artist;
    private String album;
    private String path;
    private String genre;
    private String duration;

    public static List<Song> songs;
    public static void loadSongs(){
        songs = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory().toString() + "/Music";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            Log.d("Files", "Size: " + files.length);
            for (File file:files) {
                //Adds the song to the list
                if (file.getName().endsWith(".mp3")) {
                    Log.d("Files", "FileName:" + file.getName());
                    songs.add(new Song(file));
                }
            }
        } else {
            Log.d("Files", "files is null: no files found ?");
        }

        sortSongs();
    }

    public static void sortSongs(){
        for(int i=1;i<songs.size();i++){
            int j=i;
            while(j>0 && (songs.get(i).getArtist().compareTo(songs.get(j-1).getArtist())<0 ||
                    (songs.get(i).getArtist().compareTo(songs.get(j).getArtist()) == 0 &&
                    songs.get(i).getTitle().compareTo(songs.get(j).getTitle())<0))){
                j-=1;
            }
            Song temp = songs.get(i);
            songs.set(i,songs.get(j));
            songs.set(j,temp);
        }
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

    public static ArrayList<String> getAllGenres(){
        if(songs==null)
            loadSongs();
        ArrayList<String> genres = new ArrayList<>();
        for(Song song:songs){
            if(song.getGenre() != null && !genres.contains(song.getGenre()))
                genres.add(song.getGenre());
        }
        return genres;
    }

    public Song(File file) {
        path = file.getPath();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        if(duration != null)
            duration = Pace.fancyPace(Double.parseDouble(duration)/60);

        InputStream inputStream;
        if (mmr.getEmbeddedPicture() != null) {
            inputStream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
            bitmap = BitmapFactory.decodeStream(inputStream);
        }
        else{
            color = Color.BLUE;
        }

        mmr.release();
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public String getPath(){
        return path;
    }

    public String getDuration(){
        return duration;
    }

    public String getGenre(){
        return genre;
    }

    public Drawable getColor(){
        return new ColorDrawable(color);
    }

    public boolean hasArt(){
        return bitmap!=null;
    }

}