package xyz.roadtripplanner.routes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.ImageTable;
import xyz.roadtripplanner.database.PlaceTable;
import xyz.roadtripplanner.database.PlaceTagTable;
import xyz.roadtripplanner.database.RoutePointTable;
import xyz.roadtripplanner.database.RouteSharingTable;
import xyz.roadtripplanner.database.RouteTable;
import xyz.roadtripplanner.database.TagTable;
import xyz.roadtripplanner.database.UserTable;
import xyz.roadtripplanner.model.Place;
import xyz.roadtripplanner.model.Route;
import xyz.roadtripplanner.utilities.Toolbox;

/**
 * Manager to access routes data
 *
 * @author xyz
 */
final class RoutesManager {

    public static String TAG = RoutesManager.class.getSimpleName();


    /**
     * Returns cursor with routes from database
     *
     * @param dbConnection - established connection
     * @param userId - user's Id in the database
     * @return cursor with routes' data
     */
    public static Cursor createCursorAllRoutes(SQLiteDatabase dbConnection, int userId) {

        String selectRoutes = "SELECT rt." + RouteTable._ID + ", rt." + RouteTable.COLUMN_ROUTENAME
                + ", rt." + RouteTable.COLUMN_DESC + ", rt." + RouteTable.COLUMN_IMAGEPATH
                + " FROM " + RouteTable.TABLE_NAME + " rt "
                + " INNER JOIN " + UserTable.TABLE_NAME
                + " ut ON ut." + UserTable._ID + " = " + userId
                + " LEFT JOIN " + RouteSharingTable.TABLE_NAME
                + " rst ON rt." + RouteTable._ID + " = rst." + RouteSharingTable.COLUMN_ROUTEID
                + " AND ut." + UserTable._ID + " = rst." + RouteSharingTable.COLUMN_USERID
                + " WHERE (ut." + UserTable.COLUMN_ISADMIN + " > 0) OR (rst."
                + RouteSharingTable._ID + " IS NOT NULL)";

        if (Toolbox.DEBUG) {
            Log.d(TAG, "SELECT ROUTES: " + selectRoutes);
        }


        //return cursor
        return dbConnection.rawQuery(selectRoutes, null);
    }

    /**
     * Returns array list  with routes from database
     *
     * @param context - current context
     * @param userId - user's id
     * @return list of routes
     */
    public static ArrayList<Route> getAllRoutes(Context context, int userId){

        ArrayList<Route> routes = new ArrayList<>();

        // create connection
        SQLiteDatabase dbConnection = DbHelper.getInstance(context).getWritableDatabase();
        // get cursor
        Cursor cursorRoutes = createCursorAllRoutes(dbConnection, userId);
        // get data from cursor
        while(cursorRoutes.moveToNext()){
            int uid = cursorRoutes.getInt(cursorRoutes.getColumnIndexOrThrow(RouteTable._ID));
            String name = cursorRoutes.getString(cursorRoutes.getColumnIndexOrThrow(RouteTable.COLUMN_ROUTENAME));
            String desc = cursorRoutes.getString(cursorRoutes.getColumnIndexOrThrow(RouteTable.COLUMN_DESC));
            String imagepath = cursorRoutes.getString(cursorRoutes.getColumnIndexOrThrow(RouteTable.COLUMN_IMAGEPATH));
            // create new route to array list
            Route route = new Route(imagepath, desc, name, uid);
            if (Toolbox.DEBUG) {
                Log.d(TAG, "route object: " + route.getName() + ", " + route.getDescription()
                        + ", " + route.getImagePath() + ", " + route.getId());
            }
            routes.add(route);
        }
        // close connection
        dbConnection.close();

        return routes;
    }

