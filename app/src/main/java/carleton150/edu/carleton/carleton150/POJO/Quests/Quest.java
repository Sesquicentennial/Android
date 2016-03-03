package carleton150.edu.carleton.carleton150.POJO.Quests;

import android.util.Log;


public class Quest
{
    private String desc;

    private String compMsg;

    private String name;

    private String creator;

    private String difficulty;

    private String audience;

    private String image;

    private Waypoint[] waypoints;

    public String getDesc ()
    {
        return desc;
    }

    public void setDesc (String desc)
    {
        Log.i("JSON debugging", "Quest: setDesc");
        this.desc = desc;
    }

    public String getCompMsg ()
    {
        return compMsg;
    }

    public void setCompMsg (String compMsg)
    {
        Log.i("JSON debugging", "Quest: setCompMsg");
        this.compMsg = compMsg;
    }

    public String getName ()
    {

        return name;
    }

    public void setName (String name)
    {
        Log.i("JSON debugging", "Quest: setName");
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
        Log.i("JSON debugging", "Quest: setImage");
        this.image = image;
    }

    public void setWaypoints (Waypoint[] waypoints)

    {
        Log.i("JSON debugging", "Quest: setWaypoint");
        this.waypoints = waypoints;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        Log.i("JSON debugging", "Quest: setCreator");
        this.creator = creator;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty)
    {
        Log.i("JSON debugging", "Quest: setDifficulty");
        this.difficulty = difficulty;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        Log.i("JSON debugging", "Quest: setAudience");
        this.audience = audience;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [desc = "+desc+", compMsg = "+compMsg+", name = "+name+", waypoints = "+waypoints+"]";
    }
}