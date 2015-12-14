package xyz.roadtripplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import xyz.roadtripplanner.model.RouteSharing;

/**
 * Static var-s and methods for table,
 * which contains which routes shared between which users
 *
 * @author xyz
 */
public class RouteSharingTable implements BaseColumns {

    public static final String TAG = RouteSharingTable.class.getSimpleName();
    public static final String TABLE_NAME = "tbRouteSharing";

    // table's columns
    public static final String COLUMN_ROUTEID = "route_id";
    public static final String COLUMN_USERID = "user_id";

    /** create user-route table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " integer primary key unique,"
                + COLUMN_ROUTEID + " integer,"
                + COLUMN_USERID + " integer )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    /**
     * Load array of route sharing objects to the database
     *
     * @param context - app's context
     * @param routeSharings - array list of route sharing objects
     * @return - status. True if new rows have been loaded
     */
    public static boolean batchLoadRouteSharing(Context context
            , ArrayList<RouteSharing> routeSharings, boolean clearTable) {
        // check if input is empty
        if (routeSharings.isEmpty()) {
            return false;
        }
        //open db connection
        SQLiteDatabase dbConnection = DbHelper.getInstance(context)
                .getWritableDatabase();

        boolean loaded = false;

        try {
            if (clearTable){
                dbConnection.execSQL("DELETE FROM " + TABLE_NAME);
            }

            // insert new rows
            for (RouteSharing routeShare : routeSharings) {
                ContentValues values = new ContentValues();

                values.put(_ID, routeShare.getId());
                values.put(COLUMN_ROUTEID, routeShare.getRouteid());
                values.put(COLUMN_USERID, routeShare.getUserid());

                //insert row
                dbConnection.insert(TABLE_NAME, null, values);
            }

            loaded = true;
        } catch (Exception e) {
            Log.e(TAG, "Error during route share loading : " + e.getMessage());
        }
        //close connection
        dbConnection.close();

        return loaded;
    }

}
