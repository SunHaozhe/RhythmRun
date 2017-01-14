package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.music.Song;

import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    public final static String EXTRA_SONG = "song";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //Check if external storage is available for reading
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("Files", "external storage is readable");
        } else {
            Log.d("Files", "external storage is NOT readable");
        }

        if(Song.songs==null)
            Song.loadSongs();

        SongAdapter songAdapter = new SongAdapter(this, Song.songs);
        ListView listView = (ListView) findViewById(R.id.lvLibrary);
        listView.setAdapter(songAdapter);
    }

    public class SongAdapter extends ArrayAdapter<Song> {

        SongAdapter(Context context, List<Song> songs) {
            super(context, 0, songs);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_library, parent, false);
            }

            SongViewHolder viewHolder = (SongViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new SongViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.artist = (TextView) convertView.findViewById(R.id.artist);
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
                convertView.setTag(viewHolder);
            }

            final Song song = getItem(position);

            if (song != null) {
                viewHolder.title.setText(song.getTitle());
                viewHolder.artist.setText(song.getArtist());
                if (song.hasArt()) {
                    viewHolder.thumbnail.setImageBitmap(song.getBitmap());
                } else {
                    viewHolder.thumbnail.setImageDrawable(song.getColor());
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SongActivity.class);
                    if (song != null) {
                        intent.putExtra(EXTRA_SONG, song.getPath());
                    }
                    startActivity(intent);
                }
            });

            return convertView;
        }

        private class SongViewHolder {
            TextView title;
            TextView artist;
            ImageView thumbnail;
        }
    }
}
