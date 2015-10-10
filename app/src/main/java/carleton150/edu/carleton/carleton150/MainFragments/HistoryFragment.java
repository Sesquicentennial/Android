package carleton150.edu.carleton.carleton150.MainFragments;


import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

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
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static View view;


    private Handler mHandler;

    private boolean zoomCamera = true;
    /**
     * Note that this may be null if the Google Play services APK is not
     * available.
     */


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Location currentLocation = null;

    private TextView txt_lat;
    private TextView txt_long;
    private String TAG_NO_GPS = "noGPS";
    private String TAG_NO_NETWORK = "noNetwork";

    MarkerOptions curLocationMarkerOptions;
    Marker curLocationMarker = null;

    private MainActivity mainActivity;


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


        if (container == null) {
            return null;
        }





        view = (RelativeLayout) inflater.inflate(R.layout.fragment_history, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        txt_lat = (TextView) view.findViewById(R.id.txt_lat);
        txt_long = (TextView) view.findViewById(R.id.txt_long);

        mainActivity = (MainActivity) getActivity();
        mHandler = new Handler();
        getLocationUpdates();


        setUpMapIfNeeded(); // For setting up the MapFragment

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
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
    private void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);

        //addCurLocationMarker();
        addCampusOverlays();
        setCamera();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.location_map)).getMap(); // getMap is deprecated
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    public void getLocationUpdates(){
        mStatusChecker.run();
    }


    Runnable mStatusChecker = new Runnable() {
        private int mInterval = 1000;

        @Override
        public void run() {
            currentLocation = mainActivity.getMLastLocation();
            if(currentLocation != null) {
                setCamera();
                txt_lat.setText("Latitude: " + currentLocation.getLatitude());
                txt_long.setText("Longitude: " + currentLocation.getLongitude());
                setUpMapIfNeeded();
                //addCurLocationMarker();
            }
            else{
                Log.i("location info", "current location is null");
            }
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void stopLocationUpdates() {
        mHandler.removeCallbacks(mStatusChecker);
    }

   /* private void addCurLocationMarker(){
        if (currentLocation != null){
            if(curLocationMarker!=null) {
                curLocationMarker.remove();
            }
            curLocationMarkerOptions = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .title("Your Location");
            curLocationMarker = mMap.addMarker(curLocationMarkerOptions);
        }
    }*/

    private void addCampusOverlays(){
        DummyLocations dummyLocations = new DummyLocations();
        List<List> locations = dummyLocations.getLocationsArray();
        for(int i = 0; i<locations.size(); i++){
            PolygonOptions myPolygon = new PolygonOptions();
            for(int j = 0; j<locations.get(i).size(); j++){
                LatLng point = (LatLng)locations.get(i).get(j);
                myPolygon.add(point);
            }
            myPolygon.fillColor(getResources().getColor(R.color.colorAccent))
                    .strokeWidth(4).strokeColor(R.color.colorPrimary);
            mMap.addPolygon(myPolygon);
        }

        CircleOptions myCircleOptions = new CircleOptions();
        myCircleOptions.center(new LatLng(44.45997433, -93.15474433));
        myCircleOptions.radius(10.0);
        myCircleOptions.fillColor(R.color.colorAccent);
        myCircleOptions.strokeColor(R.color.colorPrimary);
        myCircleOptions.strokeWidth(3);
        mMap.addCircle(myCircleOptions);
    }

    private void setCamera(){
        if(currentLocation != null && zoomCamera) {
            zoomCamera = false;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .zoom(13)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onDestroyView() {
        stopLocationUpdates();
        super.onDestroyView();
    }
}
