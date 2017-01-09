package layout;


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


    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spMusic);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.music_styles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return rootView;
    }

    public String getMusicStyle(){
        Spinner spinner = (Spinner) this.getView().findViewById(R.id.spMusic);
        CheckBox cb = (CheckBox) this.getView().findViewById(R.id.cbMusic);
        if(cb.isChecked()){
            return "Random";
        }
        else{
            return (String) spinner.getSelectedItem();
        }
    }

}
