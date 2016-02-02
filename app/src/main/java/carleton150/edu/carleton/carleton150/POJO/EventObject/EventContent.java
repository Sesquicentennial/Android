package carleton150.edu.carleton.carleton150.POJO.EventObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventContent
{
    private String startTime;

    private String duration;

    private String title;

    private String location;

    private String description;

    private boolean isExpanded = false;

    public String getStartTime () {
        /*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        String newStartTime;
        try {
            newStartTime = dateFormat.format(startTime);
        }
        catch (ParseException e) {
            try {
                newStartTime = dateFormat2.format(startTime);
            }
            catch (ParseException e2) {
                return startTime;
            }
        }
        return newStartTime;*/
        return startTime;

        //SimpleDateFormat oldTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date parsedStartTime = oldTimeFormat.parse(startTime);
        //String newTimeFormat = new SimpleDateFormat("MM/dd/yyyy").format(oldTimeFormat);
        //return newTimeFormat;
    }

    public void setStartTime (String startTime)
    {
        this.startTime = startTime;
    }

    public String getDuration ()
    {
        return duration;
    }

    public void setDuration (String duration)
    {
        this.duration = duration;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getLocation ()
    {
        return location;
    }

    public void setLocation (String location)
    {
        this.location = location;
    }

    public String getDescription ()
    {
        return description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [startTime = "+startTime+", duration = "+duration+", title = "+title+", location = "+location+", description = "+description+"]";
    }
}