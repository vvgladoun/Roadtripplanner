package xyz.roadtripplanner.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Static var-s and methods for table,
 * which contains routes' places
 *
 * @author xyz
 */
public class RoutePointTable implements BaseColumns {


    public static final String TABLE_NAME = "tbRoutePoint";

    // table's columns
    public static final String COLUMN_ROUTEID = "route_id";
    public static final String COLUMN_PLACEID = "place_id";
    public static final String COLUMN_ORDERNUM = "order_number";

    /** create place-route table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_ROUTEID + " INTEGER,"
                + COLUMN_PLACEID + " INTEGER,"
                + COLUMN_ORDERNUM + " INTEGER )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
