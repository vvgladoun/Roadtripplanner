package xyz.roadtripplanner.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Methods to work with preferences
 * @author xyz
 */
public class PreferencesManagement {

    // credential constants
    public static final String PREF_NAME = "preferences";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_LOGGED = "logged";
    public static final String PREF_UID = "uid";
    public static final String PREF_EMAIL = "email";
    public static final String PREF_DATALOADED = "dataloaded";

    /**
     * save users preferences if signed in
     *
     * @param context - app's context
     * @param uid - user's id
     * @param usernameValue
     * @param passwordValue
     * @param email
     */
    public static void savePreferences(Context context, int uid, String usernameValue,
                                       String passwordValue, String email) {
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // save credentials
        editor.putString(PREF_USERNAME, usernameValue);
        editor.putString(PREF_PASSWORD, passwordValue);
        editor.putString(PREF_EMAIL, email);
        editor.putInt(PREF_UID, uid);
        editor.putBoolean(PREF_LOGGED, true);
        editor.apply();
    }

    /**
     * remove user's credentials from preferences
     *
     * @param context - app's context
     */
    public static void clearPreferences(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // change cr-s to empty values
        editor.putString(PREF_USERNAME, "");
        editor.putString(PREF_PASSWORD, "");
        editor.putString(PREF_EMAIL, "");
        editor.putInt(PREF_UID, -1);
        editor.putBoolean(PREF_LOGGED, false);
        editor.apply();
    }

    /**
     * get username
     *
     * @param context - app's context
     * @return username
     */
    public static String getUsername(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(PREF_USERNAME, "");
    }

    /**
     * get user's email
     *
     * @param context - app's context
     * @return user's email
     */
    public static String getEmail(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(PREF_EMAIL, "");
    }

    /**
     * get user's password
     *
     * @param context - app's context
     * @return user's password
     */
    public static String getPassword(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(PREF_PASSWORD, "");
    }


    /**
     *
     * @param context - app's context
     * @return true if user logged in
     */
    public static boolean isLogged(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(PREF_LOGGED, false);
    }

    /**
     * get user's id (if not logged in - -1)
     *
     * @param context - app's context
     * @return user's id
     */
    public static int getUserId(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getInt(PREF_UID, -1);
    }

    /**
     * Set parameter-flag data loaded (true if initial data load was successfull)
     *
     * @param context - current app's context
     * @param isLoaded - true if data has been loaded
     */
    public static void setDataLoaded(Context context, boolean isLoaded){
        // get preferences
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // save status
        editor.putBoolean(PREF_DATALOADED, isLoaded);
        // apply changes
        editor.apply();
    }

    /**
     * Get current status - if data has been initially loaded
     *
     * @param context - current app's context
     * @return - flag, true if data has been loaded
     */
    public static boolean getDataLoaded(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(PREF_DATALOADED, false);
    }
}
