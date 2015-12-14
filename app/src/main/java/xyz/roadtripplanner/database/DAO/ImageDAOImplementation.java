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
import xyz.roadtripplanner.database.ImageTable;
import xyz.roadtripplanner.model.PlaceImage;

/**
 * Image DAO implementation
 *
 * @author xyz
 */
public class ImageDAOImplementation implements ImageDAO, BaseColumns {

    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context app's context
     */
    public ImageDAOImplementation(Context context){
        mDbHelper = new DbHelper(context);
    }

    /**
     * insert place-image object to the datastore
     *
     * @param placeImage place-image link object
     * @return status
     */
    @Override
    public boolean insertImage(PlaceImage placeImage) {
        try {
            ContentValues values = new ContentValues();

            values.put(ImageTable.COLUMN_ISDEFAULT, placeImage.isDefault());
            values.put(ImageTable.COLUMN_PATH, placeImage.getImagePath());
            values.put(ImageTable.COLUMN_PLACEID, placeImage.getPlaceId());
            values.put(_ID, placeImage.getId());

            //insert row
            db.insert(ImageTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            //Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
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
    /**
     * General method to make request to DB
     * @param whereStatement - condition
     * @return array of objects
     */
    public ArrayList<PlaceImage> findImage(String whereStatement){
        ArrayList<PlaceImage> imageList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + ImageTable.TABLE_NAME + whereStatement;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PlaceImage image = new PlaceImage();
                    image.setId(cursor.getInt(0));
                    image.setPlaceId(cursor.getInt(1));
                    image.setImagePath(cursor.getString(2));
                    image.setIsDefault(cursor.getInt(3));

                    // Adding attraction to list
                    imageList.add(image);
                } while (cursor.moveToNext());
            }
            db.close();
        }catch(Exception e){
            Log.d("Exception Get", e.getMessage());
        }
        return imageList;
    }

    /**
     * Get image by ID
     *
     * @param id - image ID
     * @return placeImage object
     */
    @Override
    public PlaceImage findImageById(int id) {
        List<PlaceImage> imageList = findImage(" WHERE _id = " + id);
        if (imageList.size() > 0) {
            return imageList.get(0);
        }
        return null;
    }

    /**
     * Get image by Place ID
     *
     * @param id - place ID
     * @return placeImage object
     */
    @Override
    public PlaceImage findImageByPlaceId(int id) {
        ArrayList<PlaceImage> imageList = findImage(" WHERE place_id = " + id);
        if (imageList.size() > 0) {
            return imageList.get(0);
        }
        return null;
    }

}
