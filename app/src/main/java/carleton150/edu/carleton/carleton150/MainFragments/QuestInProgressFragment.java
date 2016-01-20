package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectContent;
import carleton150.edu.carleton.carleton150.POJO.Quests.Geofence;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
import carleton150.edu.carleton.carleton150.POJO.Quests.Waypoint;
import carleton150.edu.carleton.carleton150.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestInProgressFragment extends MainFragment {


    private Quest quest = null;
    private int numClue = 0;
    private TextView txtClue;
    private Button btnFoundIt;
    private Button btnShowHint;
    private TextView txtHint;
    private String locationNotFoundString = "Sorry, you have not yet reached the correct location. If you need a hint," +
            " please click the hint button below.";

    private boolean zoomCamera = true;
    private static LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    public QuestInProgressFragment() {
        // Required empty public constructor
    }

    public void initialize(Quest quest){
        this.quest = quest;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quest_in_progress, container, false);
        txtClue = (TextView) v.findViewById(R.id.txt_clue);
        btnFoundIt = (Button) v.findViewById(R.id.btn_found_location);
        btnShowHint = (Button) v.findViewById(R.id.btn_show_hint);
        txtHint = (TextView) v.findViewById(R.id.txt_hint);

        btnShowHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtHint.setText(quest.getWaypoints().get(String.valueOf(numClue)).getHint());
                txtHint.setVisibility(View.VISIBLE);
                locationNotFoundString = "Sorry, you have not yet reached the correct location";
            }
        });

        btnFoundIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfClueFound();
            }
        });

        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded(); // For setting up the MapFragment
        }
        setGeofences();
        return v;
    }

    private void checkIfClueFound(){
        Location curLocation = mMap.getMyLocation();
        Geofence hintGeofence = quest.getWaypoints().get(String.valueOf(numClue)).getGeofence();
        double lat = Double.valueOf(hintGeofence.getLat());
        double lon = Double.valueOf(hintGeofence.getLng());
        double rad = Double.valueOf(hintGeofence.getRad());
        float[] results = new float[1];
        Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(),
                lat, lon,
                results);
        if(results[0] <= rad){
            clueCompleted();
        }else{
            mainActivity.showAlertDialog(locationNotFoundString,
                    new AlertDialog.Builder(mainActivity).create());
        }
    }

    /***** Sets up the map if it is possible to do so *****/
    public boolean setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.quest_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                return true;
            } else {
                //TODO: display message saying unable to set up map
                return false;
            }
        }
        return true;
    }

    /**
     * Sets up the map (should only be called if mMap is null)
     * Monitors the zoom and target of the camera and changes them
     * if the user zooms out too much or scrolls map too far off campus.
     */
    private void setUpMap() {

        // For showing a move to my location button and a blue
        // dot to show user's location
        //TODO: the move to my location button is behind the toolbar -- fix it
        mMap.setMyLocationEnabled(true);

        //Makes it so user can't zoom out very far
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                setCamera();
                //TODO: figure out best zoom level for campus
                if (cameraPosition.zoom <= 13) {
                    if (cameraPosition.target == null) {
                        setCamera();
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                }

                //makes it so user can't scroll too far off campus
                //TODO: figure out best map limits
                double latitude = cameraPosition.target.latitude;
                double longitude = cameraPosition.target.longitude;

                CameraPosition newCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))
                        .zoom(cameraPosition.zoom)
                        .bearing(cameraPosition.bearing)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));

            }
        });

        setCamera();
    }

    /**
     * Sets up the map
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mMap != null)
            setUpMap();
        setUpMapIfNeeded();
    }

    /**
     * Sets the camera for the map. If we have user location, sets the camera to that location.
     * Otherwise, the camera target is the center of campus.
     */
    //TODO: figure out what to do if user is off campus and handle that appropriately
    private void setCamera(){
        if(mainActivity.getGeofenceMonitor().currentLocation != null && zoomCamera) {
            zoomCamera = false;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mainActivity.getGeofenceMonitor().currentLocation.getLatitude(), mainActivity.getGeofenceMonitor().currentLocation.getLongitude()))
                    .zoom(15)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }if(mainActivity.getGeofenceMonitor().currentLocation == null){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(CENTER_CAMPUS.latitude, CENTER_CAMPUS.longitude))
                    .zoom(15)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /**
     * Lifecycle method overridden to set up the map and check for internet connectivity
     * when the fragment comes into focus
     */
    @Override
    public void onResume() {
        super.onResume();
        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded();
        }
    }

    /**
     * Updates map view to reflect user's new location
     *
     * @param newLocation
     */
    @Override
    public void handleLocationChange(Location newLocation) {
        super.handleLocationChange(newLocation);
        mainActivity.getGeofenceMonitor().handleLocationChange(newLocation);
        setCamera();

        if(mainActivity.getGeofenceMonitor().currentLocation != null) {
            setUpMapIfNeeded();
        }
    }

    public boolean setGeofences(){
        Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: setting geofences");
        String currentClue = String.valueOf(numClue);
        boolean finished = false;
        if(quest.getWaypoints().get(currentClue) == null &&
                quest.getWaypoints().get(String.valueOf(numClue - 1)) != null){
            finished = true;
            return finished;
        }

        HashMap<String, Waypoint> waypointHashMap = quest.getWaypoints();
        if(waypointHashMap == null){
            Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: waypointHashMap is null");
        }else{
            Waypoint waypoint = waypointHashMap.get(currentClue);
            if(waypoint == null){
                Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: waypoint is null");
            }else {
                Geofence geofence = waypoint.getGeofence();
                if(geofence == null){
                    Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: geofence is null");
                }else {
                    if(mainActivity == null){
                        Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: mainActivity is null");
                    }else{
                        if(mainActivity.getGeofenceMonitor() == null){
                            Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: geofenceMonitor is null");
                        }
                    }
                    mainActivity.getGeofenceMonitor().addQuestGeofence(geofence);
                    Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: adding geofence");
                    txtClue.setText(quest.getWaypoints().get(currentClue).getClue());
                }
            }
        }

        try {
            mainActivity.getGeofenceMonitor().addQuestGeofence(quest.getWaypoints().get(currentClue).getGeofence());
            txtClue.setText(quest.getWaypoints().get(currentClue).getClue());
        }catch (NullPointerException e){
            Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: the current thing is null");
            e.printStackTrace();
        }
        return finished;
    }

    @Override
    public void clueCompleted() {
        Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: clueCompleted");
        numClue += 1;
        boolean completedQuest = setGeofences();
        if (completedQuest){
            showCompletedQuestMessage();
        }
    }

    private void showCompletedQuestMessage(){
        txtClue.setText("Quest completed! Message is: " + quest.getCompMsg());
    }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();

    }

    @Override
    public void fragmentInView() {
        super.fragmentInView();
        if(mainActivity != null) {
            mainActivity.getGeofenceMonitor().setCurFragment(3);
        }
        setGeofences();
    }
}
