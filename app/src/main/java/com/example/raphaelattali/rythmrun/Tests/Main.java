package com.example.raphaelattali.rythmrun.Tests;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.raphaelattali.rythmrun.activities.gui.BluetoothActivity;


public class Main extends AppCompatActivity {
    //TODO
    public static final void main(){
        Log.d("Main","Starts the Main function");

        //Test of the bluetooth function
        TestBluetoothActivity testBluetoothActivity = new TestBluetoothActivity();
        testBluetoothActivity.test();

        //Test of the LibraryActivity
        TestLibraryActivity testLibraryActivity = new TestLibraryActivity();
        testLibraryActivity.test();

        //Test of the LoginActivity
        TestLoginActivity testLoginActivity = new TestLoginActivity();
        testLoginActivity.test();

        //Test of the NewRunActivity
        TestNewRunActivity testNewRunActivity = new TestNewRunActivity();
        testNewRunActivity.test();

        //Test of the RecapActivity
        TestRecapActivity testRecapActivity = new TestRecapActivity();
        testRecapActivity.test();
    }
}
