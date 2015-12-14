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

import xyz.roadtripplanner.database.AddressTable;
import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.model.Address;

/**
 * DAO implementation for addresses
 *
 * @author xyz
 */
public class AddressDAOImplementation implements AddressDAO, BaseColumns {

    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     * @param context - app's context
     */
    public AddressDAOImplementation(Context context){
        mDbHelper = DbHelper.getInstance(context);
    }

    @Override
    public boolean insertAddress(Address address) {
        try {
            ContentValues values = new ContentValues();

            values.put(AddressTable.COLUMN_BUILDING, address.getBuilding() );
            values.put(AddressTable.COLUMN_CITY, address.getCity());
            values.put(AddressTable.COLUMN_COUNTRY, address.getCountry());
            values.put(AddressTable.COLUMN_LATITUDE, address.getLatitude());
            values.put(AddressTable.COLUMN_LONGITUDE, address.getLongitude());
            values.put(AddressTable.COLUMN_POSTINDEX, address.getPostIndex());
            values.put(AddressTable.COLUMN_SUBURB, address.getSuburb());
            values.put(_ID, address.getId());

            //insert row
            db.insert(AddressTable.TABLE_NAME, null, values);

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
     * General method to generate request to DB

     * @param whereStatement filter
     * @return array list of addresses
     */
    public ArrayList<Address> findAddress(String whereStatement){
        ArrayList<Address> addressList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + AddressTable.TABLE_NAME + whereStatement;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Address address = new Address();
                    address.setId(cursor.getInt(0));
                    address.setLatitude(cursor.getDouble(1));
                    address.setLongitude(cursor.getDouble(2));
                    address.setCity(cursor.getString(4));
                    address.setCountry(cursor.getString(3));
                    address.setBuilding(cursor.getString(7));
                    address.setStreet(cursor.getString(6));
                    address.setSuburb(cursor.getString(5));
                    address.setPostIndex(cursor.getString(8));


                    // Adding attraction to list
                    addressList.add(address);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch(Exception e){
            Log.d("Exception Get", e.getMessage());
        }
        return addressList;
    }

    /**
     * Get address by its ID
     *
     * @param id - uid in datastore
     * @return object address
     */
    @Override
    public Address findAddressById(int id) {
        List<Address> categoryList = findAddress(" WHERE _id = " + id);
        if (categoryList.size() > 0) {
            return categoryList.get(0);
        }
        return null;
    }
}
