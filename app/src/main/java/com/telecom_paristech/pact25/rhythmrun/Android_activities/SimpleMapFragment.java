package com.telecom_paristech.pact25.rhythmrun.Android_activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telecom_paristech.pact25.rhythmrun.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class SimpleMapFragment extends Fragment implements OnMapReadyCallback {

    protected MapView mapView;
    protected GoogleMap googleMap;

    protected static LocationManager locationManager;
    protected static String provider;

    private PolylineOptions polylineOptions;
    protected LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private LocationListener tempLocationListener;

    public SimpleMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("SimpleMap","Initialization of SimpleMapFragment.");

        final View rootView = initView(inflater, container);

        //Initialization of location handling.
        Criteria criteria = new Criteria();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, false);
        Log.d("SimpleMap","Location initialized with provider "+provider+".");

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            final Context context = getContext();
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Location services are not enabled.");
            dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            /*dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });*/
            dialog.show();
        }

        mapView.onCreate(savedInstanceState);
        mapView.onResume(); //Needed to get the map to display immediately

        try {
            Log.d("SimpleMap","Initializing the map.");
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        return rootView;
    }

    public View initView(LayoutInflater inflater, ViewGroup container){
        /*
            Initialization of the views holders.
            It can be overridden by child class to init other views.
         */
        Log.v("SimpleMap","view initialization.");
        View rootView = inflater.inflate(R.layout.fragment_simple_map, container, false);
        mapView = (MapView) rootView.findViewById(R.id.simpleMapView);
        return rootView;
    }

    public void zoomToCurrentLocation(){
        /*
            Creates a location listener to get current location and zoom on it as soon as found.
         */
        if(googleMap != null){
            tempLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("SimpleMap","Starting the request for a single location.");
                locationManager.requestLocationUpdates(provider,2,2,tempLocationListener);
                waitForLocation(); //Starts checking if a location is available.
            } else {
                Log.e("SimpleMap","Error while zooming to current location: no location permission.");
            }
        } else { //Waits for googleMap to be initialized.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            zoomToCurrentLocation();
                        }
                    });
                }
            }).start();
        }
    }

    private void waitForLocation(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);
            if(location == null){ //No location has been found.
                Log.v("SimpleMap","location still null... Retrying in 100ms.");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        waitForLocation();
                    }
                }).start();
            } else { //A location has been found.
                Log.d("SimpleMap","location found: "+location);
                locationManager.removeUpdates(tempLocationListener); //Stopping the location requests.
                zoomToLocation(location); //Zooming to location.
            }
        } else {
            Log.e("SimpleMap","Error while waiting for location: no location permission.");
        }
    }

    private void zoomToLocation(final Location location){
        //Interaction with a view requires to be run on main UI thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v("SimpleMap","Zooming to location "+location);
                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));
            }
        });
    }

    public void startContinuousLocation(){
        //Starts the main location requests.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, Macros.UPDATE_IDLE, 2, locationListener);
            googleMap.setMyLocationEnabled(true);
            Log.d("SimpleMap","Starting the request for continuous locations.");
        } else {
            Log.e("SimpleMap","Error while starting continuous location: no location permission.");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        Log.d("SimpleMap","Map is ready: "+googleMap);
        if (ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //No location permission.
            Log.e("SimpleMap","Error on map ready: no location permission.");
            return;
        }

        googleMap.getUiSettings().setMapToolbarEnabled(false); //Disable the useless toolbar
        googleMap.getUiSettings().setAllGesturesEnabled(false); //Disable scrolling

        //If there is a polyline to draw, draws a polyline.
        if(polylineOptions != null){
            Log.d("SimpleMap","Adding a polyline in onMapReady.");
            map.addPolyline(polylineOptions);
            polylineOptions=null;
        }
    }

    public void drawnPolyline(PolylineOptions polylineOptions){
        this.polylineOptions = polylineOptions;
        if(polylineOptions != null && googleMap != null){
            googleMap.addPolyline(polylineOptions);
            this.polylineOptions = null;
            Log.v("SimpleMap","Successfully drawn polyline.");
        } else {
            Log.w("SimpleMap","Could not draw polyline. Map: "+googleMap+". PolylineOptions: "+polylineOptions);
        }
    }

    public void waitToAnimateCamera(final LatLngBounds bounds){
        /*
            Waits for the map to be ready to zoom on bounds.
         */
        if(bounds==null) {
            Log.w("SimpleMap","waitToAnimateCamera: no bounds given.");
            return ;
        }
        if(canAnimateCamera()){
            //Interaction with views requires UI thread.
            Log.d("SimpleMap","Map is ready. Camera can be animated.");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    actuallyAnimateCamera(bounds);
                }
            });
        } else {
            Log.v("SimpleMap","Map not ready for animation. Retrying in 100ms.");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    waitToAnimateCamera(bounds);
                }
            }).start();
        }
    }

    private void actuallyAnimateCamera(LatLngBounds bounds){
        Log.v("SimpleMap","Animating camera on bounds "+bounds);
        int padding=mapView.getWidth()/10; //One tenth of padding around the bounds.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);
    }

    private boolean canAnimateCamera(){
        //Checks if the map is ready and the mapView inflated (positive dimensions).
        return googleMap != null && mapView.getWidth() > 0 && mapView.getHeight() > 0;
    }

    public void stopLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("SimpleMap","End of location requests.");
            locationManager.removeUpdates(locationListener);
        } else {
            Log.e("SimpleMap","Error ending location updates: no location permission.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.v("SimpleMap","Resuming map view.");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.v("SimpleMap","Pausing map view.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.v("SimpleMap","Destroying map view.");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        Log.v("SimpleMap","On Low Memory of map view.");
    }

    public static double distanceOfPolyline(PolylineOptions polylineOptions){
        /*
            Returns the length of a polyline in meters.
         */
        Log.d("SimpleMap","Calculating distance of polyline.");
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
        return (double) sum;
    }

}
