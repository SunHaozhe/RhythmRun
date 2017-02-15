package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;

public class HistoryRunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_run);

        TextView tvDate = (TextView) findViewById(R.id.historyRunDate);
        TextView tvDistance = (TextView) findViewById(R.id.historyRunDistance);
        TextView tvPace = (TextView) findViewById(R.id.historyRunPace);
        TextView tvTime = (TextView) findViewById(R.id.historyRunTime);

        Intent intent = getIntent();
        tvDate.setText(intent.getStringExtra(Macros.EXTRA_DATE));
        tvDistance.setText(intent.getStringExtra(Macros.EXTRA_DISTANCE));
        tvPace.setText(intent.getStringExtra(Macros.EXTRA_PACE));
        tvTime.setText(intent.getStringExtra(Macros.EXTRA_TIME));

        SimpleMapFragment mapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.historyRunMap);
        CustomPolylineOptions route = intent.getParcelableExtra(Macros.EXTRA_ROUTE);
        if(route != null && route.getPolylineOptions()!=null && route.getPolylineOptions().getPoints().size()>0){
            mapFragment.drawnPolyline(route.getPolylineOptions());
            mapFragment.waitToAnimateCamera(route.getBounds());
        } else {
            mapFragment.zoomToCurrentLocation();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
