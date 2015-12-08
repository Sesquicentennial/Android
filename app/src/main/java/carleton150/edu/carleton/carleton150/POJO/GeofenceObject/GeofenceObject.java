package carleton150.edu.carleton.carleton150.POJO.GeofenceObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceObject
{
    private Content[] content;

    public Content[] getContent ()
    {
        return content;
    }

    public void setContent (Content[] content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "GeofenceObject [content = "+content+"]";
    }
}