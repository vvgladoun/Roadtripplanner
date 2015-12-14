package xyz.roadtripplanner.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Static var-s and methods for Place table
 *
 * @author xyz
 */
public class PlaceTable implements BaseColumns {

    public static final String TAG = PlaceTable.class.getSimpleName();
    public static final String TABLE_NAME = "tbPlace";

    // table's columns
    public static final String COLUMN_SHORTDESC = "short_description";
    public static final String COLUMN_FULLDESC = "full_description";
    public static final String COLUMN_ADDRESSID = "address_id";
    public static final String COLUMN_USERID = "user_id";

    /** create place table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_SHORTDESC + " TEXT DEFAULT '',"
                + COLUMN_FULLDESC + " TEXT DEFAULT '',"
                + COLUMN_ADDRESSID + " INTEGER,"
                + COLUMN_USERID + " INTEGER )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
