package carleton150.edu.carleton.carleton150.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haleyhinze on 10/4/15.
 */
public class DummyLocations {


    public List<List> getLocationsArray(){
        LatLng point1 = new LatLng(44.46022369, -93.15448424);
        LatLng point2 = new LatLng(44.46022369, -93.15748424);
        LatLng point3 = new LatLng(44.46522369, -93.15748424);
        LatLng point4 = new LatLng(44.46522369, -93.15448424);

        List<LatLng> polygon1 = new ArrayList<>();
        polygon1.add(point1);
        polygon1.add(point2);
        polygon1.add(point3);
        polygon1.add(point4);

        List<List> locations = new ArrayList<List>();
        locations.add(polygon1);
        return locations;

    }

    public ArrayList<LatLng> getCircleCenters(){
        LatLng point1 = new LatLng(44.46022369, -93.15448424);
        LatLng point2 = new LatLng(44.46022369, -93.15748424);
        LatLng point3 = new LatLng(44.46522369, -93.15748424);
        LatLng point4 = new LatLng(44.46522369, -93.15448424);

        ArrayList<LatLng> centerPoints = new ArrayList<>();
        centerPoints.add(point1);
        centerPoints.add(point2);
        centerPoints.add(point3);
        centerPoints.add(point4);
        return centerPoints;
    }
}
