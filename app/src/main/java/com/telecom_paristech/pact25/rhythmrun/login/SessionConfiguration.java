package com.telecom_paristech.pact25.rhythmrun.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */

/**
 * Permet d'ouvrir, de fermer ou de connaitre l'état de la session utilisateur
 */
public class SessionConfiguration {
        private static String TAG = SessionConfiguration.class.getSimpleName();

        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        Context context;

        private static final String PREFERENCES_NAME = "UpdityLogin";
        private static final String KEY_LOGGED_IN = "isLoggedIn";

    /**
     * Constructeur de SessionConfiguration
     * @param context Le contexte de l'application (utilisez getApplicationContext())
     */
    public SessionConfiguration(Context context){
            this.context = context;
            preferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
            editor = preferences.edit();
        }

    /**
     * Actualiser l'état de la session
     * @param isLoggedIn
     */
    public void setLogin(boolean isLoggedIn){
            editor.putBoolean(KEY_LOGGED_IN,isLoggedIn);
            editor.commit();
            Log.i(TAG,"Etat de la session modifiée");
        }

    /**
     * Retourne si l'utilisateur a une session ouverte.
     * @return true si une session user est ouverte.
     */
    public boolean isLoggedIn(){
            return preferences.getBoolean(KEY_LOGGED_IN,false);
        }

}

