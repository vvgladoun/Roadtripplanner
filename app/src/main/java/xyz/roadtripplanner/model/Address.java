package xyz.roadtripplanner.model;

/**
 * Address link objects
 *
 * @author xyz
 */
public class Address {

    private double mLatitude;
    private double mLongitude;
    private String mCountry;
    private String mCity;
    private String mSuburb;
    private String mStreet;
    private String mBuilding;
    private String mPostIndex;
    private int mId;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }


    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getSuburb() {
        return mSuburb;
    }

    public void setSuburb(String suburb) {
        mSuburb = suburb;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getBuilding() {
        return mBuilding;
    }

    public void setBuilding(String building) {
        mBuilding = building;
    }

    public String getPostIndex() {
        return mPostIndex;
    }

    public void setPostIndex(String postIndex) {
        mPostIndex = postIndex;
    }
}
