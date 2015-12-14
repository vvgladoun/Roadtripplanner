package xyz.roadtripplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import xyz.roadtripplanner.utilities.NetworkHelper;
import xyz.roadtripplanner.utilities.Toolbox;

/**
 * Static var-s and methods for Place table
 *
 * @author xyz
 */
public class ImageTable implements BaseColumns {

    public static final String TAG = ImageTable.class.getSimpleName();
    public static final String TABLE_NAME = "tbImage";

    // table's columns
    public static final String COLUMN_PLACEID = "place_id";
    public static final String COLUMN_PATH = "image_path";
    public static final String COLUMN_ISDEFAULT = "is_default";

    /** create place table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_PLACEID + " INTEGER,"
                + COLUMN_PATH + " TEXT DEFAULT '',"
                + COLUMN_ISDEFAULT + " INTEGER )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    /**
     * Download route's images from web to external storage
     * (get image paths from database, where id more than defined)
     *
     * @param dbConnection - open database connection
     * @param startId - defined id (routes with uid more than it will be loaded)
     */
    public static boolean downloadPlaceImages(Context context, SQLiteDatabase dbConnection, int startId){

        String selectImagePaths = "SELECT " + COLUMN_PLACEID
                + ", " + COLUMN_PATH
                + " FROM " + TABLE_NAME
                + " WHERE " + _ID + " > " + startId
                + " AND NOT (COALESCE(" + COLUMN_PATH + ",'') = '')";

        // get cursor
        Cursor cursorImages = dbConnection.rawQuery(selectImagePaths, null);

        boolean loaded = true;

        // get data from cursor
        while(cursorImages.moveToNext()){
            int uid = cursorImages.getInt(cursorImages.getColumnIndexOrThrow(COLUMN_PLACEID));
            String imagepath = cursorImages.getString(cursorImages.getColumnIndexOrThrow(COLUMN_PATH));
            // download image and update status
            String filename = "place_" + String.valueOf(uid);
            // download image (skip if already loaded)
            boolean currentStatus = NetworkHelper.downloadImage(context, imagepath, filename, false);
            // add status to log
            if (Toolbox.DEBUG) {
                Log.d(TAG, "Loaded " + filename +"(" + imagepath + "): " + currentStatus);
            }
            //update status
            loaded = loaded & currentStatus;
        }

        //close cursor
        cursorImages.close();

        return loaded;
    }
}