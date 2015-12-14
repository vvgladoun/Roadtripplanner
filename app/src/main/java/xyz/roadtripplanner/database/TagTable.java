package xyz.roadtripplanner.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Static var-s and methods for Tag table
 *
 * @author xyz
 */
public class TagTable implements BaseColumns {


    public static final String TABLE_NAME = "tbTag";

    // table's columns
    public static final String COLUMN_TAGNAME = "tag_name";

    /** create tag table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_TAGNAME + " TEXT DEFAULT '' )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
