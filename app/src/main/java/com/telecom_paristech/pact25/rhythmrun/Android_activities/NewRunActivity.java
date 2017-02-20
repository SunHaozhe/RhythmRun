package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.List;

public class NewRunActivity extends AppCompatActivity {

    private Pace pace;
    private ItineraryFragment itineraryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("NewRun","Creating NewRun activity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_run);

        itineraryFragment = (ItineraryFragment) getSupportFragmentManager().findFragmentById(R.id.newRunItineraryFragment);

        initExpandableMusic();
        initExpandableItinerary();
        initExpandablePace();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_run_action_bar,menu);

        //Colors each menu item in white
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.newRunMenuValidate:
                Log.d("NewRun","Creating intent for RecapActivity");

                if(itineraryFragment.isRouteCalculationAvailable()){
                    final Context context = this;
                    itineraryFragment.setOnRouteCalculatedListener(new ItineraryFragment.OnRouteCalculatedListener() {
                        @Override
                        public void onRouteCalculated() {
                            Intent intent = new Intent(context, RecapActivity.class);
                            intent.putExtra(Macros.EXTRA_DISTANCE, getDistance());
                            intent.putExtra(Macros.EXTRA_PACE, getPace());
                            intent.putExtra(Macros.EXTRA_MUSIC, getMusic());
                            intent.putExtra(Macros.EXTRA_ITINERARY, new CustomPolylineOptions(itineraryFragment.getItinerary()));
                            startActivity(intent);
                        }
                    });
                    itineraryFragment.initiateRoute();
                } else {
                    Intent intent = new Intent(this, RecapActivity.class);
                    intent.putExtra(Macros.EXTRA_DISTANCE, getDistance());
                    intent.putExtra(Macros.EXTRA_PACE, getPace());
                    intent.putExtra(Macros.EXTRA_MUSIC, getMusic());
                    intent.putExtra(Macros.EXTRA_ITINERARY, new CustomPolylineOptions(itineraryFragment.getItinerary()));
                    startActivity(intent);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initExpandableMusic(){
        Log.v("NewRun","Initialization of music selector");

        final ExpandableLinearLayout musicContent=(ExpandableLinearLayout) findViewById(R.id.newRunMusicContent);
        RelativeLayout musicHeader=(RelativeLayout) findViewById(R.id.newRunMusicHeader);

        //Creates the expandable effect for the music CardView
        musicHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicContent.toggle();
            }
        });
        findViewById(R.id.newRunMusicDownArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicContent.toggle();
            }
        });

        //Getting the list of available genres from the library
        List<String> genres = Song.getAllGenres();
        Log.d("NewRun music",genres.size()+" music genre(s) found");

        final TextView tvMusicSelection = (TextView) findViewById(R.id.newRunMusicSelection);
        final Spinner spinner = (Spinner) findViewById(R.id.newRunMusicSpinner);
        final CheckBox cbMusicRandom = (CheckBox) findViewById(R.id.newRunMusicCheckbox);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.custom_spinner_item, MusicManager.getAllGenres());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("NewRun music","Selected genre: "+spinner.getSelectedItem());
                tvMusicSelection.setText((String) spinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Setting up the random checkbox
        cbMusicRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setEnabled(!cbMusicRandom.isChecked());
                if(cbMusicRandom.isChecked()){
                    Log.d("NewRun music","Random genre selected.");
                    tvMusicSelection.setText(R.string.new_run_random);
                }
                else{
                    Log.d("NewRun music","Random genre unselected.");
                    tvMusicSelection.setText((String) spinner.getSelectedItem());
                }
            }
        });

        if(genres.size()==0){
            Log.w("NewRun music","No genre found, blocking selector in random mode.");
            cbMusicRandom.setChecked(true);
            cbMusicRandom.setEnabled(false);
            spinner.setEnabled(false);
            spinner.setVisibility(View.INVISIBLE);
            tvMusicSelection.setText(R.string.new_run_random);
        }
    }

    public void initExpandableItinerary(){
        Log.v("NewRun itinerary","Initialization of itinerary selector");

        final ExpandableLinearLayout itineraryContent=(ExpandableLinearLayout) findViewById(R.id.newRunItineraryContent);
        RelativeLayout itineraryHeader=(RelativeLayout) findViewById(R.id.newRunItineraryHeader);

        //Creates the expandable effect for the itinerary CardView
        itineraryHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itineraryContent.toggle();
            }
        });
        findViewById(R.id.newRunItineraryDownArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itineraryContent.toggle();
            }
        });

        //Disabling global scroll effect when touching the map, to be able to
        //move in the map view.
        final ScrollView scrollView = (ScrollView) findViewById(R.id.newRunScrollView);
        itineraryFragment.setListener(new ItineraryFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        //Setting up the buttons listeners.
        Button buttonDelete = (Button) findViewById(R.id.newRunItineraryButtonMarker);
        final TextView tvItineraryDistance = (TextView) findViewById(R.id.newRunItinerarySelection);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itineraryFragment.removeLastMarker();
            }
        });
        Button buttonRoute = (Button) findViewById(R.id.newRunItineraryButtonDirection);
        buttonRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itineraryFragment.initiateRoute(tvItineraryDistance);
            }
        });
    }

    public void initExpandablePace(){
        Log.v("NewRun pace","Initialization of pace selector");

        final ExpandableLinearLayout paceContent=(ExpandableLinearLayout) findViewById(R.id.newRunPaceContent);
        RelativeLayout paceHeader=(RelativeLayout) findViewById(R.id.newRunPaceHeader);

        //Creates the expandable effect for the pace CardView
        paceHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paceContent.toggle();
            }
        });
        findViewById(R.id.newRunPaceDownArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paceContent.toggle();
            }
        });

        //Setting the title along the speed/pace setting.
        TextView textView = (TextView) findViewById(R.id.newRunPaceTitle);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String paceMode = sharedPreferences.getString("pace","p");
        if(paceMode.equals("p")){
            textView.setText(R.string.new_run_pace);
        } else {
            textView.setText(R.string.new_run_speed);
        }

        //Setting up the seekBar and the free checkbox.
        SeekBar seekBar = (SeekBar) findViewById(R.id.newRunPaceSeekbar);
        final SeekBarUpdater seekBarUpdater = new SeekBarUpdater(this);
        seekBar.setOnSeekBarChangeListener(seekBarUpdater);

        CheckBox checkBoxFree = (CheckBox) findViewById(R.id.newRunPaceFree);
        checkBoxFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                update(seekBarUpdater.getProgress());
            }
        });

        update(50); //Initialization of the SeekBar at 50% (about 12 km/h).
    }

    public void update(int progress){
        /*
            Updates the display of selected speed based of the progress of the selector,
            from 1 to 100.
         */

        Log.v("NewRun pace","Updating pace selection display");

        double speed = ((double) progress / 100) * (Macros.MAX_SPEED - Macros.MIN_SPEED) + Macros.MIN_SPEED; //km/h
        pace = new Pace(60/speed); //min/km

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String unit = sharedPreferences.getString("unit_list","km");
        final String paceMode = sharedPreferences.getString("pace","p");

        TextView tvPace = (TextView) findViewById(R.id.newRunPaceSelection);
        CheckBox checkBoxFree = (CheckBox) findViewById(R.id.newRunPaceFree);

        if(checkBoxFree.isChecked()){
            tvPace.setText(R.string.recap_free);
        }
        else {
            tvPace.setText(pace.toStr(unit,paceMode,true));
        }
    }

    public class SeekBarUpdater implements SeekBar.OnSeekBarChangeListener {
        NewRunActivity activityNewRun; //A reference to the parent activity
        int progress=50; //Initialized at the middle

        SeekBarUpdater(NewRunActivity activityNewRun) {
            this.activityNewRun = activityNewRun;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //Progress has changed, activity needs an update
            Log.d("NewRun pace","SeekBar update: "+progress);
            this.progress = progress;
            activityNewRun.update(progress);
        }

        int getProgress(){ return progress; }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public Distance getDistance(){
        /*
            Returns the distance of the selected itinerary. 0 if none selected.
         */
        ItineraryFragment itineraryFragment = (ItineraryFragment) getSupportFragmentManager().findFragmentById(R.id.newRunItineraryFragment);
        Distance distance = itineraryFragment.getDistance();
        if(distance == null)
            distance = new Distance(0);
        Log.i("NewRun","Selected distance: "+distance.getValue()+" km");
        return distance;
    }

    public Pace getPace(){
        /*
            Returns the selected pace. -1 if free.
         */
        CheckBox checkBoxFree = (CheckBox) findViewById(R.id.newRunPaceFree);
        if(checkBoxFree.isChecked()) {
            Log.i("NewRun","Selected pace: free");
            return new Pace(-1);
        } else {
            Log.i("NewRun","Selected pace: "+pace.getValue()+" min/km");
            return pace;
        }
    }
    public String getMusic(){
        /*
            Returns the selected music genre.
         */
        CheckBox checkBox = (CheckBox) findViewById(R.id.newRunMusicCheckbox);
        Spinner spinner = (Spinner) findViewById(R.id.newRunMusicSpinner);
        String music;
        if(checkBox.isChecked()){
            music ="Random";
        }
        else{
            music =(String) spinner.getSelectedItem();
        }
        Log.i("NewRun","Selected music: "+ music);
        return music;
    }

}
