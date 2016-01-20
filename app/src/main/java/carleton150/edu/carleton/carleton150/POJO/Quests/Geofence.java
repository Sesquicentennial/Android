package carleton150.edu.carleton.carleton150.POJO.Quests;

public class Geofence
{
    private String lng;

    private String rad;

    private String lat;

    public String getLng ()
    {
        return lng;
    }

    public void setLng (String lng)
    {
        this.lng = lng;
    }

    public String getRad ()
    {
        return rad;
    }

    public void setRad (String rad)
    {
        this.rad = rad;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [lng = "+lng+", rad = "+rad+", lat = "+lat+"]";
    }
}