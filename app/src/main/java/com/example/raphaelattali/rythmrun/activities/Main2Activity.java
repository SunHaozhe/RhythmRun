package com.example.raphaelattali.rythmrun.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.raphaelattali.rythmrun.R;

public class Main2Activity extends AppCompatActivity {

    Button test_button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        test_button = (Button)findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test_button.setText("hop");
            }
        });
    }
}
