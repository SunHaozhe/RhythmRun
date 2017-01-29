package com.example.raphaelattali.rythmrun.Tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.raphaelattali.rythmrun.activities.gui.LibraryActivity;
import com.example.raphaelattali.rythmrun.activities.gui.LoginActivity;


public class TestLoginActivity extends AppCompatActivity {
    //TODO
    public final void test(){
        Log.d("TestLoginActivity","starts TestLoginActivity");
        Intent intent = new Intent(TestLoginActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}

