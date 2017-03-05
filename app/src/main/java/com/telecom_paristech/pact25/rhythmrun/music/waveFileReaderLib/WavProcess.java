package com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by Raphael Attali on 05/03/2017.
 * <p>
 * Project RythmRun
 */

public class WavProcess {


        public static void write(String path, int sampleRate ,final double duration, double[] buffer)
        {
            String TAG = "WriteWAV";
            try
            {

                Log.i(TAG,"Début du process");
                // Calculate the number of frames required for specified duration
                long numFrames = (long)(duration * sampleRate);

                // Create a wav file with the name specified as the first argument
                WavFile wavFile = WavFile.newWavFile(new File(path), 2, numFrames, 16, sampleRate);

                // Initialise a local frame counter
                long frameCounter = 0;
                int bufferFrames = buffer.length;

                // Loop until all frames written
                while (frameCounter < numFrames)
                {


                    // Write the buffer
                    wavFile.writeFrames(buffer, bufferFrames);
                }

                // Close the wavFile
                wavFile.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

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

        }





    public static void wavRead(String path){

        String TAG = "WavRead";
        String writingPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/modified sample.wav";
        double[] buffer = null;

        try
        {
            Log.d(TAG,"Début de la lecture du fichier wav");
            // Open the wav file specified as the first argument
            WavFile wavFile = WavFile.openWavFile(new File(path));

            // Display information about the wav file
            wavFile.display();

            // Get the number of audio channels in the wav file
            int numChannels = wavFile.getNumChannels();

            // Create a buffer of 300 frames
            buffer = new double[300 * numChannels];

            int framesRead;
            do
            {
                Log.d(TAG,"Boucle de lecture du WAV");
                // Read frames into buffer
                framesRead = wavFile.readFrames(buffer, 300);
                write(writingPath,44100,3/441,buffer);

            }
            while (framesRead != 0);

            // Close the wavFile
            wavFile.close();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    }

