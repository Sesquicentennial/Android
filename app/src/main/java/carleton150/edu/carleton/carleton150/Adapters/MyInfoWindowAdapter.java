package carleton150.edu.carleton.carleton150.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject.GeofenceInfoContent;
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
    HashMap<String, GeofenceInfoContent[]> currentGeopointsMap;

    public MyInfoWindowAdapter(){

    }

    public MyInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }

    public void setCurrentGeopoints(HashMap<String, GeofenceInfoContent[]> currentGeopointsMap){
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

        TextView title = (TextView) v.findViewById(R.id.title);
        TextView snippet = (TextView) v.findViewById(R.id.snippet);
        if(currentGeopointsMap.get(marker.getTitle()) != null) {
            GeofenceInfoContent[] geofenceInfoContents = currentGeopointsMap.get(marker.getTitle());
            String summary = null;
            for(int i = 0; i<geofenceInfoContents.length; i++){
                if (summary != null){
                    break;
                }else if (geofenceInfoContents[i].getSummary() != null){
                    summary = geofenceInfoContents[i].getSummary();
                }
            }
            if(summary != null) {
                snippet.setText(summary);
            }
        }
        title.setText(marker.getTitle());

        // Returning the view containing InfoWindow contents
        return v;
    }

    /**
     * This would be used to set window contents in the default info window view. We are
     * using a custom view so it's isn't necessary to return anything here
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;

    }
}
