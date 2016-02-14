package carleton150.edu.carleton.carleton150.POJO.Quests;

import android.media.Image;

import java.util.HashMap;

public class Quest
{
    private String desc;

    private String compMsg;

    private String name;

    private String creator;

    private String rating;

    private String image;

    private Waypoint[] waypoints;

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

    public Waypoint[] getWaypoints ()
    {
        return waypoints;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setWaypoints (Waypoint[] waypoints)
    {
        this.waypoints = waypoints;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [desc = "+desc+", compMsg = "+compMsg+", name = "+name+", waypoints = "+waypoints+"]";
    }
}