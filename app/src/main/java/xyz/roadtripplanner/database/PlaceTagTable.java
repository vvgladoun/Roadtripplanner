package xyz.roadtripplanner.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Static var-s and methods for table,
 * which connects Places and Tags
 *
 * @author xyz
 */
public class PlaceTagTable implements BaseColumns {


    public static final String TABLE_NAME = "tbPlaceTag";

    // table's columns
    public static final String COLUMN_PLACEID = "place_id";
    public static final String COLUMN_TAGID = "tag_id";
    public static final String COLUMN_ISPRIMARY = "is_primary";

    /** create place-tag table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_PLACEID + " INTEGER,"
                + COLUMN_TAGID + " INTEGER,"
                + COLUMN_ISPRIMARY + " INTEGER )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
