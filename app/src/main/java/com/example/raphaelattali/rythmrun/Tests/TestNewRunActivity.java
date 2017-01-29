package com.example.raphaelattali.rythmrun.Tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.raphaelattali.rythmrun.activities.gui.LoginActivity;
import com.example.raphaelattali.rythmrun.activities.gui.NewRunActivity;


public class TestNewRunActivity extends AppCompatActivity {
    //TODO
    public final void test(){
        Log.d("TestNewRunActivity","starts TestNewRunActivity");
        Intent intent = new Intent(TestNewRunActivity.this, NewRunActivity.class);
        startActivity(intent);
    }
}
