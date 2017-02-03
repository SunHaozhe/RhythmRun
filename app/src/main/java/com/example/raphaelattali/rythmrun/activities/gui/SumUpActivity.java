package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.Distance;
import com.example.raphaelattali.rythmrun.Pace;
import com.example.raphaelattali.rythmrun.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class SumUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_up);

        final ExpandableLinearLayout effortContent=(ExpandableLinearLayout) findViewById(R.id.sumUpEffortContent);
        RelativeLayout contentHeader=(RelativeLayout) findViewById(R.id.sumUpEffortHeader);
        contentHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effortContent.toggle();
            }
        });

        Intent intent = getIntent();
        Distance distance = new Distance(intent.getDoubleExtra(RunActivity.EXTRA_DISTANCE,0)/1000);
        Pace pace = new Pace(intent.getDoubleExtra(RunActivity.EXTRA_PACE,0));
        double elapsedTime = intent.getDoubleExtra(RunActivity.EXTRA_TIME,0);
        CustomPolylineOptions route = intent.getParcelableExtra(RunActivity.EXTRA_ROUTE);
        if(route!=null){
            SimpleMapFragment simpleMapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.sumUpMapFragment);
            simpleMapFragment.drawnPolyline(route.getPolylineOptions());
            simpleMapFragment.waitToAnimateCamera(route.getBounds());
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String unit = sharedPreferences.getString("unit_list","km");
        String paceMode = sharedPreferences.getString("pace","p");


        TextView tvDistance = (TextView) findViewById(R.id.sumUpDistance);
        TextView tvPace = (TextView) findViewById(R.id.sumUpPace);
        TextView tvTime = (TextView) findViewById(R.id.sumUpTime);

        tvTime.setText(new Pace(elapsedTime/1000).toStr("km","p",false));
        tvDistance.setText(distance.toStr(unit,true));
        tvPace.setText(pace.toStr(unit,paceMode,true));

    }
}