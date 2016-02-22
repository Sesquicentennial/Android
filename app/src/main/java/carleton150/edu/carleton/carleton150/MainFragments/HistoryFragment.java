package carleton150.edu.carleton.carleton150.MainFragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import carleton150.edu.carleton.carleton150.ExtraFragments.AddMemoryFragment;
import carleton150.edu.carleton.carleton150.ExtraFragments.RecyclerViewPopoverFragment;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectLocation;
import carleton150.edu.carleton.carleton150.R;

import static carleton150.edu.carleton.carleton150.R.id.txt_try_getting_geofences;

/**
 * The main fragment for the History section of the app
 *
 * Displays a map with markers indicating nearby points of interest. When a marker is clicked,
 * creates a RecyclerViewPopoverFragment to display the info for that point
 */
public class HistoryFragment extends MapMainFragment {

    private View view;
    private boolean needToCallUpdateGeofences = true;
    private boolean isMonitoringGeofences = false;

    //The geofences the user is currently in
    ArrayList<GeofenceObjectContent> currentGeofences;

    //The Markers for geofences that are currently being displayed
    ArrayList<Marker> currentGeofenceMarkers = new ArrayList<Marker>();

    //A Map of the info retrieved from the surver for geofences the user is currently in
    HashMap<String, GeofenceInfoContent[]> currentGeofencesInfoMap = new HashMap<>();

    //A Map of geofences the user is in that are currently being queried for using VolleyRequester
    HashMap<String, Integer> geofenceNamesBeingQueriedForInfo = new HashMap<>();

    private boolean debugMode = false;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = inflater.inflate(R.layout.fragment_history, container, false);

