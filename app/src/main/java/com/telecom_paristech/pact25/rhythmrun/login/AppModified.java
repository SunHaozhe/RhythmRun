package com.telecom_paristech.pact25.rhythmrun.login;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Classe dérivée de Application : elle fait passer des requêtes grâce à "volley"
 * Tous les objets nécessaires au fonctionnement de volley sont créés au démarrage de l'application
 */
public class AppModified extends Application {

    public static final String TAG = AppModified.class.getSimpleName();


    private RequestQueue mRequestQueue;
    private static AppModified mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppModified getInstance() {
        return mInstance;
    }

    /**
     * Récupère la file de requêtes. Si elle n'existe pas, elle est créée.
     *
     * @return La file de requêtes
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Annuler toutes les requêtes en attente de traitement portant la signature donnée.
     *
     * @param tag La signature des requêtes à annuler
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
