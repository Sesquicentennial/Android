package carleton150.edu.carleton.carleton150.POJO.GeofenceInfoRequestObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceInfoRequestObject
{
    private String[] geofences;

    public String[] getGeofences ()
    {
        return geofences;
    }

    public void setGeofences (String[] geofences)
    {
        this.geofences = geofences;
    }

    @Override
    public String toString()
    {
        return "GeofenceInfoRequestObject [geofences = "+geofences+"]";
    }
}
