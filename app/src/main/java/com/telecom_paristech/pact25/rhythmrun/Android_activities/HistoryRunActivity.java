package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.telecom_paristech.pact25.rhythmrun.R;

import java.io.File;

//TODO: implement share feature

public class HistoryRunActivity extends AppCompatActivity {

    HistoryItem historyItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_run);

        TextView tvDate = (TextView) findViewById(R.id.historyRunDate);
        TextView tvDistance = (TextView) findViewById(R.id.historyRunDistance);
        TextView tvPace = (TextView) findViewById(R.id.historyRunPace);
        TextView tvTime = (TextView) findViewById(R.id.historyRunTime);

        Intent intent = getIntent();
        historyItem = intent.getParcelableExtra(Macros.EXTRA_HISTORY_ITEM);
        tvDate.setText(historyItem.getDate());
        tvDistance.setText(historyItem.getDistance());
        tvPace.setText(historyItem.getPace());
        tvTime.setText(historyItem.getTime());

        SimpleMapFragment mapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.historyRunMap);
        CustomPolylineOptions route = historyItem.getRoute();
        if(route != null && route.getPolylineOptions()!=null && route.getPolylineOptions().getPoints().size()>0){
            mapFragment.drawnPolyline(route.getPolylineOptions());
            mapFragment.waitToAnimateCamera(route.getBounds());
        } else {
            mapFragment.zoomToCurrentLocation();
        }

        findViewById(R.id.historyRunShareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Upcoming feature",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.historyRunDiscardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dir = getFilesDir().getAbsolutePath();
                File file = new File(dir, historyItem.getFilename());
                boolean deleted = file.delete();
                Log.i("HistoryRun",historyItem.getFilename()+" deleted: "+deleted);
                if(deleted){
                    Intent intent = new Intent(view.getContext(),HistoryActivity.class);
                    startActivity(intent);
                }
            }
        });

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
