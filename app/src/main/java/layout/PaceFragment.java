package layout;


import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import com.example.raphaelattali.rythmrun.R;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaceFragment extends Fragment {

    private SeekBarUpdater seekBarUpdater;
    private CheckBox checkBox;
    private TextView tvSpeed;
    private TextView tvPace;

    final static double minSpeed = 1; //km/h
    final static double maxSpeed = 25;

    private double speed;
    private double pace;

    private String unit = "km";

    public PaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pace, container, false);

        tvSpeed = (TextView) rootView.findViewById(R.id.tvNewRunSpeed);
        tvPace = (TextView) rootView.findViewById(R.id.tvNewRunPace);

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
        Log.d("I","This is the onResume() of the pace fragment");
        Log.d("unit","unit in pace fragment: "+unit);

        //Displays informations at start
        update(seekBarUpdater.getProgress());
    }

    public void update(int progress){
        double doubleProgress = (double) progress;
        speed = (doubleProgress / 100) * (maxSpeed - minSpeed) + minSpeed; //km/h
        pace = 60 / speed; //min/km

        if(checkBox.isChecked()){
            tvSpeed.setText("-");
            tvPace.setText("-");
        }
        else {
            if(unit.equals("mi")){
                tvSpeed.setText(fancySpeed(speed*0.621) + " mi/h");
                tvPace.setText(fancyPace(pace/0.621) + " /mi");
            }
            else{
                tvSpeed.setText(fancySpeed(speed) + " km/h");
                tvPace.setText(fancyPace(pace) + " /km");
            }
        }

    }

    public static String fancyPace(double d) {
        String str = Double.toString(d);
        int min = Integer.parseInt(str.substring(0, str.indexOf('.')));
        Double secDouble = 60 * (d - (double) min);
        int sec = secDouble.intValue();
        if (sec < 10) {
            return Integer.toString(min) + ":0" + Integer.toString(sec);
        } else {
            return Integer.toString(min) + ":" + Integer.toString(sec);
        }
    }

    public static String fancySpeed(double d) {
        String str = Double.toString(d);
        int dotIndex = str.indexOf('.');
        if (str.charAt(dotIndex + 1) == '0') {
            return str.substring(0, dotIndex);
        } else {
            return str.substring(0, dotIndex + 2);
        }
    }

    public double getPace() {
        if(checkBox.isChecked()){
            return -1;
        }
        return pace;
    }

    public double getSpeed() {
        if(checkBox.isChecked()){
            return -1;
        }
        return speed;
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