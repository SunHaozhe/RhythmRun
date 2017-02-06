package com.example.raphaelattali.rythmrun.Android_activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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

import com.example.raphaelattali.rythmrun.R;

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

    private static PolylineOptions itinerary;

    private OnTouchListener listener;

    private ArrayList<LatLng> markerPoints = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private Polyline polyline;
    private float distance;

    private TextView distanceTextView;

    public ItineraryFragment() {
        super();
        // Required empty public constructor
    }

    public PolylineOptions getItinerary(){
        return itinerary;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ((ViewGroup) rootView).addView(frameLayout,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
        return rootView;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container){
        View rootView = inflater.inflate(R.layout.fragment_itinerary, container, false);
        mapView = (MapView) rootView.findViewById(R.id.itineraryMapView);

        /*ImageButton buttonDelete = (ImageButton) rootView.findViewById(R.id.itineraryButtonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLastMarker();
            }
        });
        ImageButton buttonRoute = (ImageButton) rootView.findViewById(R.id.itineraryButtonRoute);
        buttonRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateDirection();
            }
        });*/

        return rootView;
    }

    public void setListener(OnTouchListener listener){
        this.listener = listener;
    }

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
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                if (markerPoints.size() > 22) {
                    markerPoints.clear();
                    googleMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(point);
                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();
                // Setting the position of the marker
                options.position(point);
                options.draggable(true);
                if (markerPoints.size() == 1) {
                    options.title("Start");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    if(markerPoints.size()>2){
                        markers.get(markers.size()-1).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        markers.get(markers.size()-1).setTitle("Waypoint "+Integer.toString(markers.size()-1));
                    }
                    options.title("End");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map
                markers.add(googleMap.addMarker(options));

            }
        });
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                markerPoints.set(markers.indexOf(marker), marker.getPosition());
            }
        });
        zoomToCurrentLocation();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
        }
    }

    public void initiateDirection(TextView textView){
        distanceTextView = textView;
        initiateDirection();
    }

    public void initiateDirection(){
        if(markerPoints.size()>1){
            if(polyline != null){
                polyline.remove();
                polyline=null;
            }

            String url = getUrl(markerPoints);
            Log.d("onMapClick", url);
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerPoints.get(0)));
            //googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
    }

    public void removeLastMarker(){
        if(markers.size()>0){
            markers.get(markers.size()-1).remove();
            markers.remove(markers.size()-1);
            markerPoints.remove(markerPoints.size()-1);
        }
    }

    public double getDistance(){
        return distance;
    }

    public String getUrl(ArrayList<LatLng> markerPoints){
        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(markerPoints.size()-1);

        String url= "https://maps.googleapis.com/maps/api/directions/json?" +
                "mode=walking"+
                "&origin="+origin.latitude+","+origin.longitude+
                "&destination="+dest.latitude+","+dest.longitude;
        if(markerPoints.size()>=3)
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
            //noinspection ThrowFromFinallyBlock
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static float distanceOfPolyline(PolylineOptions polylineOptions){
        List<LatLng> table = polylineOptions.getPoints();
        int size = table.size() - 1;
        float[] results = new float[1];
        float sum = 0;

        for(int i = 0; i < size; i++){
            Location.distanceBetween(
                    table.get(i).latitude,
                    table.get(i).longitude,
                    table.get(i+1).latitude,
                    table.get(i+1).longitude,
                    results);
            sum += results[0];
        }
        return sum; //in meters
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
                distance = distanceOfPolyline(itinerary);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                final String unit = sharedPreferences.getString("unit_list","km");
                distanceTextView.setText(new Distance(distance/1000).toStr(unit,true));
            }
            else {
                Log.d("onPostExecute","without Poly lines drawn");
            }
        }
    }

    public class DataParser {

        /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
        @SuppressWarnings("unchecked")
        public List<List<HashMap<String,String>>> parse(JSONObject jObject){

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

}
