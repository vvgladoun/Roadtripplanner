package xyz.roadtripplanner.model;

import com.google.gson.annotations.SerializedName;

/**
 * route object
 *
 * @author XYZ
 */
public class Route {
    @SerializedName("id")
    private int mId;
    @SerializedName("route_name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image_path")
    private String mImagePath;

    public Route() {
    }

    public Route(String imagePath, String description, String name, int id) {
        mImagePath = imagePath;
        mDescription = description;
        mName = name;
        mId = id;
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

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
