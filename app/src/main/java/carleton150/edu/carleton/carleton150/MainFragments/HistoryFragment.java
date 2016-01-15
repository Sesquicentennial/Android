package carleton150.edu.carleton.carleton150.MainFragments;


import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import carleton150.edu.carleton.carleton150.Adapters.MyInfoWindowAdapter;
import carleton150.edu.carleton.carleton150.DialogFragments.HistoryPopoverDialogFragment;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;
import carleton150.edu.carleton.carleton150.R;

/**
 * The main fragment for the History section of the app
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends GeofenceMonitorFragment implements ResultCallback<Status> {

    private double MAX_LONGITUDE = -93.141134;
    private double MIN_LONGITUDE = -93.161333;
    private double MAX_LATITUDE = 44.488045;
    private double MIN_LATITUDE = 44.458869;
    private static LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);

    private MainActivity mainActivity;
    private MyInfoWindowAdapter myInfoWindowAdapter;
    private static View view;

    private boolean zoomCamera = true;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    private TextView txt_lat;
    private TextView txt_long;
    private TextView queryResult;


    ArrayList<Marker> currentGeofenceMarkers = new ArrayList<Marker>();
    private boolean debugMode = false;


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
        startGeofenceMonitoring();


        //TODO: refactor section
        //Button to transition to and from debug mode
        Button btnToggle = (Button) view.findViewById(R.id.btn_debug_toggle);
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugMode = !debugMode;
                if (!debugMode) {
                    if (mMap != null) {
                        mMap.clear();
                        drawGeofenceMapMarker(curGeofenceInfo);
                        //queryResult.setVisibility(View.GONE);
                    }
                } else {
                    try {
                        drawGeofences(geofencesBeingMonitored);
                        drawGeofenceMapMarker(curGeofenceInfo);
                        //queryResult.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if(mainActivity.isConnectedToNetwork()) {
            setUpMapIfNeeded(); // For setting up the MapFragment
        }
        return view;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
                if(curGeofenceInfo != null){
                    myInfoWindowAdapter.setCurrentGeopoints(curGeofenceInfo);
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        marker.hideInfoWindow();
                        showPopup(marker);
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
    //TODO: figure out what to do if user is off campus and handle that appropriately
    private void setCamera(){
        if(currentLocation != null && zoomCamera) {
            zoomCamera = false;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .zoom(15)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }if(currentLocation == null){
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
            (carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content[] currentGeofences){
        if(currentGeofences != null) {
            for (int i = 0; i < currentGeofenceMarkers.size(); i++) {
                currentGeofenceMarkers.get(i).remove();
            }
            currentGeofenceMarkers.clear();
            if (currentGeofences.length == 0) {
                Log.i(logMessages.GEOFENCE_MONITORING, "drawGeofenceMapMarker : length of currentGeofences is 0");
            }
            for (int i = 0; i < currentGeofences.length; i++) {
                carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content curGeofence = currentGeofences[i];
                String curGeofenceName = currentGeofences[i].getName();
                Content geofence = curGeofencesMap.get(curGeofenceName);
                double lat = geofence.getGeofence().getLocation().getLat();
                double lon = geofence.getGeofence().getLocation().getLng();
                LatLng latLng = new LatLng(lat, lon);
                MarkerOptions geofenceMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(curGeofenceName);
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
        String displayString = "Currently in geofences for: ";
        boolean showString = false;
        if(curGeofences == null){
            return;
        }
        for(int i = 0; i<curGeofences.size(); i++){
            displayString += curGeofences.get(i).getName() + " ";
            showString = true;
        }
        if(showString) {
            locationView.setText(displayString);
        } else {
            locationView.setText("No items");
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
        if (result != null){
           try {
               //Gives information to the infoWindowAdapter for displaying info windows
               myInfoWindowAdapter.setCurrentGeopoints(curGeofenceInfo);
               myInfoWindowAdapter.setCurrentGeopointsMap(curGeofencesInfoMap);

               //sets text to display current geofences
               displayGeofenceInfo();
               drawGeofenceMapMarker(curGeofenceInfo);

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
        setCamera();

        txt_lat.setText("Latitude: " + currentLocation.getLatitude());
        txt_long.setText("Longitude: " + currentLocation.getLongitude());
        setUpMapIfNeeded();
    }

    /**
     * Shows the history popover for a given marker on the map
     *
     * @param marker
     */
    private void showPopup(Marker marker){

        carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content geofenceInfoObject
                = curGeofencesInfoMap.get(marker.getTitle());
        HistoryPopoverDialogFragment dialog = HistoryPopoverDialogFragment.newInstance(geofenceInfoObject);
        dialog.show(getFragmentManager(), marker.getTitle());
    }

    /**
     * Testing function to draw circles on maps to show the geofences we are
     * currently monitoring. Helps to ensure that we are getting geofences from
     * server
     *
     * @param geofences
     */

    public void drawGeofences(Content[] geofences) {
        geofencesBeingMonitored = geofences;
        if(debugMode) {
            if(geofences != null) {
                mMap.clear();
                for (int i = 0; i < geofences.length; i++) {
                    carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Geofence geofence =
                            geofences[i].getGeofence();
                    CircleOptions circleOptions = new CircleOptions();
                    carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Location location =
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
    public void handleNewGeofences(Content[] geofencesContent){
        super.handleNewGeofences(geofencesContent);
        if(geofencesContent != null) {
            drawGeofences(geofencesContent);
        }
    }


    /**
     * When geofences change, queries database for information about geofences
     * @param currentGeofences
     */
    @Override
    public void handleGeofenceChange(ArrayList<Content> currentGeofences) {
        super.handleGeofenceChange(currentGeofences);
        if(mainActivity.isConnectedToNetwork()) {
            Log.i(logMessages.VOLLEY, "handleGeofenceChange : about to query database : " + currentGeofences.toString());
            volleyRequester.request(this, currentGeofences);
        }
    }
}
