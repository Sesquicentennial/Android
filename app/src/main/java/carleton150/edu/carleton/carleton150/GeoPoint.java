package carleton150.edu.carleton.carleton150;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by haleyhinze on 10/14/15.
 */
public class GeoPoint {

    private LatLng latLng;
    private String name;
    private int smallRadius;
    private int bigRadius;

    public GeoPoint(LatLng latLng, String name, int smallRadius, int bigRadius) {
        this.latLng = latLng;
        this.name = name;
        this.smallRadius = smallRadius;
        this.bigRadius = bigRadius;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public int getSmallRadius() {
        return smallRadius;
    }

    public int getBigRadius() {
        return bigRadius;
    }
}
