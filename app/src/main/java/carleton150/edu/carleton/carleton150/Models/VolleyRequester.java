package carleton150.edu.carleton.carleton150.Models;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;
import carleton150.edu.carleton.carleton150.MyApplication;

/**
 * Created by haleyhinze on 10/28/15.
 */
public class VolleyRequester {

    private MainFragment callerFragment;

    public VolleyRequester(MainFragment callerFragment){
        this.callerFragment = callerFragment;
    }

    public void request() {
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
                        callerFragment.handleResult(response.toString());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callerFragment.handleResult("Error with Volley: " + error.toString());

                    }
                }
        );
        MyApplication.getInstance().getRequestQueue().add(request);
    }


}
