package xyz.roadtripplanner.places;

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
import xyz.roadtripplanner.database.TagTable;
import xyz.roadtripplanner.model.Place;
import xyz.roadtripplanner.utilities.Toolbox;

/**
 * Manager class to access places' data
 *
 * @author xyz
 */
public class PlacesManager {

    public static String TAG = PlacesManager.class.getSimpleName();

    /**
     * Get an array list of route's points
     *
     * @param context - context to get DB helper instance
     * @param routeId - route's uid
     */
    public static ArrayList<Place> getArrayPlaces(Context context, int routeId) {
        ArrayList<Place> places = new ArrayList<>();

        // create connection
        SQLiteDatabase dbConnection = DbHelper.getInstance(context).getWritableDatabase();

        //set filter's by route id (if defined)
        String routeFilterJoin = "";
        String routeFilterWhere = "";

        if (routeId > 0) {
            routeFilterJoin = " LEFT JOIN " + RoutePointTable.TABLE_NAME
                    + " rpt ON rpt." + RoutePointTable.COLUMN_ROUTEID + " = " + routeId
                    + " AND rpt." + RoutePointTable.COLUMN_PLACEID + " = pt." + PlaceTable._ID;
            routeFilterWhere = " WHERE rpt." + RoutePointTable._ID + " IS NULL ";
        }

        // get cursor
        String selectPlaces = "SELECT pt." + PlaceTable._ID + " id, pt." + PlaceTable.COLUMN_SHORTDESC
                + " name, COALESCE(MAX(it." + ImageTable.COLUMN_PATH
                + "),0) imagepath, COALESCE(MAX(tt." + TagTable.COLUMN_TAGNAME
                + "),'') tag FROM " + PlaceTable.TABLE_NAME + " pt "

                + routeFilterJoin

                + " LEFT JOIN " + ImageTable.TABLE_NAME
                + " it ON it." + ImageTable.COLUMN_ISDEFAULT + " = 1 "
                + " AND it." + ImageTable.COLUMN_PLACEID + " = pt." + PlaceTable._ID

                + " LEFT JOIN " + PlaceTagTable.TABLE_NAME
                + " ptt ON ptt." + PlaceTagTable.COLUMN_PLACEID + " = pt." + PlaceTable._ID
                + " AND ptt." + PlaceTagTable.COLUMN_ISPRIMARY + " = 1 "
                + " LEFT JOIN " + TagTable.TABLE_NAME
                + " tt ON tt." + TagTable._ID + " = ptt." + PlaceTagTable.COLUMN_TAGID

                + routeFilterWhere

                + " GROUP BY pt." + PlaceTable._ID + ", pt." + PlaceTable.COLUMN_SHORTDESC;

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
