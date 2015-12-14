package xyz.roadtripplanner.model;

/**
 * Plce-tag link objects
 *
 * @author xyz
 */
public class PlaceTag {

    private String mTagName;
    private boolean isPrimary;

    public PlaceTag() {
    }

    public PlaceTag(boolean isPrimary, String tagName) {
        this.isPrimary = isPrimary;
        mTagName = tagName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getTagName() {
        return mTagName;
    }

    public void setTagName(String tagName) {
        mTagName = tagName;
    }
}
