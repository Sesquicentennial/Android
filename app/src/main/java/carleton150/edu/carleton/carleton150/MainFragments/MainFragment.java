package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created on 10/28/15.
 * Super class for all of the main view fragments. Ensures that they have
 * some methods in common so that the MainActivity can call these methods
 * without knowing which type of fragment is currently in view
 */
public class MainFragment extends Fragment{

    //communicates with server
    VolleyRequester volleyRequester = new VolleyRequester();
    protected boolean connected = true;



    protected MainActivity mainActivity;

    /**
     * Required empty constructor
     */
    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    /**
     * handles when the geofences that the user is currently in change
     * @param currentGeofences
     */
    public void handleGeofenceChange(ArrayList<Content> currentGeofences) {

    }



    /**
     * Handles results of query for information about geofences
     * @param result
     */
    public void handleResult(GeofenceInfoObject result){
        if(result == null){

                Toast toast = Toast.makeText(mainActivity, "Null info result", Toast.LENGTH_SHORT);
                toast.show();
            }
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

    public void googlePlayServicesConnected(){

    }

    public void stopProcesses(){

    }

    public void resumeProcesses(){

    }

}


