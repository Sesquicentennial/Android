package carleton150.edu.carleton.carleton150.MainFragments;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.MapView;

import org.json.JSONObject;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.GeoPoint;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;

/**
 * Created by haleyhinze on 10/28/15.
 */
public class MainFragment extends Fragment{

    VolleyRequester volleyRequester = new VolleyRequester();

    public MainFragment() {

    }

    public void handleGeofenceChange(ArrayList<Content> currentGeofences) {

    }

    public void queryDatabase(ArrayList<Content> geofence){
        Log.i("about to query database", geofence.toString());
        volleyRequester.request(this, geofence);
    }

    //TODO: should be array of JSONObjects probably
    public void handleResult(GeofenceInfoObject result){

    }

    public void handleLocationChange(Location newLocation){

    }

}


