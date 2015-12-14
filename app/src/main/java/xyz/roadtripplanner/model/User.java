package xyz.roadtripplanner.model;

/**
 * User model
 *
 * @author xyz
 */
public class User {
    private int mId;
    private String mEmail;
    private String mFullName;
    private String mLogin;
    private int isAdmin;

    public User(int id, String email, String fullName, String login) {
        mId = id;
        mEmail = email;
        mFullName = fullName;
        mLogin = login;
    }

    public User() {
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }
}
