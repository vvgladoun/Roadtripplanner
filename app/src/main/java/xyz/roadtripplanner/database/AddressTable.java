package xyz.roadtripplanner.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Static var-s and methods for Address table
 *
 * @author xyz
 */
public class AddressTable implements BaseColumns {


    public static final String TABLE_NAME = "tbAddress";

    // table's columns
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_SUBURB = "suburb";
    public static final String COLUMN_BUILDING = "building";
    public static final String COLUMN_POSTINDEX = "post_index";
    public static final String COLUMN_STREET = "street";

    /** create address table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_LATITUDE + " DECIMAL(17,13) NOT NULL,"
                + COLUMN_LONGITUDE + " TEXT,"
                + COLUMN_COUNTRY + " TEXT DEFAULT '',"
                + COLUMN_CITY + " TEXT DEFAULT '',"
                + COLUMN_SUBURB + " TEXT DEFAULT '',"
                + COLUMN_STREET + " TEXT DEFAULT '',"
                + COLUMN_BUILDING + " TEXT DEFAULT '',"
                + COLUMN_POSTINDEX + " TEXT DEFAULT '' )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
