package carleton150.edu.carleton.carleton150.Models;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by haleyhinze on 10/8/15.
 */
public class MyLocationListener implements LocationListener {


    private Location myLocation = null;
    private Activity parentActivity = null;
    private LocationManager locationManager;

    private boolean noGPS = true;

    public void initialize(Activity activity){
        parentActivity = activity;
        locationManager = (LocationManager)parentActivity.getSystemService(Context.LOCATION_SERVICE);


        if (gpsOn()) {
            noGPS = false;
            setLocationListener();
            getCoordinates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Location info", "Location changed! new Lat: " + location.getLatitude() + "new Long: " + location.getLongitude());
        myLocation = location;
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
            noGPS = false;
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Log.i("location info ", "provider is enabled");
            getLocation(provider);
        } else {
            noGPS = true;
        }
    }



    /**
     * If GPS is on, returns coordinates from the last known location.
     * This function is to be called after setLocationListener() so
     * the last known location is current.
     *
     * @return ArrayList<String> coordinates
     */
    private void getCoordinates(){

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        myLocation = locationManager.getLastKnownLocation(provider);
        Log.i("location info ", "provider is enabled");

        if (myLocation != null) {
            Log.i("location info:  ", "not null");

        } else{
            Log.i("location info: ", "location is null");
        }
    }


    private void getLocation(String usedLocationService) {
        int delay = 0;
        int period = 100;
        locationManager.requestLocationUpdates(usedLocationService, period, delay, this);
    }

    private boolean gpsOn(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public Location getMyLocation() {
        return myLocation;
    }

    public boolean isNoGPS() {
        return noGPS;
    }

    public void startListener(){
        if (gpsOn()) {
            noGPS = false;
            setLocationListener();
            getCoordinates();
        }
    }

    public void stopListener(){
        locationManager.removeUpdates(this);
    }
}
