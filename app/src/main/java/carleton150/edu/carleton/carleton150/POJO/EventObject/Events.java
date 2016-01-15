package carleton150.edu.carleton.carleton150.POJO.EventObject;

/**
 * Created by haleyhinze on 1/14/16.
 */
public class Events {


    private EventContent[] content;

    public EventContent[] getContent ()
    {
        return content;
    }

    public void setContent (EventContent[] content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [content = "+content+"]";
    }

}
