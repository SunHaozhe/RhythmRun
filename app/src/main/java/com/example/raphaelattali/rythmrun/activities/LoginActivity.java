package com.example.raphaelattali.rythmrun.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.login.SQLLiteUser;
import com.example.raphaelattali.rythmrun.login.SessionConfiguration;

/**
 * Activité pour se connecter à son compte utilisateur
 *
 * @author Raphael Attali (code) et Saoussan Kaddami (design)
 */
public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword;
    private SQLLiteUser sqlLiteUser;
    private SessionConfiguration session;
    private ProgressDialog dialog;

    final private String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



    }

}
