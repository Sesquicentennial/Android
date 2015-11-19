package carleton150.edu.carleton.carleton150;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.HashMap;

import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;
import carleton150.edu.carleton.carleton150.MainFragments.MyFragmentPagerAdapter;
import carleton150.edu.carleton.carleton150.Models.GeofenceErrorMessages;
import carleton150.edu.carleton.carleton150.Models.VolleyRequester;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObject;

/**
 * Monitors location and geofence information and calls methods in the main view fragments
 * to handle geofence and location changes. Also controls which fragment is in view.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

    //things for managing fragments
    public static FragmentManager fragmentManager;

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    //things for location
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    //last location where we requested new geofences
    private Location lastGeofenceUpdateLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    // Location updates intervals in milliseconds
    private static int UPDATE_INTERVAL = 30000; // 30 sec
    private static int FASTEST_INTERVAL = 10000; // 10 sec
    private static int DISPLACEMENT = 10; // 10 meters

    //things for detecting geofence entry
    protected ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded = false;
    private PendingIntent mGeofencePendingIntent;
    private MyFragmentPagerAdapter adapter;
    private ArrayList<Content> curGeofences = new ArrayList<Content>();
    private HashMap<String, Content> allGeopointsByName = new HashMap<String, Content>();
    private VolleyRequester mVolleyRequester = new VolleyRequester();

    AlertDialog networkAlertDialog;
    AlertDialog playServicesConnectivityAlertDialog;

    boolean DEBUG_MODE = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkAlertDialog = new AlertDialog.Builder(MainActivity.this).create();
        playServicesConnectivityAlertDialog = new AlertDialog.Builder(MainActivity.this).create();
            // check availability of play services for location data and geofencing
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                if(isConnectedToNetwork()) {
                    mGoogleApiClient.connect();
                }
            } else {
                showGooglePlayServicesUnavailableDialog();
            }


        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
        //populateGeofenceList();

        //managing fragments and UI
        fragmentManager = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_action_history));
        tabLayout.addTab(tabLayout.newTab().setText("Social"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyFragmentPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                handleGeofenceChange();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Registers a broadcastReceiver to receive broadcasts when a geofence
        //is entered or exited (broadcast is sent from GeofencingTransitionsIntentService
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GeofenceInfo"));
    }

    //
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String[] geofenceNames = intent.getStringArrayExtra("geofenceNames");
            int transitionType = intent.getIntExtra("transitionType", -1);
            //updates curGeofences by deleting geofences that were exited and adding geofences
            //that were entered
            updateCurrentGeofences(geofenceNames, transitionType);
            //calls a method in the current fragment that handles the change in geofences
            handleGeofenceChange();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //displays dialog if network isn't connected
        isConnectedToNetwork();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            if(mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
        else{
            if(isConnectedToNetwork()){
                // check availability of play services for location data and geofencing
                if (checkPlayServices()) {
                    mGoogleApiClient.connect();
                } else {
                    showGooglePlayServicesUnavailableDialog();
                }
            }
        }

    }


    /**
     * Method that is called when google API Client is connected
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {

        // Once connected with google api, get the location
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        //starts periodic location updates
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        //TODO:remove after testing
        //removeAllGeofences();

        //TODO: remove this when we are getting geofences from server
        //addGeofences();

        //gets new geofences from the server
        getNewGeofences();
        //Sets the last geofence update location since we just retrieved geofences
        lastGeofenceUpdateLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /**
     * If google api client connection was suspended, keeps trying to connect
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    /**
     * Displays an alert dialog if unable to connect to the GoogleApiClient
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showAlertDialog("Connection to play services failed with message: " +
                connectionResult.getErrorMessage() + "\nCode: " + connectionResult.getErrorCode(),
                playServicesConnectivityAlertDialog);
    }

    /**
     * Builds a GoogleApiClient
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }
    

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        tellFragmentLocationChanged();

        if(mLastLocation.distanceTo(lastGeofenceUpdateLocation) > 1000){
            lastGeofenceUpdateLocation = mLastLocation;
            getNewGeofences();
        }
    }

    /**
     * Calls a method in the current fragment to handle a location change.
     * The contents of handleLocationChange() varies depending on the fragment
     */
    private void tellFragmentLocationChanged(){
        MainFragment curFragment = adapter.getCurFragment();
        if(curFragment != null) {
            curFragment.handleLocationChange(mLastLocation);
        }
    }

    /**
     * Requests geofences from server using VolleyRequester
     */
    private void getNewGeofences(){
        mVolleyRequester.requestGeofences(mLastLocation.getLatitude(), mLastLocation.getLongitude(), this);
    }


    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 1 meter
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        if (mGoogleApiClient.isConnected()) {
            if(mRequestingLocationUpdates) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected()) {
            Log.i("Location info g:", "location updates stopped");
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    /**
     * gets most recent location stored on device
     * @return
     */
    public Location getMLastLocation() {
        return mLastLocation;
    }



    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies the initial trigger (INITIAL_TRIGGER_ENTER means that if you are currently in
     * the geofence when app is launched, it triggers an enter notification).
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);
        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // This pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * The activity implements ResultCallback, so this is a required method
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {

            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;

            if(DEBUG_MODE) {
                //TODO: This is just for testing. Remove it later
                Toast.makeText(
                        this,
                        getString(mGeofencesAdded ? R.string.geofences_added :
                                R.string.geofences_removed),
                        Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            // Get the status code and log it.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofencingTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    /**
     * Gets hard-coded geofences from DummyLocations
     * TODO: This method should be deleted or re-adapted when we get geofences from server
     *//*
    public void populateGeofenceList() {
        DummyLocations dummyLocations = new DummyLocations();
        ArrayList<GeoPoint> centerPoints = dummyLocations.getCircleCenters();

        for(int i = 0; i<centerPoints.size(); i++) {
            allGeopointsByName.put(centerPoints.get(i).getName(), centerPoints.get(i));
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(centerPoints.get(i).getName())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            centerPoints.get(i).getLatLng().latitude,
                            centerPoints.get(i).getLatLng().longitude,
                            centerPoints.get(i).getSmallRadius()
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                            //.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                    .build());
        }
    }*/

    /**
     * Tells google play services api that we don't want to listen for the geofences anymore.
     * //TODO: Do we need a check here to make sure they deleted before adding the next ones?
     */
    private void removeAllGeofences(){
        mGeofencesAdded = false;
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    }

    /**
     * Calls a method in the current fragment to handle a geofence change.
     * The contents of handleGeofenceChange() varies depending on the fragment
     */
    private void handleGeofenceChange(){
        MainFragment curFragment = adapter.getCurFragment();
        Log.i("geofence debugging: ", "handleGeofenceChange mainActivity: " + curGeofences.size());
        curFragment.handleGeofenceChange(curGeofences);
    }

    //TODO: remove this after testing
    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "entered geofence!";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exited geofence!";

            default:
                return "unknown geofence transition";
        }
    }

    /**
     * Updates curGeofences given a String[] of geofence names and a transition type.
     * If the transition type was enter, sets curGeofences to be the geofences that
     * were entered. If transition type was exit, removes the geofences that were
     * exited from curGeofences
     *
     * @param geofenceNames names of geofences that were triggered
     * @param transitionType type of transition (enter, exit, or dwell)
     */
    private void updateCurrentGeofences(String[] geofenceNames, int transitionType){
        Log.i("geofence debugging: ", "updatecurrentgeofences mainactivity: " + geofenceNames.length);
        Log.i("geofence debugging: ", "updatecurrentgeofences mainactivity: transition type" + transitionType);
        if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
            Log.i("geofence debugging: ", "updatecurrentgeofences mainactivity: transition type enter");
            //curGeofences.clear();

        }
        for(int i = 0; i<geofenceNames.length; i++){
            if(allGeopointsByName.containsKey(geofenceNames[i])){
                if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
                    curGeofences.add(allGeopointsByName.get(geofenceNames[i]));
                }else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
                    if(curGeofences.contains(allGeopointsByName.get(geofenceNames[i]))){
                        curGeofences.remove(allGeopointsByName.get(geofenceNames[i]));
                    }else{
                        //Shouldn't ever happen...
                    }
                }
            }
        }
        Log.i("geofence debugging: ", "updatecurrentgeofences mainactivity curGeofencesLength: " + curGeofences.size());
    }

    /**
     * Called from VolleyRequester. Handles the JSONObjects received
     * when we requested new geofences from the server
     * @param geofences
     */
    public void handleNewGeofences(GeofenceObject geofences){
        if(geofences != null) {
            Log.i("VolleyInfo", "Handling new geofences: " + geofences.toString());
            Content[] geofencesContent = geofences.getContent();
            for(int i = 0; i<geofencesContent.length; i++){
                carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Geofence geofence =
                        geofencesContent[i].getGeofence();
                carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Location geofenceLocation = geofence.getLocation();
                double latitude = geofenceLocation.getLat();
                double longitude = geofenceLocation.getLng();
                int radius = geofence.getRadius();
                String name = geofencesContent[i].getName();
                allGeopointsByName.put(name, geofencesContent[i]);
                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence to identify the geofence
                        .setRequestId(name)
                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                latitude,
                                longitude,
                                radius
                        )
                        // Set the expiration duration of the geofence to never expire
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        // Set the transition types of interest to track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());

            }
            removeAllGeofences();
            addGeofences();

            MainFragment curFragment = adapter.getCurFragment();
            curFragment.handleNewGeofences(geofencesContent);


        } else {
            Log.i("VolleyInfo", "new geofences are null ");
        }
    }


    /**
     * checks whether phone has network connection. If not, displays a dialog
     * requesting that the user connects to a network.
     * @return
     */
    public boolean isConnectedToNetwork(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null){
            if(activeNetworkInfo.isConnected()) {
                return true;
            } else {
                showNetworkNotConnectedDialog();
                return false;
            }
        }else {
            showNetworkNotConnectedDialog();
            return false;
        }
    }

    /**
     * displays a dialog requesting that the user connect to a network
     */
    public void showNetworkNotConnectedDialog() {
        showAlertDialog(getResources().getString(R.string.no_network_connection),
                networkAlertDialog);
    }

    private void showGooglePlayServicesUnavailableDialog(){
        showAlertDialog(getResources().getString(R.string.no_google_services), playServicesConnectivityAlertDialog);
    }

    /**
     * shows an alert dialog with the specified message
     * @param message
     */
    public void showAlertDialog(String message, AlertDialog dialog) {
        if(!dialog.isShowing()) {
            dialog.setTitle("Alert");
            dialog.setMessage(message);
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener()

                    {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            dialog.show();
        }
    }

    /**
     * TODO: Figure out how to show error messages and repeat requests when Volley isn't working
     */
}
