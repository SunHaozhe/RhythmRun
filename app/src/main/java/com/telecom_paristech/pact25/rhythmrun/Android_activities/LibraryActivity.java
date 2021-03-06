package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;

import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LibraryActivity", "created LibraryActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //Check if external storage is available for reading
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("Files", "external storage is readable");
            Log.d("LibraryActivity", "external storage is readable");
        } else {
            Log.d("Files", "external storage is NOT readable");
            Log.d("LibraryActivity", "external storage is NOT readable");
        }

        createAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.library_menu,menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_library_refresh:
                MusicManager.loadSongs();
                createAdapter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createAdapter(){
        SongAdapter songAdapter = new SongAdapter(this, MusicManager.getSongs());
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
                        intent.putExtra(Macros.EXTRA_SONG, MusicManager.getSongs().indexOf(song));
                    }
                    Log.d("LibraryActivity", "We lances the intent which contains the song selected");
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
