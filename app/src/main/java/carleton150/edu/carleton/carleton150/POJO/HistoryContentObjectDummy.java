package carleton150.edu.carleton.carleton150.POJO;

import android.media.Image;


/**
 * Created by haleyhinze on 1/26/16.
 */
public class HistoryContentObjectDummy {

    private byte[] image;
    private String text;
    private String date;

    //0 = image, 1 = text
    private int type;


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
