package carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haleyhinze on 11/9/15.
 */
public class GeofenceInfoObject
{
    private HashMap<String, GeofenceInfoContent[]> content;

    public HashMap<String, GeofenceInfoContent[]> getContent ()
    {
        return content;
    }

    public void setContent (HashMap<String, GeofenceInfoContent[]> content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        String contentString = "";
        for(Map.Entry<String, GeofenceInfoContent[]> e : content.entrySet()){
            contentString += "{" + e.getKey() + ": [";
            for(int i = 0; i < e.getValue().length; i++){
                contentString += "{" + e.getValue()[i].toString() + "}";
            }
            contentString += "]}";
        }
        return "GeofenceInfoObject [content = "+contentString+"]";
    }
}

