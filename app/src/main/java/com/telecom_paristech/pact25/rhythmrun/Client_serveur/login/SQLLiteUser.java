package com.telecom_paristech.pact25.rhythmrun.Client_serveur.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Raphael Attali on 31/01/2017.
 * <p>
 * Project RythmRun
 */

/**
 * Classe qui sert à stocker et récupérer les données de l'utilisateur directement depuis son téléphone,
 * économise alors la requête au serveur
 *
 * @author Raphael Attali
 * @version 1
 */
public class SQLLiteUser extends SQLiteOpenHelper {

        private static final String TAG = SQLLiteUser.class.getSimpleName();

        // All Static variables
        // Database Version
        private static final int DATABASE_VERSION = 1;

        // Database Name
        private static final String DATABASE_NAME = "android_api";

        // Login table name
        private static final String TABLE_USER = "login_table";

        // Login Table Columns names
        private static final String KEY_ID = "id";
        private static final String KEY_NAME = "name";
        private static final String KEY_EMAIL = "email";
        private static final String KEY_CREATED_AT = "created_at";

        public SQLLiteUser(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG,"SQLLite créé");
        }

        // Créer la table
        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                    + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                    + KEY_EMAIL + " TEXT UNIQUE," + KEY_CREATED_AT + " TEXT"
                    + ")";
            db.execSQL(CREATE_LOGIN_TABLE);

            Log.d(TAG, "Tables créées");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

            //Recréer les tables
            onCreate(db);
        }


        public void addUser(String name, String email, String created_at) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, name); // Name
            values.put(KEY_EMAIL, email); // Email
            values.put(KEY_CREATED_AT, created_at); // Created At

            // Insérer la ligne finalement
            long id = db.insert(TABLE_USER, null, values);
            db.close();

            Log.d(TAG, "New user inserted into sqlite: " + id);
        }

        /**
         * Récupérer les infos d'un utilisateur
         * @return une Hashmap avec les infos de l'utilisateur
         */
        public HashMap<String, String> getUserDetails() {
            HashMap<String, String> user = new HashMap<String, String>();
            String selectQuery = "SELECT  * FROM " + TABLE_USER;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            //Récupérer seulement la premiere ligne (un seul utilisateur connecté)
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                user.put("name", cursor.getString(1));
                user.put("email", cursor.getString(2));
                user.put("created_at", cursor.getString(3));
            }
            cursor.close();
            db.close();
            // return user
            Log.d(TAG, "Récupération de l'utilisateur : " + user.toString());

            return user;
        }


        /**
         * Tout effacer !
         */
        public void deleteUsers() {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_USER, null, null);
            db.close();

            Log.d(TAG, "TOUS les utilisateurs ont été supprimés");
        }

}
