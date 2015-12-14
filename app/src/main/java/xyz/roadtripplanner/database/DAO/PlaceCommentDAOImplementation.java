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
import xyz.roadtripplanner.database.PlaceCommentTable;
import xyz.roadtripplanner.model.Comment;

/**
 * PlaceComment DAO implementation
 * Functions for operations with DB
 *
 * @author xyz
 */
public class PlaceCommentDAOImplementation implements BaseColumns {
    private SQLiteDatabase db;
    private DbHelper mDbHelper;

    /**
     * Constructor
     *
     * @param context app's context
     */
    public PlaceCommentDAOImplementation(Context context){
        Log.d("Place impl", "Started");
        mDbHelper = new DbHelper(context);
    }

    /**
     * open DB connection
     *
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

    public boolean insertPlaceComment(Comment comment) {
        try {
            ContentValues values = new ContentValues();

            values.put(PlaceCommentTable.COLUMN_COMMENT, comment.getCommentText());
            values.put(PlaceCommentTable.COLUMN_EDITDATE, comment.getDate());
            values.put(PlaceCommentTable.COLUMN_PLACEID, comment.getPlaceId());
            values.put(PlaceCommentTable.COLUMN_USERID, comment.getUserId());
            values.put(_ID, comment.getId());

            //insert row
            db.insert(PlaceCommentTable.TABLE_NAME, null, values);

        }catch (Exception ex){
            Log.d("Exception Insert", ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * General method to make request to DB
     * @param whereStatement condition for select statement
     * @return list of comments
     */
    public ArrayList<Comment> findComment(String whereStatement){
        ArrayList<Comment> commentList = new ArrayList<>();
        // Select Query
        String selectQuery = "SELECT * FROM " + PlaceCommentTable.TABLE_NAME + whereStatement;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Comment comment = new Comment();
                    comment.setId(cursor.getInt(0));
                    comment.setPlaceId(cursor.getInt(1));
                    comment.setDate(cursor.getString(2));
                    comment.setUserId(cursor.getInt(3));
                    comment.setCommentText(cursor.getString(4));

                    // Adding attraction to list
                    commentList.add(comment);
                } while (cursor.moveToNext());
            }
            db.close();
            cursor.close();
        }catch(Exception e){
            Log.d("Exception Get", e.getMessage());
        }
        return commentList;
    }

    /**
     * Get comment by ID
     * @param id - comment ID
     * @return comment object
     */

    public Comment findCommentById(int id) {
        List<Comment> commentList = findComment(" WHERE _id = " + id);
        if (commentList.size() > 0) {
            return commentList.get(0);
        }
        return null;
    }

    /**
     * Get comments by place ID
     * @param id - place ID
     * @return list of comments
     */
    public ArrayList<Comment> findCommentByPlaceId(int id) {
        ArrayList<Comment> commentList = findComment(" WHERE place_id = " + id);
        if (commentList.size() > 0) {
            return commentList;
        }
        return null;
    }

    /**
     * Get Max id in place commet table
     *
     * @return max id in table
     */
    public int getMaxId(){
        int maxId = 0;

        String selectQuery = "SELECT MAX(_id) as maxId FROM " + PlaceCommentTable.TABLE_NAME;
        try {
            open();
            Cursor cursor = db.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                maxId = cursor.getInt(cursor.getColumnIndexOrThrow("maxId"));
            }
            db.close();
        }catch(Exception e){
            Log.d("PLACECOMENTS", e.toString());
        }
        return maxId;
    }
}
