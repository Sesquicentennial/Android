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
import android.util.DisplayMetrics;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.Adapters.HistoryCardAdapter;
import carleton150.edu.carleton.carleton150.Adapters.MyInfoWindowAdapter;
import carleton150.edu.carleton.carleton150.DialogFragments.HistoryPopoverFragment;
import carleton150.edu.carleton.carleton150.Interfaces.RecyclerViewClickListener;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectLocation;
import carleton150.edu.carleton.carleton150.R;

/**
 * The main fragment for the History section of the app
 *
 * A simple {@link Fragment} subclass.
 *
 */
public class HistoryFragment extends MainFragment implements RecyclerViewClickListener {

    private double MAX_LONGITUDE = -93.141134;
    private double MIN_LONGITUDE = -93.161333;
    private double MAX_LATITUDE = 44.488045;
    private double MIN_LATITUDE = 44.458869;
    private static LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);

    private MainActivity mainActivity;
    private MyInfoWindowAdapter myInfoWindowAdapter;
    private static View view;
    private int screenWidth;
    private RecyclerView lstImages;
    private HistoryCardAdapter historyCardAdapter;
    private LinearLayoutManager historyCardLayoutManager;

    private boolean zoomCamera = true;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    private TextView txt_lat;
    private TextView txt_long;
    private TextView queryResult;
    private TextView txtRequestGeofences;
    private Button btnRequestGeofences;

    ArrayList<Marker> currentGeofenceMarkers = new ArrayList<Marker>();
    private boolean debugMode = false;
    private Button btnToggle;


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInfoWindowAdapter = new MyInfoWindowAdapter(inflater);
        mainActivity = (MainActivity) getActivity();

        if (container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.fragment_history, container, false);
        txt_lat = (TextView) view.findViewById(R.id.txt_lat);
        txt_long = (TextView) view.findViewById(R.id.txt_long);
        queryResult = (TextView) view.findViewById(R.id.txt_query_result);
        txtRequestGeofences = (TextView) view.findViewById(R.id.txt_try_getting_geofences);
        btnRequestGeofences = (Button) view.findViewById(R.id.btn_request_geofences);

        buildRecyclerViews();
        /*If geofences weren't retrieved (likely due to network error), sets button for user
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

        //starts the mainActivity monitoring geofences
        mainActivity.getGeofenceMonitor().startGeofenceMonitoring();

        //Button to transition to and from debug mode
        btnToggle = (Button) view.findViewById(R.id.btn_debug_toggle);
        monitorDebugToggle();

        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded(); // For setting up the MapFragment
        }
        return view;
    }

    /**
     * Monitors the debug toggle button to show geofence circles when toggled.
     * This is for testing purposes only.
     */
    private void monitorDebugToggle(){
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugMode = !debugMode;
                if (!debugMode) {
                    if (mMap != null) {
                        mMap.clear();
                        drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfo);
                    }
                } else {
                    try {
                        drawGeofences(mainActivity.getGeofenceMonitor().geofencesBeingMonitored);
                        drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    /***** Sets up the map if it is possible to do so *****/
    public boolean setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setInfoWindowAdapter(myInfoWindowAdapter);
                if(mainActivity.getGeofenceMonitor().curGeofenceInfo != null){
                    myInfoWindowAdapter.setCurrentGeopoints(mainActivity.getGeofenceMonitor().curGeofenceInfo);
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        marker.hideInfoWindow();

                        showPopup(getContentFromMarker(marker));
                    }
                });
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
                if (cameraPosition.target.longitude > MAX_LONGITUDE) {
                    longitude = MAX_LONGITUDE;
                }
                if (cameraPosition.target.longitude < MIN_LONGITUDE) {
                    longitude = MIN_LONGITUDE;
                }
                if (cameraPosition.target.latitude > MAX_LATITUDE) {
                    latitude = MAX_LATITUDE;
                }
                if (cameraPosition.target.latitude < MIN_LATITUDE) {
                    latitude = MIN_LATITUDE;
                }

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
     * Adds a marker to the map at the center of each geofence for all geofences
     * the user is currently in
     *
     * @param currentGeofences a GeofenceInfoObject Content[] of information about
     *                         each geofence user is currently in
     */
    private void drawGeofenceMapMarker
    (GeofenceInfoContent[] currentGeofences){
        if(currentGeofences != null) {
            for (int i = 0; i < currentGeofenceMarkers.size(); i++) {
                currentGeofenceMarkers.get(i).remove();
            }
            currentGeofenceMarkers.clear();
            if (currentGeofences.length == 0) {
                Log.i(logMessages.GEOFENCE_MONITORING, "drawGeofenceMapMarker : length of currentGeofences is 0");
            }else{
                Log.i(logMessages.GEOFENCE_MONITORING, "drawGeofenceMapMarker : length of currentGeofences is not 0");
            }
            for (int i = 0; i < currentGeofences.length; i++) {

                String curGeofenceName = currentGeofences[i].getName();
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
     * Displays text stating which geofences the user is currently in
     */
    private void displayGeofenceInfo(){
        TextView locationView = (TextView) view.findViewById(R.id.txt_geopoint_info);
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
        if(this.isDetached()){
            return;
        }

        mainActivity.getGeofenceMonitor().handleResult(result);
        if (result != null){
            try {
                //Gives information to the infoWindowAdapter for displaying info windows
                myInfoWindowAdapter.setCurrentGeopoints(mainActivity.getGeofenceMonitor().curGeofenceInfo);
                myInfoWindowAdapter.setCurrentGeopointsMap(mainActivity.getGeofenceMonitor().curGeofencesInfoMap);
                historyCardAdapter.updateGeofences(mainActivity.getGeofenceMonitor().curGeofenceInfo);
                historyCardAdapter.notifyDataSetChanged();

                //sets text to display current geofences
                displayGeofenceInfo();
                drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfo);

                if (!debugMode) {
                    queryResult.setVisibility(View.GONE);
                }
                queryResult.setText(result.toString());
            }catch (NullPointerException e){
                e.printStackTrace();
                queryResult.setText("the geofence request returned a null content array");
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
        mainActivity.getGeofenceMonitor().handleLocationChange(newLocation);
        setCamera();

        if(mainActivity.getGeofenceMonitor().currentLocation != null) {
            txt_lat.setText("Latitude: " + mainActivity.getGeofenceMonitor().currentLocation.getLatitude());
            txt_long.setText("Longitude: " + mainActivity.getGeofenceMonitor().currentLocation.getLongitude());
            setUpMapIfNeeded();
        }
    }

    private GeofenceInfoContent getContentFromMarker(Marker marker){
        return mainActivity.getGeofenceMonitor().curGeofencesInfoMap.get(marker.getTitle());
    }

    /**
     * Shows the history popover for a given marker on the map
     *
     * @param geofenceInfoObject
     */
    private void showPopup(GeofenceInfoContent geofenceInfoObject){

        FragmentManager fm = getFragmentManager();
        HistoryPopoverFragment historyPopoverFragment = HistoryPopoverFragment.newInstance(geofenceInfoObject);

        // Transaction start
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        //fragmentTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
        fragmentTransaction.add(R.id.fragment_container, historyPopoverFragment,"HistoryPopoverFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Testing function to draw circles on maps to show the geofences we are
     * currently monitoring. Helps to ensure that we are getting geofences from
     * server
     *
     * @param geofences
     */

    public void drawGeofences(GeofenceObjectContent[] geofences) {
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
        super.handleNewGeofences(geofencesContent);
        /*This is a call from the VolleyRequester, so this check prevents the app from
        crashing if the user leaves the tab while the app is trying
        to get quests from the server
         */
        if(this.isDetached()){
            return;
        }

        if(mainActivity != null) {
            if(geofencesContent != null) {
                btnRequestGeofences.setVisibility(View.GONE);
                txtRequestGeofences.setVisibility(View.GONE);
                mainActivity.getGeofenceMonitor().handleNewGeofences(geofencesContent);
                drawGeofences(geofencesContent);

            }else{
                if(mainActivity.getGeofenceMonitor().allGeopointsByName.size() == 0){
                    btnRequestGeofences.setVisibility(View.VISIBLE);
                    txtRequestGeofences.setText(getResources().getString(R.string.no_geofences_retrieved));
                }
            }
        }
    }


    /**
     * When geofences change, queries database for information about geofences
     * @param currentGeofences
     */
    @Override
    public void handleGeofenceChange(ArrayList<GeofenceObjectContent> currentGeofences) {
        super.handleGeofenceChange(currentGeofences);
        if(mainActivity.isConnectedToNetwork()) {
            Log.i(logMessages.VOLLEY, "handleGeofenceChange : about to query database : " + currentGeofences.toString());
            volleyRequester.request(this, currentGeofences);
        }
    }

    @Override
    public void fragmentOutOfView() {
        super.fragmentOutOfView();
    }

    /**
     * Called when the fragment becomes visible on the screen. Gets new geofences
     * and draws markers on the map
     */
    @Override
    public void fragmentInView() {
        super.fragmentInView();
        mainActivity.getGeofenceMonitor().getNewGeofences();
        drawGeofenceMapMarker(mainActivity.getGeofenceMonitor().curGeofenceInfo);
    }

    public void showTooltip(GeofenceInfoContent object){
        Marker marker = null;
        for(int i = 0; i<currentGeofenceMarkers.size(); i++){
            Marker curMarker = currentGeofenceMarkers.get(i);
            if(curMarker.getTitle().equals(object.getName())){
                marker = curMarker;
            }
        }
       if(marker != null){
           marker.showInfoWindow();
       }
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        GeofenceInfoContent clickedContent = historyCardAdapter.getItemAtPosition(position);
        showTooltip(clickedContent);
    }

    /**
     * Builds the views for the quests
     */
    private void buildRecyclerViews(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        lstImages = (RecyclerView) view.findViewById(R.id.lst_images);
        historyCardLayoutManager = new LinearLayoutManager(getActivity());
        historyCardLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        lstImages.setLayoutManager(historyCardLayoutManager);
        historyCardAdapter = new HistoryCardAdapter(mainActivity.getGeofenceMonitor().curGeofenceInfo, this, screenWidth, getResources());
        lstImages.setAdapter(historyCardAdapter);
    }
}
