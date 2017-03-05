package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Song implements Parcelable {

    private int color;
    private Bitmap bitmap;
    private String title;
    private String artist;
    private String album;
    private String path;
    private String genre;
    private String duration;
    private double[] values;

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
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
            duration = Pace.fancyPace(Double.parseDouble(duration)/60000);

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

    protected Song(Parcel in) {
        color = in.readInt();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        path = in.readString();
        genre = in.readString();
        duration = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    public String getString(){
        return title+" - "+artist;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(color);
        parcel.writeParcelable(bitmap, i);
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(path);
        parcel.writeString(genre);
        parcel.writeString(duration);
    }

    public void play(){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}