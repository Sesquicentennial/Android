package carleton150.edu.carleton.carleton150.MainFragments;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.util.Log;
import java.util.ArrayList;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;

/**
 * Created on 10/28/15.
 * Super class for all of the main view fragments. Ensures that they have
 * some methods in common so that the MainActivity can call these methods
 * without knowing which type of fragment is currently in view
 */
public class MainFragment extends Fragment{

    //communicates with server
    VolleyRequester volleyRequester = new VolleyRequester();

    /**
     * Required empty constructor
     */
    public MainFragment() {

    }

    /**
     * handles when the geofences that the user is currently in change
     * @param currentGeofences
     */
    public void handleGeofenceChange(ArrayList<Content> currentGeofences) {

    }

    /**
     * queries database for information about geofences
     * @param geofence
     */
    public void queryDatabase(ArrayList<Content> geofence){
        Log.i("about to query database", geofence.toString());
        volleyRequester.request(this, geofence);
    }

    /**
     * Handles results of query for information about geofences
     * @param result
     */
    public void handleResult(GeofenceInfoObject result){

    }

    /**
     * handles when the user's location changes
     * @param newLocation
     */
    public void handleLocationChange(Location newLocation){

    }

    /**
     * handles when new geofences are set to be monitored
     * @param newGeofences
     */
    public void handleNewGeofences(Content[] newGeofences){

    }

}


