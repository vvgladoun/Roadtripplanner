package xyz.roadtripplanner.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.RoutePointTable;
import xyz.roadtripplanner.model.RoutePoint;

/**
 * DAO implementation for route points
 *
 * @author xyz
 */
public class RoutePointDAOImplementation implements BaseColumns {
    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context - app's context
     */
    public RoutePointDAOImplementation(Context context){
        mDbHelper = DbHelper.getInstance(context);
    }

    /**
     * open DB connection
     *
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
     * insert new route point row to the datastor
     *
     * @param id row uid
     * @param plId place's uid
     * @param routeId route's uid
     * @param order place's order number in the route
     * @return status flag
     */
    public boolean insertRoutePoint(int id, int plId, int routeId, int order) {
        try {
            ContentValues values = new ContentValues();

            values.put(RoutePointTable.COLUMN_PLACEID, plId);
            values.put(RoutePointTable.COLUMN_ROUTEID, routeId);
            values.put(RoutePointTable.COLUMN_ORDERNUM, order);
            values.put(_ID, id);

            //insert row
            db.insert(RoutePointTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            //Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * General method to create requests to DB
     *
     * @param whereStatement filter string
     * @return array list of the objects
     */
    public ArrayList<RoutePoint> findRoutePoint(String whereStatement){

        ArrayList<RoutePoint> placeList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + RoutePointTable.TABLE_NAME + whereStatement;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    RoutePoint rPoint = new RoutePoint();
                    rPoint.setId(cursor.getInt(0));
                    rPoint.setPlaceId(cursor.getInt(2));
                    rPoint.setRouteId(cursor.getInt(1));
                    rPoint.setOrderNumber(cursor.getInt(3));

                    // Adding attraction to list
                    placeList.add(rPoint);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch(Exception e){
            Log.d("ROUTEPOINTS", e.toString());
        }
        return placeList;
    }

    /**
     * Get routepoints by route Id
     *
     * @param routeId route's uid
     * @return array list of data objects
     */
    public ArrayList<RoutePoint> findRoutePointsByRouteId(int routeId){
        ArrayList<RoutePoint> rPointList = findRoutePoint(" WHERE " +
                RoutePointTable.COLUMN_ROUTEID + " = " + routeId);
        if (rPointList.size() > 0) {
            return rPointList;
        }
        return null;
    }

    /**
     * Get Max id in routepoint table
     *
     * @return max uid from the datastore
     */
    public int getMaxId(){
        int maxId = 0;

        String selectQuery = "SELECT MAX(_id) as maxId FROM " + RoutePointTable.TABLE_NAME;
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
