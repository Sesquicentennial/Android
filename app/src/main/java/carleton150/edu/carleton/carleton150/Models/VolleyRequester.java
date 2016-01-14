package carleton150.edu.carleton.carleton150.Models;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;
import carleton150.edu.carleton.carleton150.MyApplication;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoRequestObject.GeofenceInfoRequestObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.Content;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject.Geofence;
import carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject.GeofenceRequestObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject.Location;

/**
 * Created by haleyhinze on 10/28/15.
 *
 * Class to make server requests
 */
public class VolleyRequester {

    public VolleyRequester(){
    }

    /**
     * Requests information about the current active geofences. When information is received, calls
     * a method in the callerFragment to handle this new information.
     * @param callerFragment the fragment that called request()
     * @param mGeofenceList the list of geofences that we are requesting information for
     */
    public void request(final MainFragment callerFragment, ArrayList<Content> mGeofenceList) {
        if(mGeofenceList == null){
            return;
        }
        GeofenceInfoRequestObject geofenceInfoRequestObject = new GeofenceInfoRequestObject();
        String[] geofenceStrings = new String[mGeofenceList.size()];

        for(int i = 0 ; i<mGeofenceList.size(); i++){
            geofenceStrings[i] = mGeofenceList.get(i).getName();
        }
        geofenceInfoRequestObject.setGeofences(geofenceStrings);

        //Turns the geofenceInfoRequestObject into a JsonObject to send to server
        final Gson gson = new Gson();
        String jsonString = gson.toJson(geofenceInfoRequestObject);
        JSONObject jsonObjectrequest = null;
        try {
            jsonObjectrequest = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("Request info: ", jsonObjectrequest.toString());
        JsonObjectRequest request = new JsonObjectRequest("https://carl150.carleton.edu/info", jsonObjectrequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String responseString = response.toString();
                        GeofenceInfoObject geofenceInfoResponseObject = gson.fromJson(responseString, GeofenceInfoObject.class);
                        callerFragment.handleResult(geofenceInfoResponseObject);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callerFragment.handleResult(null);

                    }
                }
        );
        MyApplication.getInstance().getRequestQueue().add(request);
    }

    /**
     * Method to request new geofences to monitor. When it receives the new geofences,
     * calls a method in the mainActivity to handle the new geofences
     *
     * @param latitude user's latitude
     * @param longitude user's longitude
     * @param mainActivity
     */
    public void requestGeofences(double latitude, double longitude, final MainActivity mainActivity) {
        Log.i("Volley info", "about to request geofences");
        Location location = new Location();
        location.setLat(latitude);
        location.setLng(longitude);
        Geofence geofence = new Geofence();
        geofence.setLocation(location);
        geofence.setRadius(1300);
        GeofenceRequestObject geofenceRequestObject = new GeofenceRequestObject();
        geofenceRequestObject.setGeofence(geofence);

        final Gson gson = new Gson();
        String jsonString = gson.toJson(geofenceRequestObject);
        JSONObject jsonObjectrequest = null;
        try {
            jsonObjectrequest = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest("https://carl150.carleton.edu/geofences", jsonObjectrequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String responseString = response.toString();
                        Log.i("VolleyStuff", "response string = : " + responseString);
                        GeofenceObject responseObject = gson.fromJson(responseString, GeofenceObject.class);
                        mainActivity.handleNewGeofences(responseObject);
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(mainActivity!=null) {
                            Log.i("VolleyStuff", "MainActivity is not null");
                            mainActivity.handleNewGeofences(null);
                        }else{
                            Log.i("VolleyStuff", "MainActivity is null");
                        }

                    }
                }
        );
        MyApplication.getInstance().getRequestQueue().add(request);
    }
}