        //Managing UI
        final TextView txtRequestGeofences = (TextView) view.findViewById(txt_try_getting_geofences);
        final Button btnRequestGeofences = (Button) view.findViewById(R.id.btn_request_geofences);
        final Button btnGetNearbyMemories = (Button) view.findViewById(R.id.btn_get_nearby_memories);
        ImageView imgQuestion = (ImageView) view.findViewById(R.id.img_question);
        imgQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displays or hides tutorial depending on whether it is in view or not
                toggleTutorial();
            }
        });

        btnGetNearbyMemories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows a popover displaying nearby memories
                showMemoriesPopover();
            }
        });

        /*If geofences weren't retrieved (likely due to network error), shows button for user
        to try requesting geofences again. If it is clicked, calls updateGeofences() to get new
        geofences and draw the necessary map markers
         */
        btnRequestGeofences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGeofences();
                btnRequestGeofences.setVisibility(View.GONE);
                txtRequestGeofences.setText(getResources().getString(R.string.retrieving_geofences));
            }
        });

        //method to get new geofences and draw necessary map markers
        updateGeofences();
        needToCallUpdateGeofences = false;
        MainActivity mainActivity = (MainActivity) getActivity();

        if(mainActivity.checkIfGPSEnabled()) {
            //starts the mainActivity monitoring geofences
            mainActivity.getGeofenceMonitor().startGeofenceMonitoring();
            isMonitoringGeofences = true;
        }

        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded(); // For setting up the MapFragment
        }

        // Toggle tutorial if first time using app
        if (checkFirstHistoryRun()) {
            toggleTutorial();
        }

        if(debugMode){
            displayGeofenceInfo();
        }
        return view;
    }

    /**
     * If the tutorial is visible, makes it invisible. Otherwise, makes it visible
     */
    private void toggleTutorial(){
        final RelativeLayout relLayoutTutorial = (RelativeLayout) view.findViewById(R.id.tutorial);
        if(relLayoutTutorial.getVisibility() == View.VISIBLE){
            relLayoutTutorial.setVisibility(View.GONE);
        }else{
            relLayoutTutorial.setVisibility(View.VISIBLE);
        }
        Button btnCloseTutorial = (Button) view.findViewById(R.id.btn_close_tutorial);
        btnCloseTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLayoutTutorial.setVisibility(View.GONE);
            }
        });
    }


    /**
     * Sets up the map if necessary and possible
     *
     * @return
     */
    @Override
    /***** Sets up the map if it is possible to do so *****/
    public boolean setUpMapIfNeeded() {
        super.setUpMapIfNeeded();
        if (mMap != null) {
            //Shows history popover on marker clicks
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    showPopup(getContentFromMarker(marker), marker.getTitle());
                    return true;
                }
            });
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets up the map (should only be called if mMap is null)
     * Monitors the zoom and target of the camera and changes them
     * if the user zooms out too much or scrolls map too far off campus.
     */
    @Override
    protected void setUpMap() {
        super.setUpMap();
        // For showing a move to my location button and a blue
        // dot to show user's location
        MainActivity mainActivity = (MainActivity) getActivity();
        mMap.setMyLocationEnabled(mainActivity.checkIfGPSEnabled());
    }


    /**
     * Lifecycle method overridden to set up the map and check for internet connectivity
     * when the fragment comes into focus. If fragment is not already monitoring geofences,
     * begins monitoring geofences
     */
    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded();
        }
        if(mainActivity.checkIfGPSEnabled() && !isMonitoringGeofences) {
            //starts the mainActivity monitoring geofences
            mainActivity.getGeofenceMonitor().startGeofenceMonitoring();
            isMonitoringGeofences = true;
        }
        mMap.setMyLocationEnabled(mainActivity.checkIfGPSEnabled());
        if(needToCallUpdateGeofences) {
            updateGeofences();
        }
    }

    /**
     * Adds a marker to the map for each item in geofenceToAdd
     *
     * @param geofenceToAdd
     */
    private void addMarker(HashMap<String, GeofenceInfoContent[]> geofenceToAdd){
        System.gc();
        MainActivity mainActivity = (MainActivity) getActivity();

        for(Map.Entry<String, GeofenceInfoContent[]> e : geofenceToAdd.entrySet()){
            if(!currentGeofencesInfoMap.containsKey(e.getKey())) {
                currentGeofencesInfoMap.put(e.getKey(), e.getValue());
                geofenceNamesBeingQueriedForInfo.remove(e.getKey());
                String curGeofenceName = e.getKey();
                GeofenceObjectContent geofence = mainActivity.getGeofenceMonitor().curGeofencesMap.get(curGeofenceName);
                Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.basic_map_marker);
                LatLng position = new LatLng(geofence.getGeofence().getLocation().getLat(), geofence.getGeofence().getLocation().getLng());
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(markerIcon);
                MarkerOptions geofenceMarkerOptions = new MarkerOptions()
                        .position(position)
                        .title(curGeofenceName)
                        .icon(icon);
                Marker curGeofenceMarker = mMap.addMarker(geofenceMarkerOptions);
                currentGeofenceMarkers.add(curGeofenceMarker);
            }
        }
    }

    /**
     * Displays text stating which geofences the user is currently in. Used
     * only in debug mode
     */
    private void displayGeofenceInfo(){
        TextView locationView = (TextView) view.findViewById(R.id.txt_geopoint_info);
        locationView.setVisibility(View.VISIBLE);
        MainActivity mainActivity = (MainActivity) getActivity();
        String displayString = getResources().getString(R.string.currently_in_geofences_for);
        boolean showString = false;
        if(mainActivity.getGeofenceMonitor().curGeofences == null){
            return;
        }
        for(int i = 0; i<mainActivity.getGeofenceMonitor().curGeofences.size(); i++){
            displayString += mainActivity.getGeofenceMonitor().curGeofences.get(i).getName() + " ";
            showString = true;
        }
        if(showString) {
            locationView.setText(displayString);
        } else {
            locationView.setText(getResources().getString(R.string.no_items));
        }
    }


    /**
     * Handles the result of a query for information about a geofence.
     * @param result is a GeofenceInfoObject that contains information
     *               about all geofences the user is currently in
     */
    @Override
    public void handleResult(GeofenceInfoObject result) {
        super.handleResult(result);

        /*This is a call from the VolleyRequester, so this check prevents the app from
        crashing if the user leaves the tab while the app is trying
        to get quests from the server
         */
        if(this.isResumed()) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if(mainActivity != null) {
                final Button btnRequestInfo = (Button) view.findViewById(R.id.btn_request_info);
                final TextView txtRequestGeofences = (TextView) view.findViewById(txt_try_getting_geofences);
                if(result == null){
                    if(currentGeofences != null){
                        if(currentGeofences.size() != 0){
                            //If the result is null and it shouldn't be, displays error and allows user to request
                            //information again
                            btnRequestInfo.setVisibility(View.VISIBLE);
                            txtRequestGeofences.setText(getResources().getString(R.string.no_info_retrieved));
                            txtRequestGeofences.setVisibility(View.VISIBLE);
                            btnRequestInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    txtRequestGeofences.setText(getResources().getString(R.string.retrieving_info));
                                    btnRequestInfo.setVisibility(View.GONE);
                                    handleGeofenceChange(currentGeofences);
                                }
                            });
                        }
                    }
                }

                if (result != null) {
                    try {
                        Log.i(logMessages.GEOFENCE_MONITORING, "handleResult: result length is: " + result.getContent().size());
                        Log.i(logMessages.GEOFENCE_MONITORING, "handleResult: result is: " + result.getContent().toString());

                        btnRequestInfo.setVisibility(View.GONE);
                        txtRequestGeofences.setVisibility(View.GONE);
                        if(debugMode) {
                            displayGeofenceInfo();
                        }
                        addMarker(result.getContent());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
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
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getGeofenceMonitor().handleLocationChange(newLocation);
        setCamera();
        if(mainActivity.getGeofenceMonitor().currentLocation != null) {
            if(debugMode) {
                TextView txt_lat = (TextView) view.findViewById(R.id.txt_lat);
                TextView txt_long = (TextView) view.findViewById(R.id.txt_long);
                txt_lat.setVisibility(View.VISIBLE);
                txt_long.setVisibility(View.VISIBLE);
                txt_lat.setText("Latitude: " + mainActivity.getGeofenceMonitor().currentLocation.getLatitude());
                txt_long.setText("Longitude: " + mainActivity.getGeofenceMonitor().currentLocation.getLongitude());
            }
            setUpMapIfNeeded();
        }
    }

    /**
     * Returns the GeofenceInfoContent[] of info that each marker represents
     * @param marker
     * @return
     */
    private GeofenceInfoContent[] getContentFromMarker(Marker marker){
        return currentGeofencesInfoMap.get(marker.getTitle());
    }

    /**
     * Shows the history popover for a given marker on the map
     *
     * @param geofenceInfoObject
     */
    private void showPopup(GeofenceInfoContent[] geofenceInfoObject, String name){
        RelativeLayout relLayoutTutorial = (RelativeLayout) view.findViewById(R.id.tutorial);
        relLayoutTutorial.setVisibility(View.GONE);
        GeofenceInfoContent[] sortedContent = sortByDate(geofenceInfoObject);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecyclerViewPopoverFragment recyclerViewPopoverFragment = RecyclerViewPopoverFragment.newInstance(sortedContent, name);
        // Transaction start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom,
                R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fragmentTransaction.add(R.id.fragment_container, recyclerViewPopoverFragment, "RecyclerViewPopoverFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Uses bubble sort to sort geofenceInfoContents by date
     *
     * @param geofenceInfoContents
     * @return
     */
    private GeofenceInfoContent[] sortByDate(GeofenceInfoContent[] geofenceInfoContents){
        GeofenceInfoContent infoContent = null;
        for(int j = 0; j < geofenceInfoContents.length; j++) {
            for (int i = 0; i < geofenceInfoContents.length; i++) {
                infoContent = geofenceInfoContents[i];
                if (i < geofenceInfoContents.length - 1) {
                    String year1 = infoContent.getYear();
                    String year2 = geofenceInfoContents[i + 1].getYear();
                    int year1Int = Integer.parseInt(year1);
                    int year2Int = Integer.parseInt(year2);
                    if (year1Int < year2Int) {
                        geofenceInfoContents[i] = geofenceInfoContents[i + 1];
                        geofenceInfoContents[i + 1] = infoContent;
                    }
                }
            }
        }
        return geofenceInfoContents;
    }

    /**
     * Testing function to draw circles on maps to show the geofences we are
     * currently monitoring. Helps to ensure that we are getting geofences from
     * server
     *
     * @param geofences
     */

    public void drawGeofences(GeofenceObjectContent[] geofences) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getGeofenceMonitor().geofencesBeingMonitored = geofences;
        if(debugMode) {
            if(geofences != null) {
                mMap.clear();
                for (int i = 0; i < geofences.length; i++) {
                    carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Geofence geofence =
                            geofences[i].getGeofence();
                    CircleOptions circleOptions = new CircleOptions();
                    GeofenceObjectLocation location =
                            geofence.getLocation();
                    double lat = location.getLat();
                    double lon = location.getLng();
                    circleOptions.center(new LatLng(lat, lon));
                    circleOptions.radius(geofence.getRadius());
                    circleOptions.strokeColor(R.color.colorPrimary);
                    circleOptions.strokeWidth(5);
                    mMap.addCircle(circleOptions);
                }
            }
        }
    }


    /**
     * Called from VolleyRequester. Handles the JSONObjects received
     * when we requested new geofences from the server
     * @param geofencesContent
     */
    @Override
    public void handleNewGeofences(GeofenceObjectContent[] geofencesContent){
        /*This is a call from the VolleyRequester, so this check prevents the app from
        crashing if the user leaves the tab while the app is trying
        to get quests from the server
         */
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity == null){
            return;
        }
        try {
            Button btnRequestGeofences = (Button) view.findViewById(R.id.btn_request_geofences);
            TextView txtRequestGeofences = (TextView) view.findViewById(txt_try_getting_geofences);
            Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : handleNewGeofences");
            if (mainActivity != null) {
                Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : mainActivity not null");
                if (geofencesContent != null) {
                    Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : geofencesContent not null");
                    btnRequestGeofences.setVisibility(View.GONE);
                    txtRequestGeofences.setVisibility(View.GONE);
                    mainActivity.getGeofenceMonitor().handleNewGeofences(geofencesContent);
                    drawGeofences(geofencesContent);
                } else if (mainActivity.getGeofenceMonitor().allGeopointsByName.size() == 0 || geofencesContent == null){
                    btnRequestGeofences.setVisibility(View.VISIBLE);
                    txtRequestGeofences.setText(getResources().getString(R.string.no_geofences_retrieved));
                    txtRequestGeofences.setVisibility(View.VISIBLE);
                }
            }
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    /**
     * When geofences change, queries database for information about geofences
     * @param currentGeofences
     */
    @Override
    public void handleGeofenceChange(ArrayList<GeofenceObjectContent> currentGeofences) {
        super.handleGeofenceChange(currentGeofences);
        MainActivity mainActivity = (MainActivity) getActivity();

        for (int i = 0; i<currentGeofenceMarkers.size(); i++){
            boolean removeMarker = true;
            for(int j =0; j<currentGeofences.size(); j++){
                if(currentGeofenceMarkers.get(i).getTitle().equals(currentGeofences.get(j).getName())){
                    removeMarker = false;
                }
            }
            if(removeMarker){
                currentGeofenceMarkers.get(i).remove();
                currentGeofencesInfoMap.remove(currentGeofencesInfoMap.get(currentGeofenceMarkers.get(i).getTitle()));
                currentGeofenceMarkers.remove(i);
            }
        }

        this.currentGeofences = currentGeofences;
        if(mainActivity != null && currentGeofences != null) {
            if (mainActivity.isConnectedToNetwork()) {
                ArrayList<GeofenceObjectContent> singleGeofence = new ArrayList<>(1);
                for(int i = 0; i<currentGeofences.size(); i++){
                    if(!currentGeofencesInfoMap.containsKey(currentGeofences.get(i).getName()) && !geofenceNamesBeingQueriedForInfo.containsKey(currentGeofences.get(i).getName())){
                        singleGeofence.clear();
                        singleGeofence.add(currentGeofences.get(i));
                        geofenceNamesBeingQueriedForInfo.put(currentGeofences.get(i).getName(), 0);
                        Log.i(logMessages.VOLLEY, "handleGeofenceChange : about to query database : " + singleGeofence.toString());
                        volleyRequester.request(this, singleGeofence);
                    }

                }
            }

        }
    }

    /**
     * Gets new geofences
     * and draws markers on the map
     */
    public void updateGeofences() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if(view != null) {
            Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : updateGeofences : view is not null ");

            Button btnRequestGeofences = (Button) view.findViewById(R.id.btn_request_geofences);
            TextView txtRequestGeofences = (TextView) view.findViewById(txt_try_getting_geofences);

            Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : updateGeofences : about to get new geofences ");
            boolean gotGeofences = mainActivity.getGeofenceMonitor().getNewGeofences();
            if (!gotGeofences) {
                btnRequestGeofences.setVisibility(View.VISIBLE);
                txtRequestGeofences.setText(getResources().getString(R.string.no_geofences_retrieved));
            }

            if (mainActivity.getGeofenceMonitor().allGeopointsByName.size() == 0) {
                gotGeofences = mainActivity.getGeofenceMonitor().getNewGeofences();
                if (!gotGeofences) {
                    btnRequestGeofences.setVisibility(View.VISIBLE);
                    txtRequestGeofences.setText(getResources().getString(R.string.no_geofences_retrieved));
                }else{
                    btnRequestGeofences.setVisibility(View.GONE);
                    txtRequestGeofences.setVisibility(View.GONE);
                }
            } else {
                if (txtRequestGeofences != null) {
                    txtRequestGeofences.setVisibility(View.GONE);
                    btnRequestGeofences.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Shows a popover to display nearby memories
     */
    private void showMemoriesPopover(){

        RelativeLayout relLayoutTutorial = (RelativeLayout) view.findViewById(R.id.tutorial);
        relLayoutTutorial.setVisibility(View.GONE);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        RecyclerViewPopoverFragment recyclerViewPopoverFragment = RecyclerViewPopoverFragment.newInstance(this);

        // Transaction start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom,
                R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fragmentTransaction.add(R.id.fragment_container, recyclerViewPopoverFragment, "RecyclerViewPopoverFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * shows a popover for user to add a memory
     */
    public void showAddMemoriesPopover(){
        Log.i(logMessages.MEMORY_MONITORING, "HistoryFragment : showAddMemoriesPopover called");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        AddMemoryFragment addMemoryFragment = AddMemoryFragment.newInstance();

        // Transaction start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom,
                R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
        fragmentTransaction.replace(R.id.fragment_container, addMemoryFragment, "AddMemoriesFragment");

        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
        currentGeofenceMarkers = null;

    }

    @Override
    public void onPause() {
        super.onPause();
        needToCallUpdateGeofences = true;
    }
}
