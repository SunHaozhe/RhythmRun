package com.example.raphaelattali.rhythmrun.Android_activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.raphaelattali.rhythmrun.R;

//TODO: Change the company name (not example)

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Home","Creating activity Home.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button buttonNewRun = (Button) findViewById(R.id.buttonHomeNewRun);
        buttonNewRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),NewRunActivity.class);
                startActivity(intent);
            }
        });

        //Check if location is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Home","Requesting location permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Macros.PERMISSION_ACCESS_FINE_LOCATION);
        }

        //Check if external storage reading is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Home","Requesting read external storage permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Macros.PERMISSION_READ_EXTERNAL_STORAGE);
        }

        //Check if external storage writing is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Home","Requesting write external storage permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Macros.PERMISSION_WRITE_EXTERNAL_STORAGE);
        }


        //Loading of the songs in the music folder
        if (Song.songs == null){
            Log.i("Home","Starting thread for songs loading.");
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Song.loadSongs();
                }
            });
            thread.start();
        }

    }

    @Override
    public void onResume(){
        /*
            Display of run distance of the week and average pace, with a counting animation.
         */

        super.onResume();

        TextView tvDistance = (TextView) findViewById(R.id.tvHomeDistance);
        TextView tvDistanceUnit = (TextView) findViewById(R.id.tvHomeDistanceUnit);
        TextView tvPace = (TextView) findViewById(R.id.tvHomePace);
        TextView tvPaceUnit = (TextView) findViewById(R.id.tvHomePaceUnit);
        TextView tvAveragePace = (TextView) findViewById(R.id.tvHomeAveragePace);

        Distance distance = getDistance();
        Pace pace = getPace();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String unit = sharedPreferences.getString("unit_list","km");
        final String paceMode = sharedPreferences.getString("pace","p");

        tvDistance.setText(distance.toStr(unit));
        tvDistanceUnit.setText(unit);
        if(paceMode.equals("p")){
            tvPace.setText(pace.toStrPace(unit));
            tvPaceUnit.setText(getString(R.string.unit_pace,unit));
            tvAveragePace.setText(R.string.home_average_pace);
        }
        else{
            tvPace.setText(pace.toStrSpeed(unit));
            tvPaceUnit.setText(getString(R.string.unit_speed,unit));
            tvAveragePace.setText(R.string.home_average_speed);
        }

        startCountAnimation(tvDistance, Double.valueOf(getDistance().getValue() * 10).intValue(), new Callable() {
            @Override
            public String call(int value) {
                double dValue = (double) value/10; //Increments every tenth of distance unit (km usually).
                return Double.toString(dValue);
            }
        });

        if(paceMode.equals("p")){
            startCountAnimation(tvPace, Double.valueOf(getPace().getValue() * 60).intValue(), new Callable() {
                @Override
                public String call(int value) {
                    double dValue = (double) value/60; //Increments every second
                    return new Pace(dValue).toStr(unit,paceMode,false);
                }
            });
        }
        else{
            startCountAnimation(tvPace, Double.valueOf(600/getPace().getValue()).intValue(), new Callable() {
                @Override
                public String call(int value) {
                    double dValue = (double) value/10; //Increments every tenth of speed unit.
                    return Double.toString(dValue);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        /*
            Closes the drawer when back is pressed.
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /*
            Handles the navigation menu in the drawer.
         */
        int id = item.getItemId();
        switch (id){
            case R.id.nav_run:
                startActivityFromClass(RunActivity.class);
                break;
            case R.id.nav_activity:
                startActivityFromClass(HistoryActivity.class);
                break;
            case R.id.nav_Settings:
                startActivityFromClass(SettingsActivity.class);
                break;
            case R.id.nav_library:
                startActivityFromClass(LibraryActivity.class);
                break;
            case R.id.nav_about:
                startActivityFromClass(AboutActivity.class);
                break;
        }

        //Close the drawer after an item has been clicked.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startActivityFromClass(Class _class){
        Log.d("Home","Creating intent to "+_class);
        Intent intent = new Intent(this, _class);
        startActivity(intent);
    }

    public Distance getDistance(){
        //TODO: calculate week distance
        return new Distance(42.1);
    }

    public Pace getPace(){
        //TODO: calculate average pace
        return new Pace(5.6);
    }

    public interface Callable{
        /*
            This interface is used to represent a callable function object.
            It is used for the dynamic method startCountAnimation.
         */
        String call(int value);
    }

    private void startCountAnimation(final TextView tv, int value, final Callable format){
        /*
            Creates and starts a counting animation of a TextView.
            value: final value to display
            format: a function that returns the number to display based on the counting progress (from 0 to 100).
         */
        Log.d("Home","Creating counting animation for "+tv+" from 0 to"+value);
        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(0, value);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tv.setText(format.call((int) valueAnimator.getAnimatedValue()));
            }
        });
        animator.start();
    }

}
