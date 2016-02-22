package carleton150.edu.carleton.carleton150.POJO.GeofenceObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceObjectContent
{
    private Geofence geofence;

    private String _id;

    private String name;

    public Geofence getGeofence ()
    {
        return geofence;
    }

    public void setGeofence (Geofence geofence)
    {
        this.geofence = geofence;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Content [, name = "+name+"]";
    }
}

