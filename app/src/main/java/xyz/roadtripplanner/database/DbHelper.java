package xyz.roadtripplanner.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import xyz.roadtripplanner.utilities.PreferencesManagement;


/**
 * SQLite helper class for rtp database
 *
 * @author xyz
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static DbHelper sInstance;
    private static Context sContext;

    // Database Name
    private static final String DATABASE_NAME = "Roadtripplanner.db";


    /**
     * return existing instance
     * (or create new if not created)
     *
     * @param context
     * @return helper's instance
     */
    public static synchronized DbHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new DbHelper(context);
        }

        return sInstance;
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sContext = context;
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        sContext = context;
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                    DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
        sContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop all tables if exists
        dropAllTables(db);

        // create tables
        onCreate(db);

        // set preferences to load from the web server
        PreferencesManagement.setDataLoaded(sContext, false);

        Log.i(TAG, "Database was updated from ver." + oldVersion + " to ver." + newVersion);
    }

    /**
     * Drop all tables from the db
     *
     * @param db
     */
    public void dropAllTables(SQLiteDatabase db){
        AddressTable.drop(db);
        UserTable.drop(db);
        TagTable.drop(db);
        PlaceTable.drop(db);
        PlaceTagTable.drop(db);
        PlaceCommentTable.drop(db);
        RouteTable.drop(db);
        RoutePointTable.drop(db);
        RouteSharingTable.drop(db);
        ImageTable.drop(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create all tables
        AddressTable.create(db);
        UserTable.create(db);
        TagTable.create(db);
        PlaceTable.create(db);
        PlaceTagTable.create(db);
        PlaceCommentTable.create(db);
        RouteTable.create(db);
        RoutePointTable.create(db);
        RouteSharingTable.create(db);
        ImageTable.create(db);
    }


    /**
     * Select maximum id from table
     *
     * @param context - app's context
     * @param tableName table name
     * @return max available id from the database
     */
    public static int getMaxTableId(Context context, String tableName){
        int maxId = 0;
        // prepare delete statements
        String selectMax = "SELECT MAX(" + RouteSharingTable._ID
                + ") maxid FROM " + tableName;

        // create connection
        SQLiteDatabase dbConnection = getInstance(context).getWritableDatabase();
        // get cursor
        Cursor cursorMax = dbConnection.rawQuery(selectMax, null);
        // get data from cursor
        if(cursorMax.moveToNext()){
            maxId = cursorMax.getInt(cursorMax.getColumnIndexOrThrow("maxid"));
        }
        // close cursor and database connection
        cursorMax.close();
        dbConnection.close();

        return maxId;
    }

}
