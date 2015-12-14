package xyz.roadtripplanner.model;

/**
 * Place object
 *
 * @author xyz
 */
public class Place {

    // basic fields
    private String mShortDescription;
    private String mFullDescription;
    private int mAddressId;
    private int mId;
    private int mUserId;
    // additional fields
    private String mImagePath;
    private String mTag;


    /**
     * default constructor
     */
    public Place() {
    }

    /**
     * Constructor with basic fields
     *
     * @param shortDescription place's name
     * @param fullDescription place's description
     * @param id place's uid
     * @param addressId uid of place's address
     * @param userId user originally created this place
     */
    public Place(String shortDescription, String fullDescription, int id, int addressId, int userId) {
        this(shortDescription, fullDescription, id, addressId, userId, "", "");
    }

    /**
     * Constructor with basic fields
     *
     * @param shortDescription place's name
     * @param fullDescription place's description
     * @param id place's uid
     * @param addressId uid of place's address
     * @param userId user originally created this place
     */
    public Place(String shortDescription, String fullDescription, int id,
                 int addressId, int userId, String tag, String imagePath) {



        mId = id;
        mAddressId = addressId;
        mUserId = userId;
        mShortDescription = shortDescription;
        mFullDescription = fullDescription;
        mTag = tag;
        mImagePath = imagePath;
    }

    /**
     *
     * @return user's uid
     */
    public int getUserId() {
        return mUserId;
    }

    /**
     *
     * @param userId user's uid
     */
    public void setUserId(int userId) {
        mUserId = userId;
    }

    /**
     *
     * @return place's uid
     */
    public int getId() {
        return mId;
    }

    /**
     *
     * @param id place's uid
     */
    public void setId(int id) {
        mId = id;
    }

    public int getAddressId() {
        return mAddressId;
    }

    public void setAddressId(int addressId) {
        mAddressId = addressId;
    }


    public String getShortDescription() {
        return mShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        mShortDescription = shortDescription;
    }

    public String getFullDescription() {
        return mFullDescription;
    }

    public void setFullDescription(String fullDescription) {
        mFullDescription = fullDescription;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String mTag) {
        this.mTag = mTag;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }
}
