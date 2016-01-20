package carleton150.edu.carleton.carleton150.Adapters;

/**
 * Created by haleyhinze on 12/8/15.
 *
 * Class to store information about a scavenger hunt
 */
public class QuestInfo {

    private String title;
    private String description;
    private String creator;

    //width of card in recyclerview
    private int width;

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     *
     * @param creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     *
     * @param width
     */
    public void setWidth(int width){
        this.width = width;
    }

    /**
     *
     * @return width of cardview
     */
    public int getWidth(){
        return this.width;
    }
}
