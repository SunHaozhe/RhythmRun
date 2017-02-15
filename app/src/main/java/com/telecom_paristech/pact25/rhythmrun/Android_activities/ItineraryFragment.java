package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.telecom_paristech.pact25.rhythmrun.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItineraryFragment extends SimpleMapFragment implements OnMapReadyCallback {

    //TODO: Warn if more than 22 way points are selected and block.

    private static PolylineOptions itinerary;

    private OnTouchListener listener;

    private ArrayList<LatLng> markerPoints = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private Polyline polyline;
    private Distance distance;

    private OnRouteCalculatedListener onRouteCalculatedListener;

    private TextView distanceTextView;

    public ItineraryFragment() {
        super();
        // Required empty public constructor
    }

    public PolylineOptions getItinerary(){
        return itinerary;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("Itinerary","Initialization of ItineraryFragment.");

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        //The TouchableWrapper is required to disable scrollview in favor of map scroll.
        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if (rootView != null) {
            ((ViewGroup) rootView).addView(frameLayout,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));
        } else {
            Log.e("Itinerary","Error while creating TouchableWrapper, rootView is null.");
        }

        return rootView;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container){
        View rootView = inflater.inflate(R.layout.fragment_itinerary, container, false);
        mapView = (MapView) rootView.findViewById(R.id.itineraryMapView);
        return rootView;
    }

    public void setListener(OnTouchListener listener){
        this.listener = listener;
    }

    //This interface allows the definition of a On Touch Listener.
    public interface OnTouchListener{
        void onTouch();
    }

    public class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context){
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    listener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    listener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        super.onMapReady(map);

        //Handling the addition of markers
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                if (markerPoints.size() > Macros.MAX_MARKERS) {
                    Log.w("Itinerary","Too much markers (max is "+Macros.MAX_MARKERS+"). Erasing all previous ones.");
                    markerPoints.clear();
                    googleMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);
                Log.d("Itinerary","Adding marker "+point+" (total: "+markerPoints.size()+").");
                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();
                // Setting the position of the marker
                options.position(point);
                options.draggable(true); //To be able to move already set markers.
                if (markerPoints.size() == 1) { //Sets the first marker
                    options.title("Start");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    if(markerPoints.size()>2){ //Sets previous marker to transition, orange marker.
                        markers.get(markers.size()-1).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        markers.get(markers.size()-1).setTitle("Marker "+Integer.toString(markers.size()-1));
                    }
                    options.title("End");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map
                markers.add(googleMap.addMarker(options));

            }
        });

        //Handling the drag of existing marker.
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //Updating the position of an already added marker.
                Log.d("Itinerary","Marker "+marker+" moved to "+marker.getPosition()+".");
                markerPoints.set(markers.indexOf(marker), marker.getPosition());
            }
        });

        zoomToCurrentLocation();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true); //Enables the button to zoom on current location and the display of location cursor.
            googleMap.getUiSettings().setAllGesturesEnabled(true); //Re-enable map touch.
        }
    }

    public void initiateRoute(TextView textView){
        //Starts the direction calculation
        Log.d("Itinerary","Beginning of route calculation.");
        distanceTextView = textView; //Save reference of TextView to display distance.
        initiateRoute();
    }

    public void initiateRoute(){
        if(markerPoints.size()>1){
            if(polyline != null){
                polyline.remove(); //Removes the polyline from the map.
                polyline=null; //Reset of the variable.
                Log.d("Itinerary","Existing polyline found. Deleted.");
            }

            String url = getUrl(markerPoints);
            Log.d("Itinerary", "Route request URL: "+url);
            FetchUrl FetchUrl = new FetchUrl();
            FetchUrl.execute(url);
        } else {
            Log.w("Itinerary","Can not calculate route, not enough markers ("+markerPoints.size()+" found).");
        }
    }

    public void removeLastMarker(){
        /*
            Removes the last marker added on the map by the user.
         */
        if(markers.size()>0){
            markers.get(markers.size()-1).remove(); //Removes the top element of the map list
            markers.remove(markers.size()-1); //Removes the last maker from the map
            markerPoints.remove(markerPoints.size()-1); //Removes the last marker from the controller list
            Log.d("Itinerary","Last marker removed.");
        } else {
            Log.w("Itinerary","No marker to be removed.");
        }
    }

    public boolean isRouteCalculationAvailable(){
        return markerPoints.size()>1 && polyline==null;
    }

    public Distance getDistance(){
        /*
            Returns distance of selected polyline, in km.
         */
        /*if(markerPoints.size()>1 && (polyline==null)){
            //The user has set up markers without clicking on "route".
            //distance=null;
            initiateRoute();
            Thread fetchDistanceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(distance==null){

                    }
                }
            });
            fetchDistanceThread.start();
            try {
                fetchDistanceThread.join(10000); //waits max 10sec for the thread to die
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        return distance;
    }

    public String getUrl(ArrayList<LatLng> markerPoints){
        /*
            Returns the url for the route request.
         */
        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(markerPoints.size()-1);

        String url= "https://maps.googleapis.com/maps/api/directions/json?" +
                "mode=walking"+
                "&origin="+origin.latitude+","+origin.longitude+
                "&destination="+dest.latitude+","+dest.longitude;
        if(markerPoints.size()>=3)
            //noinspection SpellCheckingInspection
            url = url + "&waypoints=";
        for(int i=1;i<markerPoints.size()-1;i++){
            url = url + markerPoints.get(i).latitude + "," + markerPoints.get(i).longitude + "|";
        }
        url = url + "&sensor=false";
        return url;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (iStream != null) {
                //noinspection ThrowFromFinallyBlock
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                //noinspection RedundantStringToString
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            itinerary = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                itinerary = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                itinerary.addAll(points);
                itinerary.width(10);
                itinerary.color(Color.RED);

                Log.d("onPostExecute","onPostExecute line options decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(itinerary != null) {
                polyline = googleMap.addPolyline(itinerary);
                distance = new Distance(SimpleMapFragment.distanceOfPolyline(itinerary)/1000);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                final String unit = sharedPreferences.getString("unit_list","km");
                if(distanceTextView!=null)
                    distanceTextView.setText(distance.toStr(unit,true));

                if(onRouteCalculatedListener!=null){
                    onRouteCalculatedListener.onRouteCalculated();
                    onRouteCalculatedListener=null;
                }
            }
            else {
                Log.d("onPostExecute","without Poly lines drawn");
            }
        }
    }

    public class DataParser {

        /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
        @SuppressWarnings("unchecked")
        List<List<HashMap<String,String>>> parse(JSONObject jObject){

            List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for(int i=0;i<jRoutes.length();i++){
                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();

                    /** Traversing all legs */
                    for(int j=0;j<jLegs.length();j++){
                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for(int k=0;k<jSteps.length();k++){
                            String polyline;
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for(int l=0;l<list.size();l++){
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude) );
                                hm.put("lng", Double.toString((list.get(l)).longitude) );
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ignored){
            }

            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int d_lat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += d_lat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int d_lng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += d_lng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }

    public interface OnRouteCalculatedListener{
        public void onRouteCalculated();
    }

    public void setOnRouteCalculatedListener(OnRouteCalculatedListener onRouteCalculatedListener){
        this.onRouteCalculatedListener = onRouteCalculatedListener;
    }

}
