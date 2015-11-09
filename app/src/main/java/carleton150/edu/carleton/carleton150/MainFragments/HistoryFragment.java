package carleton150.edu.carleton.carleton150.MainFragments;


import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;

import carleton150.edu.carleton.carleton150.Adapters.MyInfoWindowAdapter;
import carleton150.edu.carleton.carleton150.ArrayAdapters.LandmarkListAdapter;
import carleton150.edu.carleton.carleton150.DialogFragments.HistoryPopoverDialogFragment;
import carleton150.edu.carleton.carleton150.GeoPoint;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.Models.DummyLocations;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String NO_NETWORK_ERROR = "No network connected. Please connect and try again";

    private OnFragmentInteractionListener mListener;
    private MyInfoWindowAdapter myInfoWindowAdapter;

    private static View view;

    private boolean gettingLocationUpdates = false;

    private ArrayList<GeoPoint> curGeofences;
    private HashMap<String, GeoPoint> curGeofencesMap = new HashMap<>();

    private Handler mHandler;

    private boolean zoomCamera = true;

    private double mDeviceHeight;
    private PopupWindow mPopupWindow;
    private Button btnShowPopup;
    private Button btnShowDrawer;
    private SlidingDrawer drawer;
    /**
     * Note that this may be null if the Google Play services APK is not
     * available.
     */


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Location currentLocation = null;

    private static LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);

    private TextView txt_lat;
    private TextView txt_long;
    private String TAG_NO_GPS = "noGPS";
    private String TAG_NO_NETWORK = "noNetwork";

    MarkerOptions curLocationMarkerOptions;
    Marker curLocationMarker = null;
    ArrayList<Marker> currentGeofenceMarkers = new ArrayList<Marker>();

    private MainActivity mainActivity;

    private LayoutInflater inflater;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity= (MainActivity) getActivity();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        myInfoWindowAdapter = new MyInfoWindowAdapter(inflater);

        if (container == null) {
            return null;
        }

        view = (RelativeLayout) inflater.inflate(R.layout.fragment_history, container, false);
        txt_lat = (TextView) view.findViewById(R.id.txt_lat);
        txt_long = (TextView) view.findViewById(R.id.txt_long);

        /*drawer = (SlidingDrawer) view.findViewById(R.id.slidingDrawer1);
        drawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                showDrawer();
            }
        });*/
        //btnShowDrawer = (Button) view.findViewById(R.id.handle);

        mainActivity = (MainActivity) getActivity();
        mHandler = new Handler();

        if(isConnectedToNetwork()) {
            setUpMapIfNeeded(); // For setting up the MapFragment
        } else {
            mainActivity.showNetworkNotConnectedDialog();
        }

        //TODO: remove this after testing
        queryDatabase();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                if(curGeofences != null){
                    myInfoWindowAdapter.setCurrentGeopoints(curGeofences);
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        //TODO: Show popover box
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
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
    private void setUpMap() {

        // For showing a move to my location button
        mMap.setMyLocationEnabled(true);

        //Makes it so user can't zoom out very far
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                setCamera();
                if(cameraPosition.zoom <= 13){
                    if(cameraPosition.target==null){
                       setCamera();
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                }
            }
        });
        //addCurLocationMarker();
        addCampusOverlays();
        setCamera();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (mMap != null)
            setUpMap();

        setUpMapIfNeeded();
    }




   private void addCurLocationMarker(){
        if (currentLocation != null){
            if(curLocationMarker!=null) {
                curLocationMarker.remove();
            }
            curLocationMarkerOptions = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title("Your Location");
            curLocationMarker = mMap.addMarker(curLocationMarkerOptions);
        }
    }

    private void addCampusOverlays(){
        DummyLocations dummyLocations = new DummyLocations();

        ArrayList<GeoPoint> geofenceCenters = dummyLocations.getCircleCenters();
        for(int i = 0; i<geofenceCenters.size(); i++){
            CircleOptions myCircleOptions = new CircleOptions();
            myCircleOptions.center(geofenceCenters.get(i).getLatLng());
            myCircleOptions.radius(geofenceCenters.get(i).getSmallRadius());
            myCircleOptions.fillColor(R.color.colorPrimarySemiTransparent);
            myCircleOptions.strokeColor(R.color.yellowAccent);
            myCircleOptions.strokeWidth(5);
            mMap.addCircle(myCircleOptions);
        }
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public boolean isConnectedToNetwork(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isConnectedToNetwork()) {
            setUpMapIfNeeded();
        } else {
            mainActivity.showNetworkNotConnectedDialog();
        }
    }

    @Override
    public void handleGeofenceChange(ArrayList<GeoPoint> currentGeofences) {
        //TODO: add params here.
        queryDatabase();
        curGeofences = currentGeofences;
        //TODO: remove this once there is a reliable server and we are parsing JSONs
        drawGeofenceMapMarker(curGeofences);
        displayGeofenceInfo();

    }

    private void drawGeofenceMapMarker(ArrayList<GeoPoint> currentGeofences){
        for(int i = 0; i<currentGeofenceMarkers.size(); i++){
            currentGeofenceMarkers.get(i).remove();
        }
        currentGeofenceMarkers.clear();
        curGeofencesMap.clear();
        for(int i =0; i<currentGeofences.size(); i++){
            GeoPoint curGeofence = currentGeofences.get(i);
            MarkerOptions geofenceMarkerOptions = new MarkerOptions()
                    .position(curGeofence.getLatLng())
                    .title(curGeofence.getName());
            Marker curGeofenceMarker = mMap.addMarker(geofenceMarkerOptions);
            curGeofencesMap.put(currentGeofences.get(i).getName(), currentGeofences.get(i));
            currentGeofenceMarkers.add(curGeofenceMarker);
        }
    }


    private void displayGeofenceInfo(){

        TextView locationView = (TextView) view.findViewById(R.id.txt_geopoint_info);
        String displayString = "Currently in geofences for: ";
        boolean showString = false;
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



    @Override
    public void handleResult(JSONObject result) {
        TextView queryResult = (TextView) view.findViewById(R.id.txt_query_result);
        if (result == null) {
            queryResult.setText("Error with volley");
        } else {
            drawGeofenceMapMarker(curGeofences);
            //TODO: Parse object, also myInfoWindowAdapter.setCurrentGeopoints(The Result of the JsonParse)
            queryResult.setText(result.toString());
        }

    }

    @Override
    public void handleLocationChange(Location newLocation) {
        setCamera();
        currentLocation = newLocation;
        txt_lat.setText("Latitude: " + currentLocation.getLatitude());
        txt_long.setText("Longitude: " + currentLocation.getLongitude());
        setUpMapIfNeeded();
        addCurLocationMarker();
    }

    public void showHistoryPopover(){
        HistoryPopoverDialogFragment dialog = HistoryPopoverDialogFragment.newInstance();
        dialog.show(getFragmentManager(), "historyDialog");
    }

    /**
     * Shows the menu drawer with active geofences
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







}
