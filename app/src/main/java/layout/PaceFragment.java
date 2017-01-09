package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaceFragment extends Fragment {

    private SeekBarUpdater seekBarUpdater;

    public PaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pace, container, false);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.skNewRun);
        seekBarUpdater = new SeekBarUpdater((TextView) rootView.findViewById(R.id.tvNewRunSpeed), (TextView) rootView.findViewById(R.id.tvNewRunPace));
        seekBar.setOnSeekBarChangeListener(seekBarUpdater);
        Log.d("I", "Creating the view of the pace fragment");
        return rootView;
    }

    public double getPace() {
        return seekBarUpdater.getPace();
    }

    public class SeekBarUpdater implements SeekBar.OnSeekBarChangeListener {
        final static double minSpeed = 1; //km/h
        final static double maxSpeed = 25;
        double speed;
        double pace;

        TextView tvSpeed;
        TextView tvPace;

        public SeekBarUpdater(TextView tvSpeed, TextView tvPace) {
            this.tvSpeed = tvSpeed;
            this.tvPace = tvPace;

            double doubleProgress = 50.0;
            speed = (doubleProgress / 100) * (maxSpeed - minSpeed) + minSpeed; //km/h
            pace = 60 / speed; //min/km
            tvSpeed.setText(fancySpeed(speed) + " km/h");
            tvPace.setText(fancyPace(pace) + " min/km");
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            double doubleProgress = (double) progress;
            speed = (doubleProgress / 100) * (maxSpeed - minSpeed) + minSpeed; //km/h
            pace = 60 / speed; //min/km
            tvSpeed.setText(fancySpeed(speed) + " km/h");
            tvPace.setText(fancyPace(pace) + " min/km");
        }

        private String fancySpeed(double d) {
            String str = Double.toString(d);
            int dotIndex = str.indexOf('.');
            if (str.charAt(dotIndex + 1) == '0') {
                return str.substring(0, dotIndex);
            } else {
                return str.substring(0, dotIndex + 2);
            }
        }

        private String fancyPace(double d) {
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

        public double getSpeed() {
            return speed;
        }

        public double getPace() {
            return pace;
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}