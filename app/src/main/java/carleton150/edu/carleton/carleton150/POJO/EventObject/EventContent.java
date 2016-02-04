package carleton150.edu.carleton.carleton150.POJO.EventObject;

public class EventContent
{
    private String startTime;

    private String duration;

    private String title;

    private String location;

    private String description;

    private boolean isExpanded = false;

    public String getStartTime ()
    {
        return startTime;
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