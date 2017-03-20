package com.telecom_paristech.pact25.rhythmrun.Android_activities.test;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

        Button button = (Button)findViewById(R.id.button_launch_audio_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.telecom_paristech.pact25.rhythmrun.music.MusicManager.readMusic(path);
                Toast.makeText(AudioTestActivity.this, "Test lancé\nAvancement disponible dans les fichiers", Toast.LENGTH_SHORT).show();
            }
        });


    }


    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/One Punch Man ending.wav";
    File songFile = new File(path);
    Song song = new Song(songFile);



}
