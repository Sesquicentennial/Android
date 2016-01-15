package carleton150.edu.carleton.carleton150.POJO.GeofenceObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceObject
{
    private GeofenceObjectContent[] content;

    public GeofenceObjectContent[] getContent ()
    {
        return content;
    }

    public void setContent (GeofenceObjectContent[] content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "GeofenceObject [content = "+content+"]";
    }
}