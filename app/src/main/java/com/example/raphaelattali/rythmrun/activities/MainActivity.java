package com.example.raphaelattali.rythmrun.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.raphaelattali.rythmrun.R;

//EXEMPLE ANDROID

public class MainActivity extends AppCompatActivity {

    // Initialisation d'un bouton
    Button button = null;

    //Methode appelée au lancement de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }
}
