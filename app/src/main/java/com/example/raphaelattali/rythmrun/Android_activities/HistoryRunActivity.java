package com.example.raphaelattali.rythmrun.Android_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;

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
        tvDate.setText(intent.getStringExtra(HistoryActivity.EXTRA_DATE));
        tvDistance.setText(intent.getStringExtra(HistoryActivity.EXTRA_DISTANCE));
        tvPace.setText(intent.getStringExtra(HistoryActivity.EXTRA_PACE));
        tvTime.setText(intent.getStringExtra(HistoryActivity.EXTRA_TIME));

        SimpleMapFragment mapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.historyRunMap);
        CustomPolylineOptions route = intent.getParcelableExtra(HistoryActivity.EXTRA_ROUTE);
        if(route != null){
            mapFragment.drawnPolyline(route.getPolylineOptions());
            mapFragment.waitToAnimateCamera(route.getBounds());
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
