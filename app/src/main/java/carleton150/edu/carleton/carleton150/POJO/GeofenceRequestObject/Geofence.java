package carleton150.edu.carleton.carleton150.POJO.GeofenceRequestObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class Geofence
{
    private Location location;

    private int radius;

    public Location getLocation ()
    {
        return location;
    }

    public void setLocation (Location location)
    {
        this.location = location;
    }

    public int getRadius ()
    {
        return radius;
    }

    public void setRadius (int radius)
    {
        this.radius = radius;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [location = "+location+", radius = "+radius+"]";
    }
}