package xyz.roadtripplanner.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.sql.SQLException;

import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.TagTable;
import xyz.roadtripplanner.model.Tag;

/**
 * DAO implementation for tags
 *
 * @author xyz
 */
public class TagDAOImplementation implements TagDAO, BaseColumns{

    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context app's context
     */
    public TagDAOImplementation(Context context){
        mDbHelper = DbHelper.getInstance(context);
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
        db.close();
    }

    @Override
    public boolean insertTag(Tag tag) {
        try {
            ContentValues values = new ContentValues();

            values.put(TagTable.COLUMN_TAGNAME, tag.getName());
            values.put(_ID, tag.getId());

            //insert row
            db.insert(TagTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            //Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }
}
