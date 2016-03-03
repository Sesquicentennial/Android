package carleton150.edu.carleton.carleton150;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.ArrayList;
import java.util.List;
import carleton150.edu.carleton.carleton150.Models.GeofenceErrorMessages;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Sends a local broadcast
 * that is received by the MainActivity to handle results
 */
public class GeofencingTransitionsIntentService extends IntentService {

    private static Constants constants = new Constants();
    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofencingTransitionsIntentService() {
        super(constants.TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(constants.TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            //Sends local broadcast to mainActivity
            Intent mainBroadcastIntent = new Intent("GeofenceInfo");
            sendGeofenceBroadcast(mainBroadcastIntent, geofenceTransition, triggeringGeofences);

            // logs the transition details.
            Log.i(constants.TAG, geofenceTransitionDetails);

        } else {
            // Log the error.
            Log.e(constants.TAG, "error end of onHandleIntent");
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param context               The app context.
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Returns an array of geofence names
     *
     * @param triggeringGeofences
     * @return
     */
    private String[] getGeofenceNames(List<Geofence> triggeringGeofences){
        String[] geofenceNames = new String[triggeringGeofences.size()];
        for(int i = 0; i<triggeringGeofences.size(); i++){
            Geofence geofence = triggeringGeofences.get(i);
            geofenceNames[i] = geofence.getRequestId();
        }
        return geofenceNames;
    }

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
     * Sends a local broadcast to the MainActivity with a list of the geofences that were triggered
     * and the transition type
     * @param intent
     * @param geofenceTransition
     * @param triggeringGeofences
     */
    private void sendGeofenceBroadcast(Intent intent, int geofenceTransition, List<Geofence> triggeringGeofences){
        intent.putExtra("transitionType", geofenceTransition);
        intent.putExtra("geofenceNames", getGeofenceNames(triggeringGeofences));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
