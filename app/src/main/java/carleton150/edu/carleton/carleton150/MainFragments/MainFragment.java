package carleton150.edu.carleton.carleton150.MainFragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.LogMessages;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.POJO.EventObject.Events;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectContent;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;

/**
 * Created on 10/28/15.
 * Super class for all of the main view fragments. Ensures that they have
 * some methods in common so that the MainActivity can call these methods
 * without checking which type of fragment is currently in view
 */
public class MainFragment extends Fragment{

    //communicates with server
    VolleyRequester volleyRequester = new VolleyRequester();
    public LogMessages logMessages = new LogMessages();
    public boolean isVisible = false;


    /**
     * Required empty constructor
     */
    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * handles when the geofences that the user is currently in change
     * @param currentGeofences
     */
    public void handleGeofenceChange(ArrayList<GeofenceObjectContent> currentGeofences) {

    }



    /**
     * Handles results of query for information about geofences
     * @param result
     */
    public void handleResult(GeofenceInfoObject result){
        /*if(result == null){
            MainActivity mainActivity = (MainActivity) getActivity();
                Toast toast = Toast.makeText(mainActivity, "Null info result", Toast.LENGTH_SHORT);
                toast.show();
            }*/
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
    public void handleNewGeofences(GeofenceObjectContent[] newGeofences){

    }

    /**
     * handles new events from server, called by VolleyRequester
     * @param events
     */
    public void handleNewEvents(Events events){

    }

    public void fragmentOutOfView(){
        isVisible = false;
    }

    public void fragmentInView(){
        isVisible = true;
    }

    public void handleNewQuests(ArrayList<Quest> newQuests){

    }

    public void printMessageToast(String message){
       /* MainActivity mainActivity = (MainActivity) getActivity();
        Toast toast = Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT);
        toast.show();*/
    }
}


