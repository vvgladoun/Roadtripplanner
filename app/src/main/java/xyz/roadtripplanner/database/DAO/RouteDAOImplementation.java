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
import xyz.roadtripplanner.database.RouteTable;
import xyz.roadtripplanner.model.Route;

/**
 * Route DAO implementation
 * Functions for operations with DB
 *
 * @author xyz
 */
public class RouteDAOImplementation implements BaseColumns{
    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context app's context
     */
    public RouteDAOImplementation(Context context){
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
     *
     * @param route route object
     * @return status flag
     */
    public boolean insertRoute(Route route) {
        try {
            ContentValues values = new ContentValues();

            values.put(RouteTable.COLUMN_ROUTENAME, route.getName());
            values.put(RouteTable.COLUMN_DESC, route.getDescription());
            values.put(RouteTable.COLUMN_IMAGEPATH, route.getImagePath());
            values.put(_ID, route.getId());

            //insert row
            db.insert(RouteTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            //Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Get max route ID in DB
     *
     * @return maximum uid in the datastore
     */
    public int getMaxId(){
        int maxId = 0;

        String selectQuery = "SELECT MAX(_id) as maxId FROM " + RouteTable.TABLE_NAME;
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

    /**
     * General method to generate request to DB
     * @param whereStatement condition
     * @return  list of routes
     */
    public ArrayList<Route> findRoute(String whereStatement){
        ArrayList<Route> routeList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + RouteTable.TABLE_NAME + whereStatement;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Route route = new Route();
                    route.setId(cursor.getInt(0));
                    route.setName(cursor.getString(1));
                    route.setDescription(cursor.getString(2));
                    route.setImagePath(cursor.getString(3));


                    // Adding attraction to list
                    routeList.add(route);
                } while (cursor.moveToNext());
            }
            db.close();
            cursor.close();
        }catch(Exception e){
            Log.d("Exception Get", e.getMessage());
        }
        return routeList;
    }

    /**
     * Get address by its ID
     * @param id ID or route
     * @return route object
     */
    public Route findRouteById(int id) {
        List<Route> routeList = findRoute(" WHERE _id = " + id);
        if (routeList.size() > 0) {
            return routeList.get(0);
        }
        return null;
    }

}
