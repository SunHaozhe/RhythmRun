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

public class PaceFragment extends Fragment {

    final static double minSpeed = 1; //km/h
    final static double maxSpeed = 25;

    private SeekBarUpdater seekBarUpdater;
    private CheckBox checkBox;
    private TextView tvPace;
    private TextView tvTime;

    private Distance distance = new Distance(0);
    private Pace pace;

    private String unit = "km";
    private String paceMode = "p";

    public PaceFragment() {
        // Required empty public constructor
    }

    public double getPace() {
        if(checkBox.isChecked()){
            return -1;
        }
        return pace.getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PaceFragment","created PaceFragment");
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
            tvLabel.setText(R.string.pace_your_pace);
        }
        else{
            tvLabel.setText(R.string.pace_your_speed);
        }

        Log.d("I","This is the onResume() of the pace fragment");
        Log.d("unit","unit in pace fragment: "+unit);
        Log.d("PaceFragment","This is the onResume() of the pace fragment");
        Log.d("PaceFragment","unit in pace fragment: "+unit);

        //Displays information at start
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

    //Used to parse distance from maps direction
    public void setDistance(double d){
        //d is in meters
        distance = new Distance(d/1000);
        TextView tvDist = (TextView) getView().findViewById(R.id.tvNewRunDistance);
        if(tvDist!=null)
            tvDist.setText(distance.toStr(unit,true));

        //Needed to update the time display
        update(seekBarUpdater.getProgress());
    }

    public class SeekBarUpdater implements SeekBar.OnSeekBarChangeListener {

        PaceFragment paceFragment;
        int progress=50;

        SeekBarUpdater(PaceFragment paceFragment) {
            this.paceFragment = paceFragment;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress;
            paceFragment.update(progress);
        }

        int getProgress(){ return progress; }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}