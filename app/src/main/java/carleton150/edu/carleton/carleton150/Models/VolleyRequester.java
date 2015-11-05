package carleton150.edu.carleton.carleton150.Models;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;
import carleton150.edu.carleton.carleton150.MyApplication;

/**
 * Created by haleyhinze on 10/28/15.
 *
 * Class to make server requests
 */
public class VolleyRequester {

    public VolleyRequester(){
    }

    public void request(final MainFragment callerFragment) {

        JSONObject geofence = new JSONObject();
        try {
            JSONObject object = new JSONObject();
            JSONObject latLong = new JSONObject();

            JSONObject timespan = new JSONObject();

            latLong.put("x", 50.0);
            latLong.put("y", 50.0);
            timespan.put("startTime", "");
            timespan.put("endTime", "");
            object.put("location", latLong);
            object.put("radius", 0);
            object.put("timespan", timespan);
            geofence.put("geofence", object);
        }catch (Exception exception){
            //TODO: do something here.
        }

        JsonObjectRequest request = new JsonObjectRequest("https://f37009fe.ngrok.io/landmarks", geofence,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        callerFragment.handleResult(response);
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

    public void requestGeofences(double latitude, double longitude, final MainActivity mainActivity) {
        JSONObject geofence = new JSONObject();
        try {
            JSONObject object = new JSONObject();
            JSONObject latLong = new JSONObject();

            latLong.put("x", latitude);
            latLong.put("y", longitude);
            object.put("location", latLong);
            object.put("radius", 0.01);

        }catch (Exception exception){
            //TODO: do something here.
        }

        JsonObjectRequest request = new JsonObjectRequest(" https://serveraddress.ngrok.io/geofences", geofence,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mainActivity.handleNewGeofences(response);
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
