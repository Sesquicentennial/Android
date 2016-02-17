package carleton150.edu.carleton.carleton150.MainFragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Map;

import carleton150.edu.carleton.carleton150.Adapters.HistoryCardAdapter;
import carleton150.edu.carleton.carleton150.Adapters.MyInfoWindowAdapter;
import carleton150.edu.carleton.carleton150.ExtraFragments.AddMemoryFragment;
import carleton150.edu.carleton.carleton150.ExtraFragments.HistoryPopoverFragment;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
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
 * A simple {@link Fragment} subclass.
 *
 */
public class HistoryFragment extends MapMainFragment implements RecyclerViewClickListener {

    private MyInfoWindowAdapter myInfoWindowAdapter;
    private View view;
    private int screenWidth;

    ArrayList<GeofenceObjectContent> currentGeofences;


    ArrayList<Marker> currentGeofenceMarkers = new ArrayList<Marker>();
    private boolean debugMode = false;

    public HistoryFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("started!");
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInfoWindowAdapter = new MyInfoWindowAdapter(inflater);

        if (container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.fragment_history, container, false);
        TextView txt_lat = (TextView) view.findViewById(R.id.txt_lat);
        TextView txt_long = (TextView) view.findViewById(R.id.txt_long);
        TextView queryResult = (TextView) view.findViewById(R.id.txt_query_result);
        final TextView txtRequestGeofences = (TextView) view.findViewById(txt_try_getting_geofences);
        final Button btnRequestGeofences = (Button) view.findViewById(R.id.btn_request_geofences);
        final Button btnGetNearbyMemories = (Button) view.findViewById(R.id.btn_get_nearby_memories);

