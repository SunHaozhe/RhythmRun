package com.telecom_paristech.pact25.rhythmrun.Android_activities;

public final class Macros {
    static final String EXTRA_RUN_DATA = "run_data";
    static final String EXTRA_DISTANCE = "distance";
    static final String EXTRA_TIME = "time";
    static final String EXTRA_DATE = "date";
    static final String EXTRA_PACE = "pace";
    static final String EXTRA_ROUTE = "route";
    final static String EXTRA_SONG = "song";
    static final String EXTRA_MUSIC="music";
    static final String EXTRA_ITINERARY = "itinerary";
    static final String EXTRA_HISTORY_ITEM = "history_item";

    static final double MAX_SPEED = 25; //km/h
    static final double MIN_SPEED = 1; //km/h

    static final int UPDATE_IDLE = 1000; //ms

    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    static final int PERMISSION_READ_EXTERNAL_STORAGE = 2;
    static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 3;

    static final int MAX_MARKERS = 22; //Google max is 22
}
