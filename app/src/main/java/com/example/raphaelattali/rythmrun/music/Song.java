package com.example.raphaelattali.rythmrun.music;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;

import com.example.raphaelattali.rythmrun.Pace;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class Song {
    private int color;
    private Bitmap bitmap;
    private String title;
    private String artist;
    private String album;
    private String path;
    private String genre;
    private String duration;

    public Song(File file) {
        path = file.getPath();
        initiate();
    }

    public Song(String path){
        this.path = path;
        initiate();
    }

    private void initiate(){
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