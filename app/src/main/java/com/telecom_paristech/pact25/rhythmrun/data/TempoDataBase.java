package com.telecom_paristech.pact25.rhythmrun.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.telecom_paristech.pact25.rhythmrun.music.PathAndTempo;

/**
 * Created by ClemSurfaceBook on 30/01/2017.
 */

public class TempoDataBase extends SQLiteOpenHelper {

    private static final String TAG = TempoDataBase.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "songs";

    // Login table name
    private static final String TABLE_TEMPO = "songs_associated_to_tempo";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PATH = "path";
    private static final String KEY_TEMPO = "tempo";

    public TempoDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_TEMPO + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PATH + " TEXT," // UNIQUE,"
                + KEY_TEMPO + " DOUBLE"
                + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addSongAndTempo(String path, double freq) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("TempoDataBase","Checking file: "+path);

        String selectQuery = "SELECT  * FROM " + TABLE_TEMPO + " WHERE " + KEY_PATH + "=\"" + path + "\"";
        SQLiteDatabase db2 = this.getReadableDatabase();
        Cursor cursor = db2.rawQuery(selectQuery, null);
        Log.d("TempoDataBase", "Found file: "+cursor.getCount());

        if(cursor.getCount() == 0){
            Log.d("TempoDataBase", "Inserting file in database.");
            ContentValues values = new ContentValues();
            values.put(KEY_PATH, path); // chemin d'acces
            values.put(KEY_TEMPO, freq); // tempo

            // Inserting Row
            long id = db.insert(TABLE_TEMPO, null, values);
        } else {
            Log.d("TempoDataBase", "File already in database");
        }

        db.close(); // Closing database connection
        db2.close();
    }

    public double getTempo(String songPath) {
        String selectQuery = "SELECT  * FROM " + TABLE_TEMPO + " WHERE " + KEY_PATH + "=\"" + songPath + "\"" ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        double tempo = -1;
        if (cursor != null && cursor.moveToNext()) { //licite
            tempo = cursor.getDouble(2);
        }

        return tempo;
    }

    public PathAndTempo getSongThatFit(double tempoMin, double tempoMax) { //rajouter une liste de song a eviter (deja lues)
        String selectQuery = "SELECT  * FROM " + TABLE_TEMPO + " WHERE " + KEY_TEMPO + " BETWEEN '" + String.valueOf(tempoMin) + "' AND '" + String.valueOf(tempoMax) + "'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        PathAndTempo song = null;
        if (cursor != null && cursor.moveToNext()) {
            song = new PathAndTempo();
            song.path = cursor.getString(1);
            song.tempoHz = (float)cursor.getDouble(2);
        }

        return song;
    }

    public PathAndTempo getASong() {
        String selectQuery = "SELECT  * FROM " + TABLE_TEMPO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        PathAndTempo song = null;
        if (cursor != null && cursor.moveToNext()) {
            song = new PathAndTempo();
            song.path = cursor.getString(1);
            song.tempoHz = (float)cursor.getDouble(2);
        }

        return song;
    }

    public void clear() { //pour les tests
        this.getReadableDatabase().execSQL("DELETE FROM "+ TABLE_TEMPO);
    }
}
