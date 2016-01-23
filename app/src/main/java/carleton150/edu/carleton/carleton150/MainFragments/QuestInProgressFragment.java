package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.Quests.Geofence;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;
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

    private boolean zoomCamera = true;
    private static LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    public QuestInProgressFragment() {
        // Required empty public constructor
    }

    /**
     * This must be called after creating the QuestInProgressFragment in order to pass
     * it the current quest
     * @param quest quest to be completed by user
     */
    public void initialize(Quest quest){
        this.quest = quest;
    }


    /**
     * Sets OnClickListeners to register when hint button or found it button is clicked
     * and to show the hint or check if the user is within a valid radius of the waypoint
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
                if(quest.getWaypoints().get(String.valueOf(numClue)) != null) {
                    btnShowHint.setVisibility(View.GONE);
                    String hint = quest.getWaypoints().get(String.valueOf(numClue)).getHint();
                    if(hint.equals("")){
                        txtHint.setText(getResources().getString(R.string.no_hint_available));
                    }else {
                        txtHint.setText(quest.getWaypoints().get(String.valueOf(numClue)).getHint());
                    }
                //If quest is completed, sets the hint to blank
                }else{
                    txtHint.setText("");
                }
                txtHint.setVisibility(View.VISIBLE);
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
        updateCurrentWaypoint();
        return v;
    }

    /**
     * Checks if the user's current location is within the radius of the waypoint
     * (both the radius and waypoint are specified in the quest object)
     */
    private void checkIfClueFound(){
        Location curLocation = mMap.getMyLocation();
        if(curLocation != null) {
            Geofence hintGeofence = quest.getWaypoints().get(String.valueOf(numClue)).getGeofence();
            double lat = Double.valueOf(hintGeofence.getLat());
            double lon = Double.valueOf(hintGeofence.getLng());
            double rad = Double.valueOf(hintGeofence.getRad());
            float[] results = new float[1];

            Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(),
                    lat, lon,
                    results);
            if (results[0] <= rad) {
                clueCompleted();
            } else {
                //String to display if hint is not already showing
                String alertString = getActivity().getResources().getString(R.string.location_not_found_hint);
                if (txtHint.getVisibility() == View.VISIBLE) {
                    //String to display if hint is already showing
                    alertString = getActivity().getResources().getString(R.string.location_not_found);
                }
                mainActivity.showAlertDialog(alertString,
                        new AlertDialog.Builder(mainActivity).create());
            }
        }else{
            Log.i(logMessages.LOCATION, "QuestInProgressFragment: checkIfClueFound: location is null");
            //TODO: this shouln't happen. Handle it better...
        }
    }

    /***** Sets up the map if it is possible to do so *****/
    public boolean setUpMapIfNeeded() {
        boolean setUp = true;
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            mMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.quest_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                setUp = true;
            } else {
                mainActivity.showAlertDialog(getResources().getString(R.string.unable_to_set_up_map),
                        new AlertDialog.Builder(mainActivity).create());
                return false;
            }
        }
        return setUp;
    }


    /**
     * Sets up the map
     * Monitors the zoom and target of the camera and changes them
     * if the user zooms out too much or scrolls map too far off campus.
     */
    private void setUpMap() {
            // For showing a move to my location button and a blue
            // dot to show user's location
            mMap.setMyLocationEnabled(false);

            //Makes it so user can't zoom out very far
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    setCamera();
                    //TODO: figure out best zoom level for campus
                    try {
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
                    }catch (NullPointerException e) {
                        e.printStackTrace();
                    }

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
        if(mMap != null) {
            if (mainActivity.getGeofenceMonitor().currentLocation != null && zoomCamera) {
                zoomCamera = false;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mainActivity.getGeofenceMonitor().currentLocation.getLatitude(),
                                mainActivity.getGeofenceMonitor().currentLocation.getLongitude()))
                        .zoom(15)
                        .bearing(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if (mainActivity.getGeofenceMonitor().currentLocation == null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(CENTER_CAMPUS.latitude, CENTER_CAMPUS.longitude))
                        .zoom(15)
                        .bearing(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    /**
     * Lifecycle method overridden to set up the map and check for internet connectivity
     * when the fragment comes into focus
     */
    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Updates map view to reflect user's new location
     *
     * @param newLocation
     */
    @Override
    public void handleLocationChange(Location newLocation) {
        super.handleLocationChange(newLocation);
        setCamera();
        drawLocationMarker(newLocation);

        if(mainActivity.getGeofenceMonitor().currentLocation != null) {
            setUpMapIfNeeded();
        }
    }

    private void drawLocationMarker(Location newLocation) {
        mMap.clear();
        Bitmap knightIcon = BitmapFactory.decodeResource(getResources(), R.drawable.knight_horse_icon);
        LatLng position = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(knightIcon);
        Marker curLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title("Current Location")
                .icon(icon));
    }

    /**
     * Checks if the quest is finished. If not, sets the text to show the next clue
     *
     * @return boolean, true if quest is finished, false otherwise
     */
    public boolean updateCurrentWaypoint(){
        String currentClue = String.valueOf(numClue);
        boolean finished = false;
        if(quest.getWaypoints().get(currentClue) == null &&
                quest.getWaypoints().get(String.valueOf(numClue - 1)) != null){
            finished = true;
            return finished;
        }
        txtClue.setText(quest.getWaypoints().get(currentClue).getClue());
        return finished;
    }

    /**
     * Handles when a clue has been completed by incrementing the clue
     * number, updating the current waypoint, and checking if the quest is completed
     */
    public void clueCompleted() {
        Log.i(logMessages.GEOFENCE_MONITORING, "QuestInProgressFragment: clueCompleted");
        numClue += 1;
        boolean completedQuest = updateCurrentWaypoint();
        if (completedQuest){

            showCompletedQuestMessage();
        }
    }

    /**
     * Shows the message stored with the quest when the quest has been
     * completed
     */
    private void showCompletedQuestMessage(){
        btnShowHint.setVisibility(View.GONE);
        btnFoundIt.setVisibility(View.GONE);
        txtClue.setText(getResources().getString(R.string.quest_completed_show_message) + quest.getCompMsg());
        txtHint.setVisibility(View.GONE);
    }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();



    }

    /**
     * Called when the fragment comes into view (different than onResume() because
     * the viewPager keeps several fragments in resumed state. This method is called
     * when the fragment actually comes into view on the screen
     *
     * Sets up the map if it hasn't already been set up, updates the waypoints,
     * and sets the map camera
     */
    @Override
    public void fragmentInView() {
        Log.i(logMessages.LOCATION, "QuestInProgressFragment : fragmentInView : called");
        if(mainActivity != null) {
            if(mainActivity.isConnectedToNetwork()) {
                setUpMapIfNeeded();
            }
        }
        updateCurrentWaypoint();
        //setUpMap();
        if(mainActivity.mLastLocation != null){
            Log.i(logMessages.LOCATION, "QuestInProgressFragment : fragmentInView : last location not null, drawing marker");
            drawLocationMarker(mainActivity.mLastLocation);
        }
        setCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            getChildFragmentManager().beginTransaction()
                    .remove(getChildFragmentManager().findFragmentById(R.id.quest_map)).commit();
            mMap = null;
        }
        zoomCamera = true;

        super.onSaveInstanceState(outState);
    }


}
