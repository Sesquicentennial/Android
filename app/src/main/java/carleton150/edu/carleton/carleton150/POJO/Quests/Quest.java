package carleton150.edu.carleton.carleton150.POJO.Quests;

import java.util.HashMap;

public class Quest
{
    private String desc;

    private String compMsg;

    private String name;

    private HashMap<String, Waypoint> waypoints;

    public String getDesc ()
    {
        return desc;
    }

    public void setDesc (String desc)
    {
        this.desc = desc;
    }

    public String getCompMsg ()
    {
        return compMsg;
    }

    public void setCompMsg (String compMsg)
    {
        this.compMsg = compMsg;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public HashMap<String, Waypoint> getWaypoints ()
    {
        return waypoints;
    }

    public void setWaypoints (HashMap<String, Waypoint> waypoints)
    {
        this.waypoints = waypoints;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [desc = "+desc+", compMsg = "+compMsg+", name = "+name+", waypoints = "+waypoints+"]";
    }
}