package xyz.roadtripplanner.model;

/**
 * Place image link objects
 *
 * @author xyz
 */
public class PlaceImage {

    private String mImagePath;
    private int isDefault;
    private int mPlaceId;
    private int mId;


    public int getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(int placeId) {
        this.mPlaceId = placeId;
    }

    public int isDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getImagePath() {
        return mImagePath;

    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

}
