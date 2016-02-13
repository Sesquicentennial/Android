package carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject;

/**
 * Created by haleyhinze on 2/12/16.
 */
public class MemoriesContent {

    private GeofenceInfoContent[] content;

    public GeofenceInfoContent[] getContent ()
    {
        return content;
    }

    public void setContent (GeofenceInfoContent[] content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "MemoriesContent [content = "+content+"]";
    }
}
