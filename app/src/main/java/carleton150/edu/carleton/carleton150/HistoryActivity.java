package carleton150.edu.carleton.carleton150;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

import carleton150.edu.carleton.carleton150.DialogFragments.SimpleTextDialogFragment;
import carleton150.edu.carleton.carleton150.Models.DummyLocations;

public class HistoryActivity extends FragmentActivity implements LocationListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location currentLocation = null;
    private LocationManager locationManager;

    private TextView txt_lat;
    private TextView txt_long;
    private String TAG_NO_GPS = "noGPS";
    private String TAG_NO_NETWORK = "noNetwork";

    MarkerOptions curLocationMarkerOptions;
    Marker curLocationMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        txt_lat = (TextView) findViewById(R.id.txt_lat);
        txt_long = (TextView) findViewById(R.id.txt_long);
        if (gpsOn()) {
            setLocationListener();
            getCoordinates();
            txt_long.setText("Longitude:" + currentLocation.getLongitude());
            txt_lat.setText("Latitude: " + currentLocation.getLatitude());
        } else {
            showSimpleTextDialog(TAG_NO_GPS);
        }

        if(isConnectedToNetwork()) {
            setUpMapIfNeeded();
        } else {
            showSimpleTextDialog(TAG_NO_NETWORK);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLocationListener();
        getCoordinates();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        locationManager.removeUpdates(this);
        super.onPause();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        addCurLocationMarker();
        addCampusOverlays();
        setCamera();
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void addCurLocationMarker(){
        if (currentLocation != null){
            if(curLocationMarker!=null) {
                curLocationMarker.remove();
            }
            curLocationMarkerOptions = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title("Your Location");
            curLocationMarker = mMap.addMarker(curLocationMarkerOptions);
        }
    }

    private void addCampusOverlays(){
        DummyLocations dummyLocations = new DummyLocations();
        List<List> locations = dummyLocations.getLocationsArray();
        for(int i = 0; i<locations.size(); i++){
            PolygonOptions myPolygon = new PolygonOptions();
            for(int j = 0; j<locations.get(i).size(); j++){
                LatLng point = (LatLng)locations.get(i).get(j);
                myPolygon.add(point);
            }
            myPolygon.fillColor(getResources().getColor(R.color.dark_gray_orange))
                    .strokeWidth(4).strokeColor(R.color.dark_gray_orange);
            mMap.addPolygon(myPolygon);
        }

        CircleOptions myCircleOptions = new CircleOptions();
        myCircleOptions.center(new LatLng(44.45997433, -93.15474433));
        myCircleOptions.radius(10.0);
        myCircleOptions.fillColor(R.color.hot_pink);
        myCircleOptions.strokeColor(R.color.bright_orange);
        myCircleOptions.strokeWidth(3);
        mMap.addCircle(myCircleOptions);
    }

    private void setCamera(){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(curLocationMarker.getPosition())
                .zoom(13)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        addCurLocationMarker();
        txt_lat.setText("Latitude: " + currentLocation.getLatitude());
        txt_long.setText("Longitude: " + currentLocation.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * sets the LocationManager, makes sure the GPS is on, and starts
     * getting location updates
     */
    private void setLocationListener(){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Log.i("location info ", "provider is enabled");
            getLocation(provider);
        } else {
            showSimpleTextDialog(TAG_NO_GPS);
        }
    }



    /**
     * If GPS is on, returns coordinates from the last known location.
     * This function is to be called after setLocationListener() so
     * the last known location is current.
     *
     * @return ArrayList<String> coordinates
     */
    public void getCoordinates(){

        locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        currentLocation = locationManager.getLastKnownLocation(provider);
        Log.i("location info ", "provider is enabled");

        if (currentLocation != null) {
            Log.i("location info:  ", "not null");

        } else{
            Log.i("location info: ", "location is null");
        }
    }

    public void getLocation(String usedLocationService) {
        long updateTime = 100;
        float updateDistance = 0;
        locationManager.requestLocationUpdates(usedLocationService, updateTime, updateDistance, this);
    }


    private void showSimpleTextDialog(String tag){
        //gets rid of the fragment if it already exists
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        android.app.Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = SimpleTextDialogFragment.newInstance();
        newFragment.show(ft, tag);
    }

    private boolean isConnectedToNetwork(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean gpsOn(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
