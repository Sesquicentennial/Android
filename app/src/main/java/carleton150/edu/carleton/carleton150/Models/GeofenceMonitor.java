package carleton150.edu.carleton.carleton150.Models;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.HashMap;
import carleton150.edu.carleton.carleton150.GeofencingTransitionsIntentService;
import carleton150.edu.carleton.carleton150.LogMessages;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectLocation;



/**
 * Class to monitor geofences
 *
 * Created by haleyhinze on 1/14/16.
 */
public class GeofenceMonitor{

    public Location currentLocation = null;
    protected Location currentGeofenceUpdateRequestLocation = null;
    protected Location lastGeofenceUpdateLocation = null;

    public HashMap<String, GeofenceObjectContent> curGeofencesMap = new HashMap<>();

    public GeofenceObjectContent[] geofencesBeingMonitored;
    protected PendingIntent mGeofencePendingIntent;
    public ArrayList<GeofenceObjectContent> curGeofences = new ArrayList<GeofenceObjectContent>();
    public HashMap<String, GeofenceObjectContent> allGeopointsByName = new HashMap<String, GeofenceObjectContent>();
    public boolean mGeofencesAdded = false;
    protected ArrayList<Geofence> mGeofenceList;

    private MainActivity activity;
    private LogMessages logMessages = new LogMessages();
    private boolean googlePlayConnected = false;

    public GeofenceMonitor(MainActivity activity) {
        this.activity = activity;
    }


    /**
     * Registers a broadcast receiver to receive broadcasts when a geofence
     * is entered or exited. Broadcast is sent from GeofencingTransitionsIntentService
     */
    public void startGeofenceMonitoring(){
        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;

        //Registers a broadcastReceiver to receive broadcasts when a geofence
        //is entered or exited (broadcast is sent from GeofencingTransitionsIntentService
        LocalBroadcastManager.getInstance(activity).registerReceiver(
                mMessageReceiver, new IntentFilter("GeofenceInfo"));

    }

    /**
     * Receives broadcast when geofence is entered or exited. Notifies the current fragment
     * that the current geofences that are triggered by the user have changed
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String[] geofenceNames = intent.getStringArrayExtra("geofenceNames");
            int transitionType = intent.getIntExtra("transitionType", -1);
            Log.i(logMessages.GEOFENCE_MONITORING, " GeofenceMonitor: about to update current geofences");
            //updates curGeofences by deleting geofences that were exited and adding geofences
            //that were entered
            updateCurrentGeofences(geofenceNames, transitionType);
            Log.i(logMessages.GEOFENCE_MONITORING, " GeofenceMonitor: about to handle geofence changes");
            //calls a method in the current fragment that handles the change in geofences
            handleGeofenceChange(curGeofences);
        }

    };

    /**
     * Called when the geofences currently triggered by the user change.
     * Sets curGeofences and curGeofencesMap to contain the current geofences
     * and then queries the database for information about those geofences.
     *
     * @param currentGeofences the geofences that the user is currently triggering
     */
    public void handleGeofenceChange(ArrayList<GeofenceObjectContent> currentGeofences) {
        Log.i(logMessages.GEOFENCE_MONITORING, "handleGeofenceChange Content length: " + currentGeofences.size());
        curGeofences = currentGeofences;
        curGeofencesMap.clear();
        for(int i = 0; i< curGeofences.size(); i++){
            curGeofencesMap.put(curGeofences.get(i).getName(), curGeofences.get(i));
        }
        Log.i("Fragment Handling", "GeofenceMonitor : about to call handleGeofenceChange : curGeofences size: " + curGeofences.size());
        activity.handleGeofenceChange(currentGeofences);
    }


    /**
     * Handles the result of a query for information about a geofence.
     * @param result is a GeofenceInfoObject that contains information
     *               about all geofences the user is currently in
     */
    public void handleResult(GeofenceInfoObject result) {
        if (result == null) {
            Log.i(logMessages.VOLLEY, "handleResult : result is NULL");
            //TODO: Do something here if there is an error (check error message, check internet, maybe make new query, etc..)
            //   queryResult.setText("Error with volley");
        } else {
            Log.i(logMessages.VOLLEY, "handleResult: result is: " + result.toString());
            try {
                Log.i(logMessages.VOLLEY, "GeofenceMonitor handleResult : got content");
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.i(logMessages.VOLLEY, "GeofenceMonitor handleResult : NullPointerException");
            }
        }

    }

