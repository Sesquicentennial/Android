package carleton150.edu.carleton.carleton150.MainFragments;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.GeoPoint;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;

/**
 * Created by haleyhinze on 10/28/15.
 */
public class MainFragment extends Fragment {
    
    VolleyRequester volleyRequester = new VolleyRequester(this);

    public MainFragment() {

    }

    public void handleGeofenceChange(ArrayList<GeoPoint> currentGeofences) {

    }

    public void queryDatabase(){
        //httpRequester.execute();
        volleyRequester.request();
    }

    public void handleResult(String result){

    }

}


