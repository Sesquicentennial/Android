package carleton150.edu.carleton.carleton150.POJO.Quests;

/**
 * Created by haleyhinze on 1/19/16.
 */
public class Waypoint {

    private Geofence geofence;

    private String clue;

    private String hint;

    public Geofence getGeofence ()
    {
        return geofence;
    }

    public void setGeofence (Geofence geofence)
    {
        this.geofence = geofence;
    }

    public String getClue ()
    {
        return clue;
    }

    public void setClue (String clue)
    {
        this.clue = clue;
    }

    public String getHint ()
    {
        return hint;
    }

    public void setHint (String hint)
    {
        this.hint = hint;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [geofence = "+geofence+", clue = "+clue+", hint = "+hint+"]";
    }
}
