package com.telecom_paristech.pact25.rhythmrun.Android_activities.test;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.MusicManager;
import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;
import com.telecom_paristech.pact25.rhythmrun.R;
import com.telecom_paristech.pact25.rhythmrun.music.MusicReader;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.SongSpeedChanger;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;
import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFileException;

import java.io.File;
import java.io.IOException;

public class AudioTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);


        Button button = (Button)findViewById(R.id.button_launch_audio_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread (new Runnable() {
                    @Override
                    public void run() {
                        int dureeBuffer = 1;
                        int bufferSize = 44100 * dureeBuffer;
                        String waveFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Sikidim Tarkan.wav";
                        MusicReader musicReader = new MusicReader(bufferSize);
                        SongSpeedChanger songSpeedChanger;
                        try {
                            boolean first = true;
                            songSpeedChanger = new SongSpeedChanger(waveFilePath, bufferSize, 0.5);
                            while ((!songSpeedChanger.songEnded())) {
                                if (musicReader.getNumberOfBuffers() < 2) {
                                    musicReader.addBuffer(songSpeedChanger.getNextBuffer());
                                }
                                if (first) {
                                    first = false;
                                    musicReader.play();
                                }
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.i("ACTIVITY AUDIO TEST", "STOP _ REACHED THE END");
                            musicReader.stopAtTheEnd();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })).start();

            


    }


});
    }
    }
