package carleton150.edu.carleton.carleton150.Models;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.LogMessages;
import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;
import carleton150.edu.carleton.carleton150.MyApplication;
import carleton150.edu.carleton.carleton150.POJO.EventObject.Events;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoRequestObject.GeofenceInfoRequestObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObjectContent;
import carleton150.edu.carleton.carleton150.POJO.GeofenceObject.GeofenceObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject.Geofence;
import carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject.GeofenceRequestObject;
import carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject.Location;
import carleton150.edu.carleton.carleton150.POJO.Quests.Quest;

/**
 * Created by haleyhinze on 10/28/15.
 *
 * Class to make server requests
 */
public class VolleyRequester {

    private LogMessages logMessages = new LogMessages();

    public VolleyRequester(){
    }

    /**
     * Requests information about the current active geofences. When information is received, calls
     * a method in the callerFragment to handle this new information.
     * @param callerFragment the fragment that called request()
     * @param mGeofenceList the list of geofences that we are requesting information for
     */
    public void request(final MainFragment callerFragment, ArrayList<GeofenceObjectContent> mGeofenceList) {
        if(mGeofenceList == null){
            return;
        }
        final GeofenceInfoRequestObject geofenceInfoRequestObject = new GeofenceInfoRequestObject();
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
        Log.i(logMessages.VOLLEY, "request : requestObject: " + jsonObjectrequest.toString());
        JsonObjectRequest request = new JsonObjectRequest("https://carl150.carleton.edu/info", jsonObjectrequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String responseString = response.toString();
                        GeofenceInfoObject geofenceInfoResponseObject = gson.fromJson(responseString, GeofenceInfoObject.class);
                        Log.i(logMessages.VOLLEY, "request : length of response: " + geofenceInfoResponseObject.getContent().length);
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
     * @param callerActivity
     */
    public void requestGeofences(double latitude, double longitude, final MainActivity callerActivity) {
        Log.i(logMessages.VOLLEY, "requestGeofences : about to request geofences. Lat: " + latitude + " Long: " + longitude);
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
            Log.i(logMessages.VOLLEY, "requestGeofences : JSONException ");
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://carl150.carleton.edu/geofences", jsonObjectrequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String responseString = response.toString();
                        Log.i(logMessages.VOLLEY, "requestGeofences : response string = : " + responseString);
                        GeofenceObject responseObject = gson.fromJson(responseString, GeofenceObject.class);
                        callerActivity.handleNewGeofences(responseObject.getContent());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(callerActivity!=null) {
                            Log.i(logMessages.VOLLEY, "requestGeofences : MainActivity is not null");
                            Log.i(logMessages.VOLLEY, "requestGeofences : error : " + error.toString());
                            error.printStackTrace();
                            callerActivity.handleNewGeofences(null);
                        }else{
                            Log.i(logMessages.VOLLEY, "requestGeofences : MainActivity is null");
                        }

                    }
                }
        );
        MyApplication.getInstance().getRequestQueue().add(request);
    }

    public void requestEvents(String startTime, int limit, final MainFragment mainFragment){

        final Gson gson = new Gson();
        //Creates request object
        JSONObject eventRequest = new JSONObject();
        try {
            eventRequest.put("startTime", startTime);
            eventRequest.put("limit", limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest("https://carl150.carleton.edu/events", eventRequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String responseString = response.toString();
                        Log.i(logMessages.VOLLEY, "requestEvents : response string = : " + responseString);
                        Events responseObject = gson.fromJson(responseString, Events.class);
                        mainFragment.handleNewEvents(responseObject);
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(logMessages.VOLLEY, "requestEvents : error : " + error.toString());
                        if(mainFragment!=null) {
                            mainFragment.handleNewEvents(null);
                        }
                    }
                }
        );
        MyApplication.getInstance().getRequestQueue().add(request);

    }

    public void requestQuests(final MainFragment callerFragment){
        final Gson gson = new Gson();
        JSONObject emptyRequest = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest("https://carl150.carleton.edu/quest", emptyRequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String responseString = response.toString();
                        ArrayList<Quest> quests = new ArrayList<>();
                        try {
                            JSONArray responseArr = response.getJSONArray("content");
                            for(int i = 0; i<responseArr.length(); i++){
                                Quest responseQuest = gson.fromJson(responseArr.getString(i), Quest.class);
                                Log.i(logMessages.VOLLEY, "requestQuests : quest response string = : " + responseArr.getString(i));
                                quests.add(responseQuest);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i(logMessages.VOLLEY, "requestQuests : response string = : " + responseString);
                        callerFragment.handleNewQuests(quests);
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(logMessages.VOLLEY, "requestQuests : error : " + error.toString());
                        if(callerFragment!=null) {
                            callerFragment.handleNewQuests(null);
                        }
                    }
                }
        );
        MyApplication.getInstance().getRequestQueue().add(request);
    }
}
