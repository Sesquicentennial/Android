package carleton150.edu.carleton.carleton150.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Array;
import java.util.ArrayList;

import carleton150.edu.carleton.carleton150.GeoPoint;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created by haleyhinze on 11/5/15.
 */
public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater;
    ArrayList<GeoPoint> currentGeopoints = new ArrayList<>();

    public MyInfoWindowAdapter(){

    } public MyInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }


    //TODO: SET CURRENT GEOPOINTS WHEN WE HAVE THEM!!!!!
    public void setCurrentGeopoints(ArrayList<GeoPoint> currentGeopoints){
        this.currentGeopoints = currentGeopoints;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Getting view from the layout file info_window_layout
        View v = inflater.inflate(R.layout.custom_info_window, null);

        // Getting the position from the marker
        LatLng clickMarkerLatLng = marker.getPosition();

        TextView title = (TextView) v.findViewById(R.id.title);
        TextView snippet = (TextView) v.findViewById(R.id.snippet);
        snippet.setText("Latitude: " + clickMarkerLatLng.latitude + " Longitude: " + clickMarkerLatLng.longitude);
        title.setText(marker.getTitle());
        //TODO: SET IMAGE USING THE CURRENTGEOPOINTS

        // Returning the view containing InfoWindow contents
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;

    }
}
