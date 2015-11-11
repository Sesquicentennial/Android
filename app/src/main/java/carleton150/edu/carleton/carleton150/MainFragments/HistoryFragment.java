package carleton150.edu.carleton.carleton150.MainFragments;


import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import carleton150.edu.carleton.carleton150.Adapters.MyInfoWindowAdapter;
import carleton150.edu.carleton.carleton150.DialogFragments.HistoryPopoverDialogFragment;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;
import carleton150.edu.carleton.carleton150.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends MainFragment{

    private MyInfoWindowAdapter myInfoWindowAdapter;
    private static View view;
    private ArrayList<Content> curGeofences;
    private HashMap<String, Content> curGeofencesMap = new HashMap<>();
    private carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content[] curGeofenceInfo;
    private HashMap<String, carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content>
            curGeofencesInfoMap = new HashMap<>();
    private boolean zoomCamera = true;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location currentLocation = null;
    private static LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);
    private TextView txt_lat;
    private TextView txt_long;
    ArrayList<Marker> currentGeofenceMarkers = new ArrayList<Marker>();
    private MainActivity mainActivity;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity= (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myInfoWindowAdapter = new MyInfoWindowAdapter(inflater);

        if (container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.fragment_history, container, false);
        txt_lat = (TextView) view.findViewById(R.id.txt_lat);
        txt_long = (TextView) view.findViewById(R.id.txt_long);

        //TODO: probably won't end up using this. Figure out if we need it
        /*drawer = (SlidingDrawer) view.findViewById(R.id.slidingDrawer1);
        drawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                showDrawer();
            }
        });*/
        //btnShowDrawer = (Button) view.findViewById(R.id.handle);

        mainActivity = (MainActivity) getActivity();

        if(isConnectedToNetwork()) {
            setUpMapIfNeeded(); // For setting up the MapFragment
        } else {
            mainActivity.showNetworkNotConnectedDialog();
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
        mMap.setMyLocationEnabled(true);

        //Makes it so user can't zoom out very far
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                setCamera();
                if (cameraPosition.zoom <= 13) {
                    if (cameraPosition.target == null) {
                        setCamera();
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                }

                //makes it so user can't scroll too far off campus
                double latitude = cameraPosition.target.latitude;
                double longitude = cameraPosition.target.longitude;
                if (cameraPosition.target.longitude > -93.141134) {
                    longitude = -93.141134;
                }
                if (cameraPosition.target.longitude < -93.161333) {
                    longitude = -93.161333;
                }
                if (cameraPosition.target.latitude > 44.488045) {
                    latitude = 44.488045;
                }
                if (cameraPosition.target.latitude < 44.458869) {
                    latitude = 44.458869;
                }


                CameraPosition newCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))
                        .zoom(cameraPosition.zoom)
                        .bearing(cameraPosition.bearing)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
                //setCamera();

        }
    });
        //addCampusOverlays();
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
     * Testing method to make sure I am getting the geofences to monitor from the server.
     * Draws a circle for each geofence at the location of that geofence on the map
     * @param geofences
     */
    private void drawGeofenceCircles(Content[] geofences){
        mMap.clear();
        for(int i = 0; i<geofences.length; i++){
            carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Geofence geofence = geofences[i].getGeofence();
            CircleOptions circleOptions = new CircleOptions();
            carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Location location = geofence.getLocation();
            double lat = location.getLat();
            double lon = location.getLng();
            circleOptions.center(new LatLng(lat, lon));
            circleOptions.radius(geofence.getRadius());
            circleOptions.strokeColor(R.color.colorPrimary);
            circleOptions.strokeWidth(5);
            mMap.addCircle(circleOptions);
        }
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
     * Checks for internet connectivity
     * @return true if connected, false otherwise
     */
    public boolean isConnectedToNetwork(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Lifecycle method overridden to set up the map and check for internet connectivity
     * when the fragment comes into focus
     */
    @Override
    public void onResume() {
        super.onResume();
        if(isConnectedToNetwork()) {
            setUpMapIfNeeded();
        } else {
            mainActivity.showNetworkNotConnectedDialog();
        }
    }

    /**
     * Called when the geofences currently triggered by the user change.
     * Sets curGeofences and curGeofencesMap to contain the current geofences
     * and then queries the database for information about those geofences.
     *
     * @param currentGeofences
     */
    @Override
    public void handleGeofenceChange(ArrayList<Content> currentGeofences) {
        //TODO: add params here.
        Log.i("geofences changed", "content length: " + currentGeofences.size());

        curGeofences = currentGeofences;
        curGeofencesMap.clear();
        for(int i = 0; i< curGeofences.size(); i++){
            curGeofencesMap.put(curGeofences.get(i).getName(), curGeofences.get(i));
        }
        queryDatabase(currentGeofences);
    }

    /**
     * Deletes all entries in the curGeofencesInfoMap, then adds each current geofence
     * to the map where the key is the name of the geofence and the value is the info Content
     * of that geofence
     *
     * @param currentGeofences
     */
    private void makeGeofenceInfoMap
            (carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content[] currentGeofences){
        curGeofencesInfoMap.clear();
        if(currentGeofences.length == 0){
            Log.i("Geofence info: ", "length is 0");
        }
        for(int i = 0; i < currentGeofences.length; i++){
            String curGeofenceName = currentGeofences[i].getGeofences()[0];
            curGeofencesInfoMap.put(curGeofenceName, currentGeofences[i]);
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
        for(int i = 0; i<currentGeofenceMarkers.size(); i++){
            currentGeofenceMarkers.get(i).remove();
        }
        currentGeofenceMarkers.clear();
        if(currentGeofences.length == 0){
            Log.i("Geofence info: ", "length is 0");
        }
        for(int i = 0; i < currentGeofences.length; i++){
            carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content curGeofence = currentGeofences[i];
            String curGeofenceName = currentGeofences[i].getGeofences()[0];
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

        //TODO:TextView for testing that I'm recieving correct query. Remove for final version
       // TextView queryResult = (TextView) view.findViewById(R.id.txt_query_result);
       if (result == null) {
           //TODO: Do something here if there is an error (check error message, check internet, maybe make new query, etc..)
           //   queryResult.setText("Error with volley");
        } else {
            curGeofenceInfo = result.getContent();
            makeGeofenceInfoMap(curGeofenceInfo);

           //Gives information to the infoWindowAdapter for displaying info windows
            myInfoWindowAdapter.setCurrentGeopoints(curGeofenceInfo);
            myInfoWindowAdapter.setCurrentGeopointsMap(curGeofencesInfoMap);

           //sets text to display current geofences
            displayGeofenceInfo();
            drawGeofenceMapMarker(curGeofenceInfo);

           //TODO: this call is for testing
           // queryResult.setText(result.toString());
        }

    }

    /**
     * Updates map view to reflect user's new location
     *
     * @param newLocation
     */
    @Override
    public void handleLocationChange(Location newLocation) {
        setCamera();
        currentLocation = newLocation;
        txt_lat.setText("Latitude: " + currentLocation.getLatitude());
        txt_long.setText("Longitude: " + currentLocation.getLongitude());
        setUpMapIfNeeded();
       // addCurLocationMarker();
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
     * Shows the menu drawer with active geofences.
     *//*
    private void showDrawer(){
        ListView lstView = (ListView) view.findViewById(R.id.lst_cur_landmarks);
        String[] geofenceNames = new String[6];
        int[] geofenceImages = new int[6];
        geofenceNames[0] = "Geofence 1";
        geofenceNames[1] = "Geofence 2";
        geofenceNames[2] = "Geofence 3";
        geofenceNames[3] = "Geofence 4";
        geofenceNames[4] = "Geofence 5";
        geofenceNames[5] = "Geofence 6";
        geofenceImages[0] = R.drawable.carleton_logo;
        geofenceImages[1] = R.drawable.carleton_logo;
        geofenceImages[2] = R.drawable.carleton_logo;
        geofenceImages[3] = R.drawable.carleton_logo;
        geofenceImages[4] = R.drawable.carleton_logo;
        geofenceImages[5] = R.drawable.carleton_logo;

        lstView.setAdapter(new LandmarkListAdapter((MainActivity)getActivity(), this, geofenceNames, geofenceImages ));

    }

    public void closeDrawer(){
        drawer.animateClose();
    }*/


    /**
     * Testing function to draw circles on maps to show the geofences we are
     * currently monitoring. Helps to ensure that we are getting geofences from
     * server
     *
     * @param newGeofences
     */
    @Override
    public void handleNewGeofences(Content[] newGeofences) {

        //drawGeofenceCircles(newGeofences);
    }
}
