package carleton150.edu.carleton.carleton150.POJO.GeofenceObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceObjectLocation
{
    private double lng;

    private double lat;

    public double getLng ()
    {
        return lng;
    }

    public void setLng (double lng)
    {
        this.lng = lng;
    }

    public double getLat ()
    {
        return lat;
    }

    public void setLat (double lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "Location [lng = "+lng+", lat = "+lat+"]";
    }
}
