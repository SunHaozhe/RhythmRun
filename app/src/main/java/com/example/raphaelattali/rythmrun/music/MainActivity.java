package com.example.raphaelattali.rythmrun.music;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.music.phase_vocoder.PhaseVocoder;

//EXEMPLE ANDROID

public class MainActivity extends AppCompatActivity {


    // Initialisation d'un bouton
    Button button = null;

    //Methode appelée au lancement de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity","created MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // le bouton est associé à celui sur l'interface graphique
        button = (Button)findViewById(R.id.button);
        // le bouton réagit au clic et envoie sur l'activité Main2Activity
        // Main2Activity n'est pas définitive
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });

        // Bouton test du vocodeur
        Button phaseVocodButton =(Button)findViewById(R.id.test_phase_vocod_button);
        phaseVocodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhaseVocoder pv = new PhaseVocoder();
                pv.modifyMusicToFitTempo(null,0);
            }
        });
        // FIN test

        Button testBeatButton = (Button)findViewById(R.id.test_beat_synchronized_with_paces_button);
        testBeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TestBeatActivity.class));
            }
        });
    }
}