    /**
     * Remove route from database
     *
     * @param context - context to get DB helper instance
     * @param routeId - route's uid
     */
    public static void removeRoute(Context context, int routeId){
        // prepare delete statements
        String deleteRoutes = "DELETE FROM " + RouteTable.TABLE_NAME + " WHERE "
                + RouteTable._ID + " = " + routeId;
        String deleteRouteSharing = "DELETE FROM " + RouteSharingTable.TABLE_NAME + " WHERE "
                + RouteSharingTable.COLUMN_ROUTEID + " = " + routeId;

        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        // execute delete route statement
        db.execSQL(deleteRoutes);
        // execute delete sharing statement
        db.execSQL(deleteRouteSharing);
        // close connection
        db.close();
    }


    /**
     * Remove route point from database
     *
     * @param context - context to get DB helper instance
     * @param routeId - route's uid
     * @param placeId - place's uid
     */
    public static void removePlace(Context context, int routeId, int placeId){
        // prepare delete statements
        String deleteRoutePoint = "DELETE FROM " + RoutePointTable.TABLE_NAME + " WHERE "
                + RoutePointTable.COLUMN_ROUTEID + " = " + routeId
                + " AND " + RoutePointTable.COLUMN_PLACEID + " = " + placeId;

        // open connection
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        // execute delete route point statement
        db.execSQL(deleteRoutePoint);
        // close connection
        db.close();
    }

    /**
     * Move route point position in route
     * stored in the database
     *
     * @param context - context to get DB helper instance
     * @param routeId - route's uid
     * @param sourcePlaceId - start place's uid
     * @param targetPlaceId - end place's uid
     */
    public static void movePlace(Context context, int routeId, int sourcePlaceId, int targetPlaceId){

        // open connection
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        //get source and target order number
        int sourceNumber = getOrderNumberById(db, routeId, sourcePlaceId);
        int targetNumber = getOrderNumberById(db, routeId, targetPlaceId);

        String orderNumColumn = RoutePointTable.COLUMN_ORDERNUM;

        if (sourceNumber == targetNumber) {
            db.close();
        } else {
            // update source's order num to target's
            String sqlUpdateSource = "UPDATE " + RoutePointTable.TABLE_NAME + " SET "
                    + orderNumColumn + " = " + targetNumber + " WHERE "
                    + RoutePointTable.COLUMN_ROUTEID + " = " + routeId + " AND "
                    + RoutePointTable.COLUMN_PLACEID + " = " + sourcePlaceId;
            db.execSQL(sqlUpdateSource);
            // shift other route's points
            if (sourceNumber > targetNumber) {
                // move point up (in asc order)
                String sqlShift = "UPDATE " + RoutePointTable.TABLE_NAME
                        + " SET " + orderNumColumn + " = " + orderNumColumn + " + 1"
                        + " WHERE " + RoutePointTable.COLUMN_ROUTEID + " = " + routeId
                        + " AND " + orderNumColumn + " >= "
                        + targetNumber + " AND order_number <= " + sourceNumber
                        + " AND (NOT (market_id = " + sourcePlaceId + "))";
                db.execSQL(sqlShift);

            } else {

                // move points down
                String sqlShift = "UPDATE " + RoutePointTable.TABLE_NAME
                        + " SET " + orderNumColumn + " = " + orderNumColumn + " - 1"
                        + " WHERE " + RoutePointTable.COLUMN_ROUTEID + " = " + routeId
                        + " AND " + orderNumColumn + " >= "
                        + sourceNumber + " AND order_number <= " + targetNumber
                        + " AND (NOT (market_id = " + sourcePlaceId + "))";
                db.execSQL(sqlShift);
            }

            // close connection
            db.close();
        }
    }

    /**
     * Return place's order num in route
     *
     * @param db - sqlite connection
     * @param routeid - route's id
     * @param placeId - place's id
     * @return order number
     */
    public static int getOrderNumberById(SQLiteDatabase db, int routeid, int placeId){

        int orderNum = 0;

        String selectNum = "SELECT " + RoutePointTable.COLUMN_ORDERNUM
                + " FROM " + RoutePointTable.TABLE_NAME + " WHERE "
                + RoutePointTable.COLUMN_ROUTEID + " = " + routeid
                + " AND " + RoutePointTable.COLUMN_PLACEID + " = " + placeId;


        //get data from database
        Cursor cursor = db.rawQuery(selectNum, null);
        if(cursor.moveToNext()) {
            // parse cursor row
            orderNum = cursor.getInt(cursor.getColumnIndexOrThrow(RoutePointTable.COLUMN_ORDERNUM));
        }
        // close cursor
        cursor.close();
        return  orderNum;
    }



