package com.example.raphaelattali.rythmrun.Tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.raphaelattali.rythmrun.activities.gui.LoginActivity;
import com.example.raphaelattali.rythmrun.activities.gui.RecapActivity;



public class TestRecapActivity extends AppCompatActivity {
    //TODO
    public final void test(){
        Log.d("TestRecapActivity","starts TestRecapActivity");
        Intent intent = new Intent(TestRecapActivity.this, RecapActivity.class);
        startActivity(intent);
    }
}
