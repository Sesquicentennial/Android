package carleton150.edu.carleton.carleton150.POJO.GeofenceInfoObject;

/**
 *
 */
public class GeofenceInfoContent
{
    public final String TYPE_IMAGE = "image";
    public final String TYPE_TEXT = "text";


    private String summary;

    private String geofence_id;

    private String info_id;

    private String year;

    private String caption;

    private String desc;

    private String contentType;

    private String data;

    private String name;

    private String type;

    private boolean expanded = false;


    /*
    These are for the Memories only
     */

    private String timestamp;

    private String uploader;

    private String image;


    public String getTimestamp() {
        return timestamp;
    }

    public String getUploader() {
        return uploader;
    }

    public String getImage() {
        return image;
    }

    public String getSummary ()
    {
        return summary;
    }

    public void setSummary (String summary)
    {
        this.summary = summary;
    }

    public String getGeofence_id() {
        return geofence_id;
    }

    public void setGeofence_id(String geofence_id) {
        this.geofence_id = geofence_id;
    }

    public String getInfo_id() {
        return info_id;
    }

    public void setInfo_id(String info_id) {
        this.info_id = info_id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getData ()
    {
        return data;
    }

    public void setData (String data)
    {
        this.data = data;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public String toString()
    {
        try {
            return "Content [summary = " + summary + ", geofence_id = " + geofence_id + ", data = data"  + ", geofences = " + name + ", type = " + type + "]";
        } catch(NullPointerException e){
            e.printStackTrace();
            return "null content";
        }
    }
}
