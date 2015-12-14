package xyz.roadtripplanner.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Static var-s and methods for table,
 * which contains users' comments on Places
 *
 * @author xyz
 */
public class PlaceCommentTable implements BaseColumns {


    public static final String TABLE_NAME = "tbPlaceComment";

    // table's columns
    public static final String COLUMN_PLACEID = "place_id";
    public static final String COLUMN_EDITDATE = "edit_date";
    public static final String COLUMN_USERID = "user_id";
    public static final String COLUMN_COMMENT = "comment_text";

    /** create place-comment table */
    static void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY UNIQUE,"
                + COLUMN_PLACEID + " INTEGER,"
                + COLUMN_EDITDATE + " TEXT,"
                + COLUMN_USERID + " USERID,"
                + COLUMN_COMMENT + " TEXT DEFAULT '' )");
    }

    /** drop table if exists */
    static void drop(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}
