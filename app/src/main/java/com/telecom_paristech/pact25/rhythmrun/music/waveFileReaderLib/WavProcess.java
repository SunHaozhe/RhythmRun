package com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;
import com.telecom_paristech.pact25.rhythmrun.music.AudioReader;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.SelfMadeInterpolator;
import com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.SongSpeedChanger;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by Raphael Attali on 05/03/2017.
 * <p>
 * Project RythmRun
 */

public class WavProcess {

    private double coef = 1.5;
    private double[] buffer;
    private int compteur = 0;
    private String path;
    private int bufferLength = 500000;
    private int sampleRate = 44100;
    private int duration = bufferLength/sampleRate;

    public WavProcess(String path){
        this.path = path;
        compteur = 0;
    }

    private void write(String path)
        {
            String TAG = "WriteWAV";
            try
            {
                // Calculate the number of frames required for specified duration
                long numFrames = (long)(duration * sampleRate) * ((long) coef);

                // Create a wav file with the name specified as the first argument
                WavFile wavFile = WavFile.newWavFile(new File(path), 2, numFrames, 16, sampleRate);

                buffer = SongSpeedChanger.modifyBufferSpeed(buffer,coef);


                    // Write the buffer
                wavFile.writeFrames(buffer, bufferLength);

                // Close the wavFile
                wavFile.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
/*
            MediaPlayer player = new MediaPlayer();
            try{
                Log.d("MediaPlayer","Media bound to source");
                player.setDataSource(path);
                player.prepare();
                player.start();

                Thread.sleep((long) duration);
                player.stop();
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
        }


    public int wavRead(){

        String TAG = "WavRead";
        String writingPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Rhythm Run Samples/modified sample 0.wav";
        buffer = null;

        try
        {
            Log.d(TAG,"Début de la lecture du fichier wav");
            // Open the wav file specified as the first argument
            WavFile wavFile = WavFile.openWavFile(new File(path));

            // Display information about the wav file
            wavFile.display();

            // Get the number of audio channels in the wav file
            int numChannels = wavFile.getNumChannels();

            // Create a buffer of 250000 frames
            buffer = new double[bufferLength * numChannels];
            int framesRead;
            do
            {
                compteur++;
                Log.i(TAG,"Boucle de lecture du WAV n° "+String.valueOf(compteur));
                // Read frames into buffer
                framesRead = wavFile.readFrames(buffer, bufferLength);
                buffer = SongSpeedChanger.modifyBufferSpeed(buffer,1.5);

                write(writingPath);
                writingPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Rhythm Run Samples/modified sample " +String.valueOf(compteur) +".wav";


            }
            while (framesRead != 0);

            // Close the wavFile
            wavFile.close();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return compteur;
    }
}

