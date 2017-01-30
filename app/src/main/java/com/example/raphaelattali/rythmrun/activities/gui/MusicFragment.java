package com.example.raphaelattali.rythmrun.activities.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.music.Song;

public class MusicFragment extends Fragment {

    private CheckBox checkBox;
    private Spinner spinner;
    private TextView tvFoundSongs;

    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("MusicFragment", "created MusicFragment");
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);

        tvFoundSongs = (TextView) rootView.findViewById(R.id.tvNewRunFoundFiles);

        spinner = (Spinner) rootView.findViewById(R.id.spMusic);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(),android.R.layout.simple_spinner_item,Song.getAllGenres());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateFoundSongs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkBox = (CheckBox) rootView.findViewById(R.id.cbMusic);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setEnabled(!checkBox.isChecked());
            }
        });

        //Forced update at start
        updateFoundSongs();
        Log.d("MusicFragment", "We force update in the beginning");

        return rootView;
    }

    public String getMusicStyle(){
        if(checkBox.isChecked()){
            return "Random";
        }
        else{
            return (String) spinner.getSelectedItem();
        }
    }

    private void updateFoundSongs(){
        int n = Song.getSongsByGenre((String) spinner.getSelectedItem()).size();
        if(n==0){
            tvFoundSongs.setText(R.string.music_no_files_found);
        }
        else{
            tvFoundSongs.setText(getString(R.string.music_files_found,n));
        }
    }
}
