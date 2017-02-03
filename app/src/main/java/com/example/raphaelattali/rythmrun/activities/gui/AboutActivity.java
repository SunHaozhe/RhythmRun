package com.example.raphaelattali.rythmrun.activities.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = (TextView) findViewById(R.id.aboutTextView);
        textView.setText(Html.fromHtml("" +
                "<h1>Rhythm Run</h1>" +
                "Are you interested in listening to music while running? " +
                "Would you like to hear a music adapted to your own rhythm ?  This application " +
                "is created for you. You can choose the kind of music you’d prefer in the " +
                "application’s library, add songs you’re keen on, choose an itinerary, " +
                "eventually a rhythm if you want to and decide to begin a new run.  The " +
                "application will save all of your records and grant you music that follows " +
                "your own rhythm." +
                "<h2>Privacy</h2>" +
                "This application collects, uses and saves user's location and physical " +
                "state and performance. You can disable the history at any times in the " +
                "settings. You can also delete current history in this menu or in the " +
                "activity menu." +
                "<h2>The team</h2>"+
                "<i>Rhythm Run</i> is an application made by students from the engineering " +
                "school Télécom ParisTech. The team: Raphael Attali, Yohan Chalier, Lucas " +
                "Lebailly, Saousan Kaddami, Clément Robin, Haozhe Sun."
        ));
    }

}
