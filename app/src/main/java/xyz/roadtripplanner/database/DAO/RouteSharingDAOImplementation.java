package xyz.roadtripplanner.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.sql.SQLException;

import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.RouteSharingTable;

/**
 * DAO implementation for route sharing
 *
 * @author xyz
 */
public class RouteSharingDAOImplementation implements BaseColumns {
    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context - app's context
     */
    public RouteSharingDAOImplementation(Context context){
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

    /**
     * Insert new row to the datastore
     *
     * @param id - row's uid
     * @param userId - user's uid
     * @param routeId route's uid
     * @return status flag
     */
    public boolean insertRouteSharing(int id, int userId, int routeId) {
        try {
            ContentValues values = new ContentValues();

            values.put(RouteSharingTable.COLUMN_ROUTEID, routeId);
            values.put(RouteSharingTable.COLUMN_USERID, userId);
            values.put(_ID, id);

            //insert row
            db.insert(RouteSharingTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            //Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }


    /**
     * Get max id from routesharing table
     *
     * @return maximum uid from the datastore
     */
    public int getMaxId(){
        int maxId = 0;

        String selectQuery = "SELECT MAX(_id) as maxId FROM " + RouteSharingTable.TABLE_NAME;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                maxId = cursor.getInt(cursor.getColumnIndexOrThrow("maxId"));
            }
            cursor.close();
        }catch(Exception e){
            Log.d("ROUTEPOINTS", e.toString());
        }
        return maxId;
    }
}
