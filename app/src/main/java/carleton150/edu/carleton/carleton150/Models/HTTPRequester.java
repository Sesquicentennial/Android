package carleton150.edu.carleton.carleton150.Models;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import carleton150.edu.carleton.carleton150.MainFragments.MainFragment;

/**
 * Created by haleyhinze on 10/28/15.
 */
public class HTTPRequester extends AsyncTask<String, String, String> {

    private MainFragment callerFragment;

        public HTTPRequester(MainFragment callerFragment){
            this.callerFragment = callerFragment;
        }


        @Override
        protected String doInBackground(String... args) {

            try {
                return downloadUrl("https://f37009fe.ngrok.io/landmarks");
            } catch (IOException e) {


                try {
                    JSONObject object = new JSONObject();
                    JSONObject latLong = new JSONObject();
                    JSONObject geofence = new JSONObject();
                    JSONObject timespan = new JSONObject();

                    latLong.put("x", 50.0);
                    latLong.put("y", 50.0);
                    timespan.put("startTime", "");
                    timespan.put("endTime", "");
                    object.put("location", latLong);
                    object.put("radius", 0);
                    object.put("timespan", timespan);
                    geofence.put("geofence", object);
                    return geofence.toString();
                }catch (Exception exception){
                    return "Unable to retrieve web page. URL may be invalid.";
                }


            }
        }

        @Override
        protected void onPostExecute(String result) {
            callerFragment.handleResult(result);
            //Do something with the JSON string

        }


    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            /*URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000 *//* milliseconds *//*);
            conn.setConnectTimeout(150000 *//* milliseconds *//*);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("POST");
            //conn.setChunkedStreamingMode(0); // Use default chunk size
            conn.setDoOutput(true);*/

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(100000 /* milliseconds */);
            //conn.setConnectTimeout(150000 /* milliseconds */);
            //conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("GET");
            //conn.setChunkedStreamingMode(0); // Use default chunk size
            conn.setDoInput(true);
            



           /* JSONObject object = new JSONObject();
            JSONObject latLong = new JSONObject();
            JSONObject geofence = new JSONObject();
            JSONObject timespan = new JSONObject();

            latLong.put("x", 50.0);
            latLong.put("y", 50.0);
            timespan.put("startTime", "");
            timespan.put("endTime", "");
            object.put("location", latLong);
            object.put("radius", 0);
            object.put("timespan", timespan);
            geofence.put("geofence", object);


            // Write serialized JSON data to output stream.
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(geofence.toString());

*/
            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();
            Log.d("tag", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            // Close streams and disconnect.
           // writer.close();
           // out.close();
            conn.disconnect();
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } /*catch (JSONException e) {
            e.printStackTrace();
            return "Error with JSON";
        }*/ finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    }

