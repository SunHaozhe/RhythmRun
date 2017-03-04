package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.music.Song;

public class SongActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        ImageView imageView = (ImageView) findViewById(R.id.ivSong);
        TextView tvTitle = (TextView) findViewById(R.id.tvSongTitle);
        TextView tvArtist = (TextView) findViewById(R.id.tvSongArtist);
        TextView tvAlbum = (TextView) findViewById(R.id.tvSongAlbum);
        TextView tvGenre = (TextView) findViewById(R.id.tvSongGenre);
        TextView tvDuration = (TextView) findViewById(R.id.tvSongDuration);

        Intent intent = getIntent();
        Song song = Song.songs.get(intent.getIntExtra(LibraryActivity.EXTRA_SONG,0));

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        tvAlbum.setText(song.getAlbum());
        tvGenre.setText(song.getGenre());
        tvDuration.setText(song.getDuration());

        if(song.hasArt()){
            imageView.setImageBitmap(song.getBitmap());
        }
        else{
            imageView.setImageDrawable(song.getColor());
        }

    }
}