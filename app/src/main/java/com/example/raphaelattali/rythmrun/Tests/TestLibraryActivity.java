package com.example.raphaelattali.rythmrun.Tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.raphaelattali.rythmrun.activities.gui.BluetoothActivity;
import com.example.raphaelattali.rythmrun.activities.gui.LibraryActivity;


public class TestLibraryActivity extends AppCompatActivity {
    //TODO
    public final void test(){
        Log.d("TestLibraryActivity","starts TestLibraryActivity");
        Intent intent = new Intent(TestLibraryActivity.this, LibraryActivity.class);
        startActivity(intent);
    }
}
