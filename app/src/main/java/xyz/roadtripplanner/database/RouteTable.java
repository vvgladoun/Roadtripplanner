package xyz.roadtripplanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import xyz.roadtripplanner.model.Route;
import xyz.roadtripplanner.utilities.NetworkHelper;
import xyz.roadtripplanner.utilities.Toolbox;


/**
 * Static var-s and methods for Route table
 *
 * @author xyz
 */
public class RouteTable implements BaseColumns {


    public static final String TAG = RouteTable.class.getSimpleName();
    public static final String TABLE_NAME = "tbRoute";


    // table's columns
    public static final String COLUMN_ROUTENAME = "route_name";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_IMAGEPATH = "image_path";

    /** create route table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_ROUTENAME + " TEXT DEFAULT '',"
                + COLUMN_DESC + " TEXT DEFAULT '',"
                + COLUMN_IMAGEPATH + " TEXT DEFAULT '' )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    /**
     * Load array of route's objects to the database
     *
     * @param context - app's context
     * @param routes - array list of routes
     * @return - status. True if new routes has been loaded
     */
    public static boolean loadRoutes(Context context
            , ArrayList<Route> routes, boolean clearTable) {
        if (routes.size() == 0) {
            return false;
        }

        //open db connection
        SQLiteDatabase dbConnection = DbHelper.getInstance(context)
                .getWritableDatabase();

        boolean loaded = false;

        try {
            // clear table if needed
            if (clearTable){
                dbConnection.execSQL("DELETE FROM " + TABLE_NAME);
            }
            // add to batch insert
            for (Route route : routes) {
                ContentValues values = new ContentValues();

                values.put(RouteTable.COLUMN_ROUTENAME, route.getName());
                values.put(RouteTable.COLUMN_DESC, route.getDescription());
                values.put(RouteTable.COLUMN_IMAGEPATH, route.getImagePath());
                values.put(_ID, route.getId());
                //insert row
                dbConnection.insert(RouteTable.TABLE_NAME, null, values);
            }
            loaded = true;
        } catch (Exception e) {
            Log.e(TAG, "Error during routes loading : " + e.getMessage());
        }

        dbConnection.close();
        return loaded;
    }

    /**
     * Download route's images from web to external storage
     * (get image paths from array of route objects)
     *
     * @param routes - array list of route objects
     */
    public static void downloadRouteImages(Context context, ArrayList<Route> routes){
        for (Route route : routes) {

            String imagePath = route.getImagePath();
            //if image path was set, download image
            if (!(imagePath.equals(""))) {
                String imageName = "route_" + route.getId();
                // download the image to external storage
                boolean currentStatus = NetworkHelper.downloadImage(context, imagePath, imageName, true);
                // add status to log
                if (Toolbox.DEBUG) {
                    Log.d(TAG, "Loaded img " + imageName + " (" + imagePath + "): " + currentStatus);
                }
            }
        }
    }

    /**
     * Download route's images from web to external storage
     * (get image paths from database, where id more than defined)
     *
     * @param dbConnection - open database connection
     * @param startId - defined id (routes with uid more than it will be loaded)
     */
    public static boolean downloadRouteImages(Context context, SQLiteDatabase dbConnection, int startId){

        String selectImagePaths = "SELECT " + RouteTable._ID
                + ", " + RouteTable.COLUMN_IMAGEPATH
                + " FROM " + RouteTable.TABLE_NAME
                + " WHERE " + RouteTable._ID + " > " + startId
                + " AND NOT (COALESCE(" + RouteTable.COLUMN_IMAGEPATH + ",'') = '')";

        // get cursor
        Cursor cursorRoutes = dbConnection.rawQuery(selectImagePaths, null);

        boolean loaded = true;

        // get data from cursor
        while(cursorRoutes.moveToNext()){
            int uid = cursorRoutes.getInt(cursorRoutes.getColumnIndexOrThrow(RouteTable._ID));
            String imagepath = cursorRoutes.getString(cursorRoutes.getColumnIndexOrThrow(RouteTable.COLUMN_IMAGEPATH));
            // download image and update status
            String filename = "route_" + String.valueOf(uid);
            // download image
            boolean currentStatus = NetworkHelper.downloadImage(context, imagepath, filename, true);
            // add status to log
            if (Toolbox.DEBUG) {
                Log.d(TAG, "Loaded img " + filename + " (" + imagepath + "): " + currentStatus);
            }
            //update status
            loaded = loaded & currentStatus;
        }

        //close cursor
        cursorRoutes.close();

        return loaded;
    }

}
