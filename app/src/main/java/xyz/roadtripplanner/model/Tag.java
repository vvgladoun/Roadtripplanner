package xyz.roadtripplanner.model;

/**
 * Tag object
 *
 * @author xyz
 */
public class Tag {
    private int mId;
    private String mName;

    public Tag() {
    }

    public Tag(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
