package com.example.raphaelattali.rythmrun.activities;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.sensors.Podometer;

public class monRunnable implements Runnable {
    Context context;
    Integer fini;
    Main2Activity act;
    public monRunnable(Main2Activity act, Context context, Integer fini) {
        this.context = context;
        this.fini = fini;
        this.act = act;
    }

    public void run() {
        Log.i("lucas", "on est rentrés dans le thread de main2activity");
        Podometer pod = new Podometer(context);

        while (fini == 0) {
            //act.setPacefreq(pod.getRunningPaceFrequency()); marche pas
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

}
