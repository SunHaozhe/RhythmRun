package com.example.raphaelattali.rythmrun.Android_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.raphaelattali.rythmrun.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class NewRunActivity extends AppCompatActivity {

    public static final double MAX_SPEED = 25;
    public static final double MIN_SPEED = 1;

    public static final String EXTRA_DISTANCE="distance";
    public static final String EXTRA_PACE="pace";
    public static final String EXTRA_MUSIC="music";
    public static final String EXTRA_ITINERARY = "itinerary";

    private double distance;
    private double pace;
    private String music;

    private ItineraryFragment itineraryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Intent intent = new Intent(this, RecapActivity.class);
                intent.putExtra(EXTRA_DISTANCE, getDistance());
                intent.putExtra(EXTRA_PACE, getPace());
                intent.putExtra(EXTRA_MUSIC, getMusic());
                intent.putExtra(EXTRA_ITINERARY, new CustomPolylineOptions(itineraryFragment.getItinerary()));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initExpandableMusic(){
        final ExpandableLinearLayout musicContent=(ExpandableLinearLayout) findViewById(R.id.newRunMusicContent);
        RelativeLayout musicHeader=(RelativeLayout) findViewById(R.id.newRunMusicHeader);

        musicHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicContent.toggle();
            }
        });

        final TextView tvMusicSelection = (TextView) findViewById(R.id.newRunMusicSelection);
        final Spinner spinner = (Spinner) findViewById(R.id.newRunMusicSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.custom_spinner_item, MusicManager.getAllGenres());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvMusicSelection.setText((String) spinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final CheckBox cbMusicRandom = (CheckBox) findViewById(R.id.newRunMusicCheckbox);
        cbMusicRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setEnabled(!cbMusicRandom.isChecked());
                if(cbMusicRandom.isChecked()){
                    tvMusicSelection.setText(R.string.new_run_random);
                }
                else{
                    tvMusicSelection.setText((String) spinner.getSelectedItem());
                }
            }
        });
    }

    public void initExpandableItinerary(){
        final ExpandableLinearLayout itineraryContent=(ExpandableLinearLayout) findViewById(R.id.newRunItineraryContent);
        RelativeLayout itineraryHeader=(RelativeLayout) findViewById(R.id.newRunItineraryHeader);

        itineraryHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itineraryContent.toggle();
            }
        });

        final ScrollView scrollView = (ScrollView) findViewById(R.id.newRunScrollView);
        itineraryFragment.setListener(new ItineraryFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
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
                itineraryFragment.initiateDirection(tvItineraryDistance);
            }
        });
    }

    public void initExpandablePace(){
        final ExpandableLinearLayout paceContent=(ExpandableLinearLayout) findViewById(R.id.newRunPaceContent);
        RelativeLayout paceHeader=(RelativeLayout) findViewById(R.id.newRunPaceHeader);

        paceHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paceContent.toggle();
            }
        });

        TextView textView = (TextView) findViewById(R.id.newRunPaceTitle);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String paceMode = sharedPreferences.getString("pace","p");
        if(paceMode.equals("p")){
            textView.setText(R.string.new_run_pace);
        } else {
            textView.setText(R.string.new_run_speed);
        }

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

        update(50);
    }

    public void update(int progress){
        double doubleProgress = (double) progress;
        double speed = (doubleProgress / 100) * (MAX_SPEED - MIN_SPEED) + MIN_SPEED; //km/h
        pace = 60/speed;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String unit = sharedPreferences.getString("unit_list","km");
        final String paceMode = sharedPreferences.getString("pace","p");

        TextView tvPace = (TextView) findViewById(R.id.newRunPaceSelection);
        CheckBox checkBoxFree = (CheckBox) findViewById(R.id.newRunPaceFree);

        if(checkBoxFree.isChecked()){
            tvPace.setText(R.string.recap_free);
        }
        else {
            tvPace.setText(new Pace(pace).toStr(unit,paceMode,true));
        }
    }

    public class SeekBarUpdater implements SeekBar.OnSeekBarChangeListener {

        NewRunActivity activityNewRun;
        int progress=50;

        SeekBarUpdater(NewRunActivity activityNewRun) {
            this.activityNewRun = activityNewRun;
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress;
            activityNewRun.update(progress);
        }

        int getProgress(){ return progress; }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public double getDistance(){
        ItineraryFragment itineraryFragment = (ItineraryFragment) getSupportFragmentManager().findFragmentById(R.id.newRunItineraryFragment);
        distance = itineraryFragment.getDistance();
        return distance;
    }
    public double getPace(){
        CheckBox checkBoxFree = (CheckBox) findViewById(R.id.newRunPaceFree);
        if(checkBoxFree.isChecked())
            return -1;
        return pace;
    }
    public String getMusic(){
        CheckBox checkBox = (CheckBox) findViewById(R.id.newRunMusicCheckbox);
        Spinner spinner = (Spinner) findViewById(R.id.newRunMusicSpinner);
        if(checkBox.isChecked()){
            music="Random";
        }
        else{
            music=(String) spinner.getSelectedItem();
        }
        return music;
    }

}
