package xyz.roadtripplanner.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.sql.SQLException;

import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.PlaceTagTable;

/**
 * Place Tag DAO implementation
 * Functions for operations with DB
 *
 * @author xyz
 */
public class PlaceTagDAOImplementation implements BaseColumns {
    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     * @param context app's context
     */
    public PlaceTagDAOImplementation(Context context){
        Log.d("Place impl", "Started");
        mDbHelper = new DbHelper(context);
    }

    /**
     * open DB connection
     * @throws SQLException
     */
    public void open() throws SQLException {
        db = mDbHelper.getWritableDatabase();
    }

    /**
     * close DB connection
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Insert place tag to DB
     * @param id id of record
     * @param plId place id
     * @param tagId tag id
     * @param isPrimary 1 if tag is primary
     * @return status
     */
    public boolean insertPlaceTag(int id, int plId, int tagId, int isPrimary) {
        try {
            ContentValues values = new ContentValues();

            values.put(PlaceTagTable.COLUMN_ISPRIMARY, isPrimary);
            values.put(PlaceTagTable.COLUMN_PLACEID, plId);
            values.put(PlaceTagTable.COLUMN_TAGID, tagId);
            values.put(_ID, id);

            //insert row
            db.insert(PlaceTagTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }
}
