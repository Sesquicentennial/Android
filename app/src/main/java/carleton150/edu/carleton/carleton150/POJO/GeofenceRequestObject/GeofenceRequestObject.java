package carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceRequestObject
{
    private Geofence geofence;

    public Geofence getGeofence ()
    {
        return geofence;
    }

    public void setGeofence (Geofence geofence)
    {
        this.geofence = geofence;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [geofence = "+geofence+"]";
    }
}
