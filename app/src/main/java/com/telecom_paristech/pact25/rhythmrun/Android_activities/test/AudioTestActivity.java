package com.telecom_paristech.pact25.rhythmrun.Android_activities.test;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;
import com.telecom_paristech.pact25.rhythmrun.R;

import java.io.File;

public class AudioTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);
        song.play();
    }


    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/One Punch Man ending.wav";
    File songFile = new File(path);
    Song song = new Song(songFile);



}
