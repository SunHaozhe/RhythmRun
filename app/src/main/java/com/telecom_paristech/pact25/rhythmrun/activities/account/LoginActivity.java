package com.telecom_paristech.pact25.rhythmrun.activities.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.telecom_paristech.pact25.rhythmrun.R;
import com.telecom_paristech.pact25.rhythmrun.activities.gui.HomeActivity;
import com.telecom_paristech.pact25.rhythmrun.app_config.AppConfig;
import com.telecom_paristech.pact25.rhythmrun.login.AppModified;
import com.telecom_paristech.pact25.rhythmrun.login.SQLLiteUser;
import com.telecom_paristech.pact25.rhythmrun.login.SessionConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

//Charger la session déjà existante et sauter l'étape de connexion si déjà connecté
        session = new SessionConfiguration(getApplicationContext());
        if (session.isLoggedIn()){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        //Boite de dialogue informant de l'avancement du processus : elle ne peut pas être fermée par l'utilisateur
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);


        sqlLiteUser = new SQLLiteUser(getApplicationContext());

        inputUsername = (EditText)findViewById(R.id.user_edittext__login);
        inputPassword = (EditText)findViewById(R.id.password_edittext__login);

        /**
         * Essaye de se connecter lors de l'appui
         */
        Button loginButton = (Button)findViewById(R.id.connect_button__login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressing("Tentative de connexion"); //TODO :  permettre une option de traduction via les ressources
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connect(inputUsername.getText().toString(),inputPassword.getText().toString());
                    }
                }).start();
            }
        });

        // Redirige vers l'écran d'inscription
        Button registerButton = (Button)findViewById(R.id.register_button__login);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

    }




    /**
     * Essaie de se connecter avec les identifiants donnés en paramètre.
     * Maintient la session connectée.
     *
     * @param username
     *       Nom de l'utilisateur
     * @param password
     *      Mot de passe de l'utilisateur
     */
    private void connect(final String username, final String password){
        String loginTagForRequests = "request_login_tag";

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response);

                        try {
                            Log.i(TAG,"Entrée dans le try");
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            if (!error) {
                                // Session actualisée
                                session.setLogin(true);

                                // Utilisateur inscris dans la bdd interne
                                JSONObject user = jObj.getJSONObject("user");
                                String name = user.getString("name");
                                String email = user.getString("email");
                                String created_at = user.getString("created_at");

                                sqlLiteUser.addUser(name, email, created_at);

                                // Lancement de l'activité principale !
                                dismissProgressing();
                                Toast.makeText(LoginActivity.this, name+", vous êtes à présent connecté !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //message d'erreur
                                clearFields();
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            clearFields();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        clearFields();
                        Log.e(TAG, "Erreur : " + error.getMessage());
                        Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

        ){

            @Override
            protected Map<String, String> getParams() {
                // récupère les paramètres sous forme de Map
                Map<String, String> params = new HashMap<>();
                params.put("emailOrUsername", username);
                params.put("password", password);

                return params;
            }

        };



        dismissProgressing();

        // Finalement on ajoute la requête à la file de requêtes à traiter
        AppModified.getInstance().addToRequestQueue(request,loginTagForRequests);

        // Fermer le clavier dans tous les cas
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void showProgressing(String message){
        if(!dialog.isShowing())
        {
            Log.d(TAG,"Lancement de la boite de dialogue avec le message : "+message);
            dialog.setMessage(message);
            dialog.show();
        }
    }

    private void dismissProgressing(){
        if(dialog.isShowing()) dialog.dismiss();
    }

    /**
     * Pour plus de sécurité, une entrée erronée efface les champs username et password
     */
    private void clearFields(){
        inputPassword.setText("");
        inputUsername.setText("");
    }

}
