package xyz.roadtripplanner.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.PlaceTable;
import xyz.roadtripplanner.model.Place;

/**
 * Place DAO implementation
 * Functions for operations with DB
 *
 * @author xyz
 */
public class PlaceDAOImplementation implements PlaceDAO, BaseColumns {

    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context apps' context
     */
    public PlaceDAOImplementation(Context context){
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
     * Insert place to DB
     * @param place place object
     * @return status
     */
    @Override
    public boolean insertPlace(Place place) {

        try {
            ContentValues values = new ContentValues();

            values.put(PlaceTable.COLUMN_ADDRESSID, place.getAddressId());
            values.put(PlaceTable.COLUMN_SHORTDESC, place.getShortDescription());
            values.put(PlaceTable.COLUMN_USERID, place.getUserId());
            values.put(PlaceTable.COLUMN_FULLDESC, place.getFullDescription());
            values.put(_ID, place.getId());

            //insert row
            db.insert(PlaceTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }


    /**
     * General method to make request to DB
     *
     * @param whereStatement - filter string
     * @return array list of places
     */
    public ArrayList<Place> findPlaces(String whereStatement){
        ArrayList<Place> placeList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + PlaceTable.TABLE_NAME + whereStatement;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Place place = new Place();
                    place.setId(cursor.getInt(0));
                    place.setShortDescription(cursor.getString(1));
                    place.setFullDescription(cursor.getString(2));
                    place.setAddressId(cursor.getInt(3));

                    // Adding attraction to list
                    placeList.add(place);
                } while (cursor.moveToNext());
            }
            db.close();
            cursor.close();
        }catch(Exception e){
            Log.d("Exception Get", e.getMessage());
        }
        return placeList;
    }

    /**
     * Get palce by ID
     *
     * @param id place ID
     * @return place object
     */
    @Override
    public Place findPlaceById(int id) {
        List<Place> categoryList = findPlaces(" WHERE _id = " + id);
        if (categoryList.size() > 0) {
            return categoryList.get(0);
        }
        return null;
    }
}