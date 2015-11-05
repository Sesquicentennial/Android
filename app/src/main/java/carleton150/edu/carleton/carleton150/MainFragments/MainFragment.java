package carleton150.edu.carleton.carleton150.MainFragments;

import android.location.Location;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.GeoPoint;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;

/**
 * Created by haleyhinze on 10/28/15.
 */
public class MainFragment extends Fragment {

    VolleyRequester volleyRequester = new VolleyRequester();

    public MainFragment() {

    }

    public void handleGeofenceChange(ArrayList<GeoPoint> currentGeofences) {

    }

    public void queryDatabase(){
        volleyRequester.request(this);
    }

    //TODO: should be array of JSONObjects probably
    public void handleResult(JSONObject result){

    }

    public void handleLocationChange(Location newLocation){

    }

}


