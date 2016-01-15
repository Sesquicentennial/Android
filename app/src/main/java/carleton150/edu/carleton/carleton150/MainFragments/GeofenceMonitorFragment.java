package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.HashMap;
import carleton150.edu.carleton.carleton150.GeofencingTransitionsIntentService;
import carleton150.edu.carleton.carleton150.Models.GeofenceErrorMessages;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;
import carleton150.edu.carleton.carleton150.R;


/**
 * Created by haleyhinze on 1/14/16.
 */
public class GeofenceMonitorFragment extends MainFragment implements ResultCallback<Status> {


    protected Location currentLocation = null;
    protected Location currentGeofenceUpdateRequestLocation = null;
    protected Location lastGeofenceUpdateLocation = null;


    protected HashMap<String, Content> curGeofencesMap = new HashMap<>();
    protected carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content[] curGeofenceInfo;
    protected HashMap<String, carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content>
            curGeofencesInfoMap = new HashMap<>();

    protected Content[] geofencesBeingMonitored;
    protected PendingIntent mGeofencePendingIntent;
    protected ArrayList<Content> curGeofences = new ArrayList<Content>();
    protected HashMap<String, Content> allGeopointsByName = new HashMap<String, Content>();
    protected boolean mGeofencesAdded = false;
    protected ArrayList<Geofence> mGeofenceList;


    public GeofenceMonitorFragment() {
        // Required empty public constructor
    }


    public void startGeofenceMonitoring(){
        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;

        //Registers a broadcastReceiver to receive broadcasts when a geofence
        //is entered or exited (broadcast is sent from GeofencingTransitionsIntentService
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("GeofenceInfo"));

    }

    /**
     * Receives broadcast when geofence is entered or exited
     */
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
            handleGeofenceChange(curGeofences);
        }
    };




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
            String curGeofenceName = currentGeofences[i].getName();
            curGeofencesInfoMap.put(curGeofenceName, currentGeofences[i]);
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
        if (result == null) {
            Log.i("result is: ", "NULL");
            //TODO: Do something here if there is an error (check error message, check internet, maybe make new query, etc..)
            //   queryResult.setText("Error with volley");
        } else {
            Log.i("result is: ", result.toString());
            try {
                curGeofenceInfo = result.getContent();
                makeGeofenceInfoMap(curGeofenceInfo);


            }catch (NullPointerException e){
                e.printStackTrace();
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
        currentLocation = newLocation;
        if(lastGeofenceUpdateLocation == null){
            currentGeofenceUpdateRequestLocation = newLocation;
            getNewGeofences();
        }
        else if(newLocation.distanceTo(lastGeofenceUpdateLocation) > 1000){
            currentGeofenceUpdateRequestLocation = newLocation;
            getNewGeofences();
        }
    }




    /**
     * Requests geofences from server using VolleyRequester
     */
    protected void getNewGeofences(){
        if(mainActivity.isConnectedToNetwork()) {
            mainActivity.mVolleyRequester.requestGeofences(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), this);
        }
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
        if (!mainActivity.mGoogleApiClient.isConnected()) {
            Toast.makeText(mainActivity, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mainActivity.mGoogleApiClient,
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
        Log.e("Security Exception", "Invalid location permission. " +
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
            mGeofencesAdded = !mGeofencesAdded;
        } else {
            // Get the status code and log it.
            String errorMessage = GeofenceErrorMessages.getErrorString(mainActivity,
                    status.getStatusCode());
            Log.e("Geofence error", errorMessage);
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
        Intent intent = new Intent(mainActivity, GeofencingTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(mainActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Tells google play services api that we don't want to listen for the geofences anymore.
     * //TODO: Do we need a check here to make sure they deleted before adding the next ones?
     */
    public void removeAllGeofences(){
        mGeofencesAdded = false;
        LocationServices.GeofencingApi.removeGeofences(
                mainActivity.mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
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
     * @param geofencesContent
     */
    @Override
    public void handleNewGeofences(Content[] geofencesContent){
        if(geofencesContent != null) {
            Log.i("VolleyInfo", "Handling new geofences: " + geofencesContent.toString());
            lastGeofenceUpdateLocation = currentGeofenceUpdateRequestLocation;
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
        } else {
            Log.i("VolleyInfo", "new geofences are null ");

            Toast toast = Toast.makeText(mainActivity, "Null info result", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void googlePlayServicesConnected() {
        super.googlePlayServicesConnected();
        if(currentLocation!=null) {
            getNewGeofences();
        }
    }


}
