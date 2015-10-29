package carleton150.edu.carleton.carleton150.MainFragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.GeoPoint;
import carleton150.edu.carleton.carleton150.Models.HTTPRequester;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by haleyhinze on 10/28/15.
 */
public class MainFragment extends Fragment {

    HTTPRequester httpRequester = new HTTPRequester(this);

    public MainFragment() {

    }

    public void handleGeofenceChange(ArrayList<GeoPoint> currentGeofences) {

    }

    public void queryDatabase(){
        httpRequester.execute();
    }

    public void handleResult(String result){

    }



}


