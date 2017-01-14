package com.example.raphaelattali.rythmrun.activities.gui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.raphaelattali.rythmrun.R;

public class MusicFragment extends Fragment {

    private CheckBox checkBox;
    private Spinner spinner;

    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);

        spinner = (Spinner) rootView.findViewById(R.id.spMusic);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.music_styles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        checkBox = (CheckBox) rootView.findViewById(R.id.cbMusic);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()){
                    spinner.setEnabled(false);
                }
                else{
                    spinner.setEnabled(true);
                }
            }
        });

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

}
