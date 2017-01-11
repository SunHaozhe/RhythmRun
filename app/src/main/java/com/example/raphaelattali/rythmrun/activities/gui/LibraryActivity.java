package com.example.raphaelattali.rythmrun.activities.gui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.activities.MainActivity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class LibraryActivity extends AppCompatActivity {

    private List<Song> songs;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);


        //Check if external storage is available for reading
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("Files","external storage is readable");
        }
        else{
            Log.d("Files","external storage is NOT readable");
        }

        List<Song> songs = new ArrayList<Song>();

        String path = Environment.getExternalStorageDirectory().toString()+"/Music";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files != null){
            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                //Adds the song to the list
                if(files[i].getName().toString().endsWith(".mp3")){
                    Log.d("Files", "FileName:" + files[i].getName());
                    songs.add(new Song(files[i]));
                }
            }
        }
        else{
            Log.d("Files", "files is null: no files found ?");
        }

        SongAdapter songAdapter = new SongAdapter(this,songs);
        listView = (ListView) findViewById(R.id.lvLibrary);
        listView.setAdapter(songAdapter);

    }

    public class Song{
        private int color;
        private Bitmap bitmap;
        private String title;
        private String artist;
        private String album;

        public Song(File file) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(file.getPath());

            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

            InputStream inputStream = null;
            if (mmr.getEmbeddedPicture() != null) {
                inputStream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            else{
                color = Color.BLUE;
            }
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

        public Drawable getColor(){
            return new ColorDrawable(color);
        }

        public boolean hasArt(){
            return bitmap!=null;
        }

    }

    public class SongAdapter extends ArrayAdapter<Song> {

        public SongAdapter(Context context, List<Song> songs) {
            super(context, 0, songs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_library,parent, false);
            }

            SongViewHolder viewHolder = (SongViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new SongViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
                convertView.setTag(viewHolder);
            }

            Song song = getItem(position);

            viewHolder.title.setText(song.getTitle());
            viewHolder.artist.setText(song.getArtist());
            if(song.hasArt()){
                viewHolder.thumbnail.setImageBitmap(song.getBitmap());
            }
            else{
                viewHolder.thumbnail.setImageDrawable(song.getColor());
            }

            return convertView;
        }

        private class SongViewHolder{
            public TextView title;
            public TextView artist;
            public ImageView thumbnail;
        }
    }
}