    public ArrayList<GeofenceObjectContent> getCurGeofences(){
        return this.curGeofences;
    }

    /**
     * Called from MainActivity if user's location changes. If
     * a certain distance has passed since the last geofence update
     * and the historyFragment is in view, gets new geofences to monitor
     * (This is so the number of geofences being monitored at any
     * given time is limited)
     *
     * @param newLocation
     */
    public void handleLocationChange(Location newLocation) {
        currentLocation = newLocation;

        if (lastGeofenceUpdateLocation == null) {
            currentGeofenceUpdateRequestLocation = newLocation;
            getNewGeofences();
        } else if (newLocation.distanceTo(lastGeofenceUpdateLocation) > 1000) {
            currentGeofenceUpdateRequestLocation = newLocation;
            getNewGeofences();
        }

    }

    /**
     * Requests geofences from server using VolleyRequester
     */
    public boolean getNewGeofences(){
        try {
            activity.mVolleyRequester.requestGeofences(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), activity);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
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
        Intent intent = new Intent(activity, GeofencingTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Tells google play services api that we don't want to listen for the geofences anymore.
     */
    public void removeAllGeofences(){
        mGeofencesAdded = false;
        LocationServices.GeofencingApi.removeGeofences(
                activity.mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(activity); // Result processed in onResult().
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
        Log.i(logMessages.GEOFENCE_MONITORING, "updateCurrentGeofences : length of geofenceNames: " + geofenceNames.length);
        Log.i(logMessages.GEOFENCE_MONITORING, "updateCurrentGeofences : transition type: " + transitionType);

        if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
            Log.i(logMessages.GEOFENCE_MONITORING, "updateCurrentGeofences : transition type enter");
        }
        for(int i = 0; i<geofenceNames.length; i++){
            if(allGeopointsByName.containsKey(geofenceNames[i])) {
                if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    if (!curGeofencesMap.containsKey(geofenceNames[i])) {
                        curGeofences.add(allGeopointsByName.get(geofenceNames[i]));
                    }
                } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    if (curGeofences.contains(allGeopointsByName.get(geofenceNames[i]))) {
                        curGeofences.remove(allGeopointsByName.get(geofenceNames[i]));
                    } else {
                        //Shouldn't ever happen...
                    }
                }
            }
        }
        Log.i(logMessages.GEOFENCE_MONITORING, "updateCurrentGeofences curGeofences length: " + curGeofences.size());
    }

    /**
     * Called from the MainActivity by a method that was called by
     * VolleyRequester. Handles the JSONObjects received
     * when we requested new geofences from the server
     * @param geofencesContent
     */
    public void handleNewGeofences(GeofenceObjectContent[] geofencesContent){
        if (geofencesContent != null) {
            Log.i(logMessages.VOLLEY, "handleNewGeofences : content is: " + geofencesContent.toString());
            lastGeofenceUpdateLocation = currentGeofenceUpdateRequestLocation;
            for (int i = 0; i < geofencesContent.length; i++) {
                carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Geofence geofence =
                        geofencesContent[i].getGeofence();
                GeofenceObjectLocation geofenceLocation = geofence.getLocation();
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
            Log.i(logMessages.VOLLEY, "handleNewGeofences : new geofences are null ");
            Toast toast = Toast.makeText(activity, "Null info result", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void startMonitoringGeofencesAfterPause(){
        if(googlePlayConnected) {
            removeAllGeofences();
            addGeofences();
        }
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     * Note that this is only called if the HistoryFragment is currently in view
     */
    public void addGeofences() {
        if (!activity.mGoogleApiClient.isConnected()) {
            Toast.makeText(activity, "Not connected to google api client", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    activity.mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // This pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(activity); // Result processed in onResult().
        } catch (Exception e) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            e.printStackTrace();
        }

    }


    /**
     * Called by MainActivity when google play services is connected.
     * Requests new geofences.
     */
    public void googlePlayServicesConnected() {
        googlePlayConnected = true;
        if(currentLocation!=null) {
            getNewGeofences();
        }
    }

}
