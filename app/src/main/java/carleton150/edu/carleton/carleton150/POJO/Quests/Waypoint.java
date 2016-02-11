package carleton150.edu.carleton.carleton150.POJO.Quests;

/**
 * Created by haleyhinze on 1/19/16.
 */
public class Waypoint {

    private double lat;
    private double lng;
    private double rad;


    private Clue clue;

    private Hint hint;

    private Completion completion;


    public Clue getClue ()
    {
        return clue;
    }

    public void setClue (Clue clue)
    {
        this.clue = clue;
    }

    public Hint getHint ()
    {
        return hint;
    }

    public void setHint (Hint hint)
    {
        this.hint = hint;
    }



    public Completion getCompletion() {
        return completion;
    }

    public void setCompletion(Completion completion) {
        this.completion = completion;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getRad() {
        return rad;
    }

    public void setRad(double rad) {
        this.rad = rad;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [geofence = "+lat + lng+", clue = "+clue.getText()+", hint = "+hint.getText()+"]";
    }
}
