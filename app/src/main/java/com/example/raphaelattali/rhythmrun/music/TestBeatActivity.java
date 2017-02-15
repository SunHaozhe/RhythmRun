package com.example.raphaelattali.rhythmrun.music;

import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.raphaelattali.rhythmrun.R;

public class TestBeatActivity extends AppCompatActivity implements View.OnClickListener {

    SoundPool soundPool;
    private int IDSOUND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TestBeatActivity","created TestBeatActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_beat);

        initializeSound();
    }


    private void initializeSound(){
        soundPool = new SoundPool(1,1,1); //DEPRECATED !! A utiliser avec un Builder

        IDSOUND = soundPool.load(this,R.raw.son_de_mort,0);
    }


    @Override
    public void onClick(View v) {
        soundPool.play(IDSOUND,1,1,5,0,1);
    }
}
