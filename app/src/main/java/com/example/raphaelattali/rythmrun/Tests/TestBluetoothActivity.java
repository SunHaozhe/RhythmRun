package com.example.raphaelattali.rythmrun.Tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.raphaelattali.rythmrun.activities.gui.BluetoothActivity;



public final class TestBluetoothActivity extends AppCompatActivity {
    //TODO
    public final void test(){
        Log.d("TestBluetoothActivity","starts TestBluetoothActivity");
        Intent intent = new Intent(TestBluetoothActivity.this, BluetoothActivity.class);
        startActivity(intent);
    }
}