        btnGetNearbyMemories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemoriesPopover();
            }
        });


        //buildRecyclerViews();


        /*If geofences weren't retrieved (likely due to network error), shows button for user
        to try requesting geofences again. If it is clicked, calls fragmentInView() to get new
        geofences and draw the necessary map markers
         */
        btnRequestGeofences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInView();
                btnRequestGeofences.setVisibility(View.GONE);
                txtRequestGeofences.setText(getResources().getString(R.string.retrieving_geofences));
            }
        });


        MainActivity mainActivity = (MainActivity) getActivity();

        //starts the mainActivity monitoring geofences
        mainActivity.getGeofenceMonitor().startGeofenceMonitoring();

        //Button to transition to and from debug mode
        Button btnToggle = (Button) view.findViewById(R.id.btn_debug_toggle);
        monitorDebugToggle();

        //TODO: remove
        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded(); // For setting up the MapFragment
        }

        drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfoMap);
        return view;
    }

    /**
     * Monitors the debug toggle button to show geofence circles when toggled.
     * This is for testing purposes only.
     */
    private void monitorDebugToggle(){
        Button btnToggle = (Button) view.findViewById(R.id.btn_debug_toggle);
        final MainActivity mainActivity = (MainActivity) getActivity();
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugMode = !debugMode;
                if (!debugMode) {
                    if (mMap != null) {
                        mMap.clear();
                        drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfoMap);
                        drawTiles();
                    }
                } else {
                    try {
                        drawGeofences(mainActivity.getGeofenceMonitor().geofencesBeingMonitored);
                        drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfoMap);
                        drawTiles();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    /***** Sets up the map if it is possible to do so *****/
    public boolean setUpMapIfNeeded() {
        super.setUpMapIfNeeded();
        MainActivity mainActivity = (MainActivity) getActivity();

            if (mMap != null) {
                mMap.setInfoWindowAdapter(myInfoWindowAdapter);
                if(mainActivity.getGeofenceMonitor().curGeofenceInfoMap != null){
                    myInfoWindowAdapter.setCurrentGeopoints(mainActivity.getGeofenceMonitor().curGeofenceInfoMap);
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        marker.hideInfoWindow();

                        showPopup(getContentFromMarker(marker));
                    }
                });
                return true;
            } else {
                //TODO: display message saying unable to set up map
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
        //TODO: the move to my location button is behind the toolbar -- fix it
        mMap.setMyLocationEnabled(true);
    }


    /**
     * Lifecycle method overridden to set up the map and check for internet connectivity
     * when the fragment comes into focus
     */
    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded();
        }
        fragmentInView();
    }


    /**
     * Adds a marker to the map at the center of each geofence for all geofences
     * the user is currently in
     *
     * @param currentGeofences a GeofenceInfoObject Content[] of information about
     *                         each geofence user is currently in
     */
    private void drawGeofenceMapMarker
    (HashMap<String, GeofenceInfoContent[]> currentGeofences){

        MainActivity mainActivity = (MainActivity) getActivity();
        if(currentGeofences != null) {
            for (int i = 0; i < currentGeofenceMarkers.size(); i++) {
                currentGeofenceMarkers.get(i).remove();
            }
            currentGeofenceMarkers.clear();
            if (currentGeofences.size() == 0) {
                Log.i(logMessages.GEOFENCE_MONITORING, "drawGeofenceMapMarker : length of currentGeofences is 0");
            }else{
                Log.i(logMessages.GEOFENCE_MONITORING, "drawGeofenceMapMarker : length of currentGeofences is not 0");
            }

            for(Map.Entry<String, GeofenceInfoContent[]> e : currentGeofences.entrySet()){
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
        Log.i(logMessages.GEOFENCE_MONITORING, "drawGeofenceMapMarker : done drawing markers");
    }

    /**
     * Displays text stating which geofences the user is currently in
     */
    private void displayGeofenceInfo(){
        TextView locationView = (TextView) view.findViewById(R.id.txt_geopoint_info);

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
                mainActivity.getGeofenceMonitor().handleResult(result);
                if (result != null) {
                    TextView queryResult = (TextView) view.findViewById(R.id.txt_query_result);
                    try {
                        //Gives information to the infoWindowAdapter for displaying info windows
                        myInfoWindowAdapter.setCurrentGeopoints(mainActivity.getGeofenceMonitor().curGeofenceInfoMap);
                        // historyCardAdapter.updateGeofences(mainActivity.getGeofenceMonitor().curGeofenceInfoMap);
                        // historyCardAdapter.notifyDataSetChanged();

                        btnRequestInfo.setVisibility(View.GONE);
                        txtRequestGeofences.setVisibility(View.GONE);

                        //sets text to display current geofences
                        displayGeofenceInfo();
                        drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfoMap);

                        if (!debugMode) {
                            queryResult.setVisibility(View.GONE);
                        }
                        queryResult.setText(result.toString());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        queryResult.setText("the geofence request returned a null content array");
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
        TextView txt_lat = (TextView) view.findViewById(R.id.txt_lat);
        TextView txt_long = (TextView) view.findViewById(R.id.txt_long);
        if(mainActivity.getGeofenceMonitor().currentLocation != null) {
            txt_lat.setText("Latitude: " + mainActivity.getGeofenceMonitor().currentLocation.getLatitude());
            txt_long.setText("Longitude: " + mainActivity.getGeofenceMonitor().currentLocation.getLongitude());
            setUpMapIfNeeded();
        }
    }

    private GeofenceInfoContent[] getContentFromMarker(Marker marker){
        MainActivity mainActivity = (MainActivity) getActivity();
        return mainActivity.getGeofenceMonitor().curGeofenceInfoMap.get(marker.getTitle());
    }

    /**
     * Shows the history popover for a given marker on the map
     *
     * @param geofenceInfoObject
     */
    private void showPopup(GeofenceInfoContent[] geofenceInfoObject){

        GeofenceInfoContent[] sortedContent = sortByDate(geofenceInfoObject);



        FragmentManager fm = getActivity().getSupportFragmentManager();
        HistoryPopoverFragment historyPopoverFragment = HistoryPopoverFragment.newInstance(sortedContent);

        // Transaction start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom,
                R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fragmentTransaction.add(R.id.fragment_container, historyPopoverFragment, "HistoryPopoverFragment");
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
                    if (year1Int > year2Int) {
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
        try {
            Button btnRequestGeofences = (Button) view.findViewById(R.id.btn_request_geofences);
            TextView txtRequestGeofences = (TextView) view.findViewById(txt_try_getting_geofences);
            super.handleNewGeofences(geofencesContent);
            Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : handleNewGeofences");


            if (mainActivity != null) {
                Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : mainActivity not null");
                if (geofencesContent != null) {
                    Log.i(logMessages.GEOFENCE_MONITORING, "HistoryFragment : geofencesContent not null");
                    btnRequestGeofences.setVisibility(View.GONE);
                    txtRequestGeofences.setVisibility(View.GONE);
                    mainActivity.getGeofenceMonitor().handleNewGeofences(geofencesContent);
                    drawGeofences(geofencesContent);

                } else if (mainActivity.getGeofenceMonitor().allGeopointsByName.size() == 0){

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
        this.currentGeofences = currentGeofences;
        if(mainActivity != null) {
            if (mainActivity.isConnectedToNetwork()) {
                Log.i(logMessages.VOLLEY, "handleGeofenceChange : about to query database : " + currentGeofences.toString());
                volleyRequester.request(this, currentGeofences);
            }
        }
    }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getGeofenceMonitor().removeAllGeofences();
        Log.i("UI", "HistoryFragment : fragmentOutOfView");
    }

    /**
     * Called when the fragment becomes visible on the screen. Gets new geofences
     * and draws markers on the map
     */
    @Override
    public void fragmentInView() {
        super.fragmentInView();
        MainActivity mainActivity = (MainActivity) getActivity();
        if(view != null) {
            Button btnRequestGeofences = (Button) view.findViewById(R.id.btn_request_geofences);
            TextView txtRequestGeofences = (TextView) view.findViewById(txt_try_getting_geofences);
            if (mainActivity == null) {
                mainActivity = (MainActivity) getActivity();
            }
            Log.i("UI", "HistoryFragment : fragmentInView");

            if (mainActivity.getGeofenceMonitor().allGeopointsByName.size() == 0) {
                boolean gotGeofences = mainActivity.getGeofenceMonitor().getNewGeofences();
                if (!gotGeofences) {
                    btnRequestGeofences.setVisibility(View.VISIBLE);
                    txtRequestGeofences.setText(getResources().getString(R.string.no_geofences_retrieved));
                }
            } else {
                mainActivity.getGeofenceMonitor().startMonitoringGeofencesAfterPause();

                if (txtRequestGeofences != null) {
                    txtRequestGeofences.setVisibility(View.GONE);

                }
            }
        }

    }

    public void showTooltip(GeofenceInfoContent[] object){
        Marker marker = null;
        for(int i = 0; i<currentGeofenceMarkers.size(); i++){
            Marker curMarker = currentGeofenceMarkers.get(i);
            String name = null;
            for(int j =0; j < object.length; j++){
                if(name != null){
                    break;
                }else if(object[i].getName() != null){
                    name = object[i].getName();
                }
            }
            if(curMarker.getTitle().equals(name)){
                marker = curMarker;
            }
        }
       if(marker != null){
           marker.showInfoWindow();
       }
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
       // GeofenceInfoContent[] clickedContent = historyCardAdapter.getItemAtPosition(position);
       // showTooltip(clickedContent);
    }

    /**
     * Builds the views for the quests
     */
   /* private void buildiews(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        lstImages = (RecyclerView) view.findViewById(R.id.lst_images);
        historyCardLayoutManager = new LinearLayoutManager(getActivity());
        historyCardLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        lstImages.setLayoutManager(historyCardLayoutManager);

        historyCardAdapter = new HistoryCardAdapter(mainActivity.getGeofenceMonitor().curGeofenceInfoMap, this, screenWidth, getResources());
        lstImages.setAdapter(historyCardAdapter);

    }*/

    private void showMemoriesPopover(){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        HistoryPopoverFragment historyPopoverFragment = HistoryPopoverFragment.newInstance(this);

        // Transaction start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom,
                R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        fragmentTransaction.add(R.id.fragment_container, historyPopoverFragment, "HistoryPopoverFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

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
        myInfoWindowAdapter = null;
        currentGeofenceMarkers = null;

    }
}
