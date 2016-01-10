package carleton150.edu.carleton.carleton150.POJO;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by haleyhinze on 1/10/16.
 */
public class Event {

    private Date date;
    private String title;
    private String description;
    private String location;
    private Time time;

    public Date getDate() {
        return date;
    }

    public String getFormattedDate(){
        String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);
        String stringMonth = (String) android.text.format.DateFormat.format("MMM", date);
        String year = (String) android.text.format.DateFormat.format("yyyy", date);
        String day = (String) android.text.format.DateFormat.format("dd", date);
        String hours = (String) android.text.format.DateFormat.format("HH", date);
        String minutes = (String) android.text.format.DateFormat.format("mm", date);

        String date = dayOfTheWeek + ", " + stringMonth + " " + day + ", " + year + " at " + hours + ":" + minutes;
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
