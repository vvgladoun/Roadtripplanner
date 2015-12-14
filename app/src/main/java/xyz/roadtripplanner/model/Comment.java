package xyz.roadtripplanner.model;

/**
 * Place-comment link objects
 *
 * @author xyz
 */
public class Comment {
    private String mCommentText;
    private int mPlaceId;
    private int mUserId;
    private int mId;
    private String mDate;

    public Comment() {
    }

    public Comment(String commentText, String date, int id, int userId, int placeId) {
        mCommentText = commentText;
        mDate = date;
        mId = id;
        mUserId = userId;
        mPlaceId = placeId;
    }

    public String getCommentText() {
        return mCommentText;
    }

    public void setCommentText(String commentText) {
        mCommentText = commentText;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public int getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(int placeId) {
        mPlaceId = placeId;
    }
}
