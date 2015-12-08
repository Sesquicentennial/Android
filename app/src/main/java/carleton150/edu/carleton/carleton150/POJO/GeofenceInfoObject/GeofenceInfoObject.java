package carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceInfoObject
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
        String contentString = "";
        for(int i = 0; i<content.length; i++){
            contentString += content[i].toString();
        }
        return "GeofenceInfoObject [content = "+contentString+"]";
    }
}

