package carleton150.edu.carleton.carleton150.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import carleton150.edu.carleton.carleton150.GeoPoint;

/**
 * Created by haleyhinze on 10/4/15.
 *
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

    public ArrayList<GeoPoint> getCircleCenters(){

        GeoPoint recCenter = new GeoPoint(new LatLng(44.46430023, -93.14958939), "Recreation Center", 40, 60);
        GeoPoint cmc = new GeoPoint(new LatLng(44.46234939, -93.15400795), "CMC", 30, 40);
        GeoPoint goodhue = new GeoPoint(new LatLng(44.46260362, -93.14990513), "Goodhue", 40, 60);
        GeoPoint laird = new GeoPoint(new LatLng(44.46217483, -93.15392314), "Laird", 20, 30);
        GeoPoint collier = new GeoPoint(new LatLng(44.459351, -93.158082), "Collier House", 50, 75);
        GeoPoint chapel = new GeoPoint(new LatLng(44.460174, -93.154726), "Skinner Memorial Chapel", 100, 60);


        ArrayList<GeoPoint> centerPoints = new ArrayList<>();

        centerPoints.add(recCenter);
        centerPoints.add(cmc);
        centerPoints.add(goodhue);
        centerPoints.add(laird);
        centerPoints.add(collier);
        centerPoints.add(chapel);

        return centerPoints;
    }
}