    /**
     * Get an array list of route's points
     *
     * @param context - context to get DB helper instance
     * @param routeId - route's uid
     */
    public static ArrayList<Place> getArrayRoutePoints(Context context, int routeId) {
        ArrayList<Place> places = new ArrayList<>();

        // create connection
        SQLiteDatabase dbConnection = DbHelper.getInstance(context).getWritableDatabase();
        // get cursor
        String selectPlaces = "SELECT pt." + PlaceTable._ID + " id, pt." + PlaceTable.COLUMN_SHORTDESC
                + " name, rpt." + RoutePointTable.COLUMN_ORDERNUM + " ordernum, COALESCE(MAX(it." + ImageTable.COLUMN_PATH
                + "),0) imagepath, COALESCE(MAX(tt." + TagTable.COLUMN_TAGNAME
                + "),'') tag FROM " + PlaceTable.TABLE_NAME + " pt "
                + " INNER JOIN " + RoutePointTable.TABLE_NAME
                + " rpt ON rpt." + RoutePointTable.COLUMN_ROUTEID + " = " + routeId
                + " AND rpt." + RoutePointTable.COLUMN_PLACEID + " = pt." + PlaceTable._ID
                + " LEFT JOIN " + ImageTable.TABLE_NAME
                + " it ON it." + ImageTable.COLUMN_ISDEFAULT + " = 1 "
                + " AND it." + ImageTable.COLUMN_PLACEID + " = pt." + PlaceTable._ID
                + " LEFT JOIN " + PlaceTagTable.TABLE_NAME
                + " ptt ON ptt." + PlaceTagTable.COLUMN_PLACEID + " = pt." + PlaceTable._ID
                + " AND ptt." + PlaceTagTable.COLUMN_ISPRIMARY + " = 1 "
                + " LEFT JOIN " + TagTable.TABLE_NAME
                + " tt ON tt." + TagTable._ID + " = ptt." + PlaceTagTable.COLUMN_TAGID
                + " GROUP BY pt." + PlaceTable._ID + ", pt." + PlaceTable.COLUMN_SHORTDESC
                + ", rpt." + RoutePointTable.COLUMN_ORDERNUM
                + " ORDER BY rpt." + RoutePointTable.COLUMN_ORDERNUM;

        // for debugging
        if (Toolbox.DEBUG) {
            Log.d(TAG, "SELECT PLACES: " + selectPlaces);
        }
        //return cursor
        Cursor cursorPlaces = dbConnection.rawQuery(selectPlaces, null);
        // get data from cursor
        while(cursorPlaces.moveToNext()){
            // parse cursor row
            int uid = cursorPlaces.getInt(cursorPlaces.getColumnIndexOrThrow("id"));
            String name = cursorPlaces.getString(cursorPlaces.getColumnIndexOrThrow("name"));
            String imagePath = cursorPlaces.getString(cursorPlaces.getColumnIndexOrThrow("imagepath"));
            String tag = cursorPlaces.getString(cursorPlaces.getColumnIndexOrThrow("tag"));
            // create and add new place object to the array list
            Place place = new Place(name, "", uid, 0, 0, tag, imagePath);
            if (Toolbox.DEBUG) {
                Log.d(TAG, "place object: " + place.getShortDescription() + ", "
                        + place.getId() + ", " + place.getTag() + ", " + place.getImagePath());
            }
            places.add(place);
        }
        // close connection
        cursorPlaces.close();
        dbConnection.close();

        return places;

    }


}
