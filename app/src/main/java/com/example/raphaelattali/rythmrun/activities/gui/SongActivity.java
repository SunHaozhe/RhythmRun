package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.music.Song;

public class SongActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        imageView = (ImageView) findViewById(R.id.ivSong);
        tvTitle = (TextView) findViewById(R.id.tvSongTitle);
        tvArtist = (TextView) findViewById(R.id.tvSongArtist);
        tvAlbum = (TextView) findViewById(R.id.tvSongAlbum);

        Intent intent = getIntent();
        String songPath = intent.getStringExtra(LibraryActivity.EXTRA_SONG);
        Song song = new Song(songPath);

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        tvAlbum.setText(song.getAlbum());

        if(song.hasArt()){
            imageView.setImageBitmap(song.getBitmap());
        }
        else{
            imageView.setImageDrawable(song.getColor());
        }

    }
}
