package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;

import org.w3c.dom.Text;

import layout.PaceFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                view.setBackground(getResources().getDrawable(R.drawable.roun_shape_btn_pressed));
                Intent intent = new Intent(view.getContext(),NewRunActivity.class);
                startActivity(intent);
                view.setBackground(getResources().getDrawable(R.drawable.roun_shape_btn));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        TextView tvDistance = (TextView) findViewById(R.id.tvHomeDistance);
        TextView tvDistanceUnit = (TextView) findViewById(R.id.tvHomeDistanceUnit);
        TextView tvPace = (TextView) findViewById(R.id.tvHomePace);
        TextView tvPaceUnit = (TextView) findViewById(R.id.tvHomePaceUnit);

        double distance = getDistance();
        double pace = getPace();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");

        if(unit.equals("mi")){
            distance *= 0.621;
            pace /= 0.621;
        }

        tvDistance.setText(PaceFragment.fancySpeed(distance));
        tvDistanceUnit.setText(unit);
        tvPace.setText(PaceFragment.fancyPace(pace));
        tvPaceUnit.setText("/"+unit);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_run:
                startActivityFromClass(RunActivity.class);
                break;
            case R.id.nav_Settings:
                startActivityFromClass(SettingsActivity.class);
                break;
            case R.id.nav_library:
                startActivityFromClass(LibraryActivity.class);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startActivityFromClass(Class _class){
        Intent intent = new Intent(this, _class);
        startActivity(intent);
    }

    public double getDistance(){
        return 42.1;
    }

    public double getPace(){
        return 5.6;
    }

}
