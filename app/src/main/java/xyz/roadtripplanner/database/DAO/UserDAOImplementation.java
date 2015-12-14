package xyz.roadtripplanner.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.UserTable;
import xyz.roadtripplanner.model.User;

/**
 * DAO implementation for users
 *
 * @author xyz
 */
public class UserDAOImplementation implements BaseColumns {
    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context app's context
     */
    public UserDAOImplementation(Context context){
        mDbHelper = DbHelper.getInstance(context);
    }

    /**
     * open DB connection
     * @throws SQLException
     */
    public void open() throws SQLException {
        db = mDbHelper.getWritableDatabase();
    }

    /**
     * close DB connection
     */
    public void close() {
        db.close();
    }

    public boolean insertUser(User user) {
        try {
            ContentValues values = new ContentValues();

            values.put(UserTable.COLUMN_USERNAME, user.getLogin());
            values.put(UserTable.COLUMN_EMAIL, user.getEmail());
            values.put(UserTable.COLUMN_FULLNAME, user.getFullName());
            values.put(UserTable.COLUMN_ISADMIN, user.getIsAdmin());
            values.put(_ID, user.getId());

            //insert row
            db.insert(UserTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            //Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * get max id from user table
     *
     * @return maximum uid from the datastore
     */
    public int getMaxId(){
        int maxId = 0;

        String selectQuery = "SELECT MAX(_id) as maxId FROM " + UserTable.TABLE_NAME;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                maxId = cursor.getInt(cursor.getColumnIndexOrThrow("maxId"));
            }
            cursor.close();
        }catch(Exception e){
            Log.d("USER", e.toString());
        }
        return maxId;
    }

    /**
     * General method to make request to DB
     *
     * @param whereStatement - condition
     * @return arrayList of users
     */
    public ArrayList<User> findUser(String whereStatement){
        ArrayList<User> userList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + UserTable.TABLE_NAME + whereStatement;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getInt(0));
                    user.setLogin(cursor.getString(1));
                    user.setEmail(cursor.getString(2));
                    user.setFullName(cursor.getString(3));
                    user.setIsAdmin(cursor.getInt(4));

                    // Adding attraction to list
                    userList.add(user);
                } while (cursor.moveToNext());
            }
            db.close();
            cursor.close();
        }catch(Exception e){
            Log.d("Exception Get", e.getMessage());
        }
        return userList;
    }

    /**
     * Get user by ID
     *
     * @param id - user ID
     * @return user object
     */

    public User findUserById(int id) {
        List<User> userList = findUser(" WHERE _id = " + id);
        if (userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

}
