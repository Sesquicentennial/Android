package carleton150.edu.carleton.carleton150.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import carleton150.edu.carleton.carleton150.GeoPoint;
import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.Content;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created by haleyhinze on 11/5/15.
 *
 * Class to override methods in GoogleMap.InfoWindowAdapter so that we can
 * make custom views for InfoWindows (windows that pop up when a google map
 * marker is clicked)
 *
 */
public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater;
    Content[] currentGeopoints;
    HashMap<String, Content> currentGeopointsMap;

    public MyInfoWindowAdapter(){

    }

    public MyInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }


    //TODO: SET CURRENT GEOPOINTS WHEN WE HAVE THEM!!!!!
    public void setCurrentGeopoints(Content[] currentGeopoints){
        this.currentGeopoints = currentGeopoints;
    }

    public void setCurrentGeopointsMap(HashMap<String, Content> currentGeopointsMap){
        this.currentGeopointsMap = currentGeopointsMap;
    }

    /**
     * returns a custom view for the InfoWindow
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(Marker marker) {
        // Getting view from the layout file info_window_layout
        View v = inflater.inflate(R.layout.custom_info_window, null);

        // Getting the position from the marker
        LatLng clickMarkerLatLng = marker.getPosition();

        TextView title = (TextView) v.findViewById(R.id.title);
        TextView snippet = (TextView) v.findViewById(R.id.snippet);
        if(currentGeopointsMap.get(marker.getTitle()) != null) {
            String summary = currentGeopointsMap.get(marker.getTitle()).getSummary();
            snippet.setText(summary);
        }
        title.setText(marker.getTitle());
        //TODO: SET IMAGE USING THE CURRENTGEOPOINTS

        // Returning the view containing InfoWindow contents
        return v;
    }

    /**
     * This would be used to set window contents to the default view. We are
     * using a custom view so it's isn't necessary to return anything here
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;

    }
}
