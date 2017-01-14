package com.example.raphaelattali.rythmrun.activities.gui;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.Distance;
import com.example.raphaelattali.rythmrun.Pace;
import com.example.raphaelattali.rythmrun.R;

import org.w3c.dom.Text;

public class PaceFragment extends Fragment {

    private SeekBarUpdater seekBarUpdater;
    private CheckBox checkBox;
    private TextView tvPace;
    private Distance distance = new Distance(0);
    private TextView tvTime;

    final static double minSpeed = 1; //km/h
    final static double maxSpeed = 25;

    private Pace pace;
    private String unit = "km";
    private String paceMode = "p";

    public PaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pace, container, false);

        tvPace = (TextView) rootView.findViewById(R.id.tvNewRunPace);
        tvTime = (TextView) rootView.findViewById(R.id.tvNewRunTime);

        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.skNewRun);
        seekBarUpdater = new SeekBarUpdater(this);
        seekBar.setOnSeekBarChangeListener(seekBarUpdater);

        checkBox = (CheckBox) rootView.findViewById(R.id.cbNewRunFree);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                update(seekBarUpdater.getProgress());
            }
        });

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        unit = sharedPreferences.getString("unit_list","km");
        paceMode = sharedPreferences.getString("pace","p");

        TextView tvLabel = (TextView) getView().findViewById(R.id.tvNewRunLabel3);
        if(paceMode.equals("p")){
            tvLabel.setText("YOUR PACE");
        }
        else{
            tvLabel.setText("YOUR SPEED");
        }

        Log.d("I","This is the onResume() of the pace fragment");
        Log.d("unit","unit in pace fragment: "+unit);

        //Displays informations at start
        update(seekBarUpdater.getProgress());
    }

    public void update(int progress){
        double doubleProgress = (double) progress;
        double speed = (doubleProgress / 100) * (maxSpeed - minSpeed) + minSpeed; //km/h
        pace = new Pace(60/speed);

        if(checkBox.isChecked()){
            tvPace.setText("-");
        }
        else {
            tvTime.setText(Pace.fancyPace(distance.getValue()*pace.getValue()));
            tvPace.setText(pace.toStr(unit,paceMode,true));
        }
    }

    public double getPace() {
        if(checkBox.isChecked()){
            return -1;
        }
        return pace.getValue();
    }

    public void setDistance(double d){
        //d in meters, distance in kilometers
        distance = new Distance(d/1000);
        TextView tv = (TextView) getView().findViewById(R.id.tvNewRunDistance);
        tv.setText(distance.toStr(unit,true));
        update(seekBarUpdater.getProgress()); //Displays time
    }

    public class SeekBarUpdater implements SeekBar.OnSeekBarChangeListener {

        PaceFragment paceFragment;
        int progress=50;

        public SeekBarUpdater(PaceFragment paceFragment) {
            this.paceFragment = paceFragment;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress;
            paceFragment.update(progress);
        }

        public int getProgress(){ return progress; }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}