package xyz.roadtripplanner.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Static var-s and methods for User table
 *
 * @author xyz
 */
public class UserTable implements BaseColumns {


    public static final String TABLE_NAME = "tbUser";

    // table's columns
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_FULLNAME = "full_name";
    public static final String COLUMN_ISADMIN = "is_admin";



    /** create user table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_USERNAME + " TEXT DEFAULT '',"
                + COLUMN_EMAIL + " TEXT DEFAULT '',"
                + COLUMN_FULLNAME + " TEXT DEFAULT '',"
                + COLUMN_ISADMIN + " INTEGER DEFAULT 0 )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
