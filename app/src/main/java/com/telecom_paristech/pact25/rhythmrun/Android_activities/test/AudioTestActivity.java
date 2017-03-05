package com.telecom_paristech.pact25.rhythmrun.Android_activities.test;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.MusicManager;
import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;
import com.telecom_paristech.pact25.rhythmrun.R;

import java.io.File;
import java.io.IOException;

public class AudioTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);

       /* MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        com.telecom_paristech.pact25.rhythmrun.music.MusicManager.readMusic(path);

    }


    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/One Punch Man ending.wav";
    File songFile = new File(path);
    Song song = new Song(songFile);



}
