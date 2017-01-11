package com.example.raphaelattali.rythmrun.activities.gui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
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
import com.example.raphaelattali.rythmrun.music.Song;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class LibraryActivity extends AppCompatActivity {

    private List<Song> songs;
    private ListView listView;

    public final static String EXTRA_SONG = "song";

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

            final Song song = getItem(position);

            viewHolder.title.setText(song.getTitle());
            viewHolder.artist.setText(song.getArtist());
            if(song.hasArt()){
                viewHolder.thumbnail.setImageBitmap(song.getBitmap());
            }
            else{
                viewHolder.thumbnail.setImageDrawable(song.getColor());
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SongActivity.class);
                    intent.putExtra(EXTRA_SONG,song.getPath());
                    startActivity(intent);
                }
            });

            return convertView;
        }

        private class SongViewHolder{
            public TextView title;
            public TextView artist;
            public ImageView thumbnail;
        }
    }
}
