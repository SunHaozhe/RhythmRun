package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.telecom_paristech.pact25.rhythmrun.R;

import java.util.ArrayList;
import java.util.Arrays;

public class UserInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                new ArrayList<>(Arrays.asList("Male", "Female")));
        final EditText editText = (EditText) findViewById(R.id.userInfoAgeEdit);
        final Spinner spinner = (Spinner) findViewById(R.id.userInfoGenderSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        findViewById(R.id.userInfoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()).edit();

                String gender = (String) spinner.getSelectedItem();
                String age = (String) editText.getText().toString();

                if (gender.equals("Male")){
                    editor.putString("gender", "M");
                    editor.commit();
                } else {
                    editor.putString("gender", "F");
                    editor.commit();
                }

                if (!age.equals("")){
                    editor.putString("age", age);
                    editor.commit();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        });

    }
}
