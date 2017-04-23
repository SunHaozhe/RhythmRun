package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.music.tempo.Tempo;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

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
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if(duration != null)
            duration = Pace.fancyPace(Double.parseDouble(duration)/60000);

        double freq = Tempo.findTempoHzFast(path);
        HomeActivity.getDB().addSongAndTempo(path, freq);


        if (path.endsWith(".mp3")){
            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            genre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

            InputStream inputStream;
            if (mmr.getEmbeddedPicture() != null) {
                inputStream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            else{
                color = Color.BLUE;
            }

        } else if (path.endsWith(".wav")){
            String[] tmp = null;
            try {
                tmp = getTags(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (tmp != null){
                artist = tmp[0];
                album = tmp[1];
                title = tmp[2];
                genre = tmp[3];
            }

            //InputStream inputStream;
            String[] pathSplit = path.split("/");
            String picPath = "/storage/emulated/0/Music/" + pathSplit[pathSplit.length-1].substring(0,2) + ".jpg";
            //inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(picPath)));
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(picPath)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Log.v("Song", "Detected title:    " + title);
        Log.v("Song", "Detected artist:   " + artist);
        Log.v("Song", "Detected album:    " + album);
        Log.v("Song", "Detected genre:    " + genre);
        Log.v("Song", "Detected duration: " + duration);
        Log.v("Song", "Detected freq:     " + freq);



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

    /*public void play(){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    private String[] getTags(String filename) throws IOException {
        String[] tmp = filename.split("/");
        String songId = tmp[tmp.length-1].substring(0,2);
        Log.d("Song","Getting tags for id: " + songId);

        File tagFile = new File("/storage/emulated/0/Music/tags.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(tagFile));
        String line;
        while ((line = bufferedReader.readLine()) != null){
            if(line.substring(1,3).equals(songId) || line.substring(0,2).equals(songId)){
                String[] split = line.split("  -  ");
                String artist = split[1];
                String album = split[2];
                String title = split[3];
                String genre = split[6];
                bufferedReader.close();
                return new String[] {artist, album, title, genre};
            }
        }
        return null;
    }
}