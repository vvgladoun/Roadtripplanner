package xyz.roadtripplanner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import xyz.roadtripplanner.json.JsonParserHelper;
import xyz.roadtripplanner.places.PlaceDetailsFragment;
import xyz.roadtripplanner.utilities.APIConfig;
import xyz.roadtripplanner.utilities.HttpHandler;
import xyz.roadtripplanner.utilities.VolleyHelper;

/**
 *  Methods for async updating database tables from web
 *
 *  @author XYZ
 */
public class SynchronizeDB {

    private static final String TAG = SynchronizeDB.class.getSimpleName();
    private Context mContext;

    public SynchronizeDB(Context context){
        this.mContext = context;
    }


    /**
     * Remove from database all rows with uid more then defined id
     *
     * @param tableName - name of the table to delete rows from
     * @param lastId - defined id
     */
    public static void removeRows(Context context, String tableName, int lastId){
        // build delete statement
        String deleteRows = "DELETE FROM " + tableName + " WHERE "
                + RouteTable._ID + " > " + lastId;
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        // execute delete statement
        db.execSQL(deleteRows);
        // close connection
        db.close();
    }



    /**
     * sync data for places
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizePlace(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.PLACE_URL, params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    removeRows(context, PlaceTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonPlaces(context, jObj);
                    if (success){
                        Log.d(TAG, "Places updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "Place Error: no response from server");
        }
    }

    /**
     * Get data from server using Google Volley
     * @param minid min id for synchronization
     */





    /**
     * sync data for addresses
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizeAddress(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.ADDRESS_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    removeRows(context, AddressTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonAddress(context, jObj);
                    if (success){
                        Log.d(TAG, "Addresses updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "Address loading error: no response from server");
        }
    }






    /**
     * sync data for tags
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizeTag(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.TAG_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "no error");
                    removeRows(context, TagTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonTag(context, jObj);
                    if (success){
                        Log.d(TAG, "Tags updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "Tags loading error: no response from server");
        }
    }




    /**
     * sync data for routes
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizeRoute(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.ROUTE_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "no error");
                    removeRows(context, RouteTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonRoute(context, jObj, minid);
                    if (success){
                        Log.d(TAG, "Routes updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "Routes loading error: no response from server");
        }
    }


    /**
     * Get data from server using Google Volley
     * @param minid min id for synchronization
     */
    public void synchronizeRoute(final int minid){
        // Tag used to cancel the request
        String tagRequestTag = "REQUEST_ROUTE";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ROUTE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Route Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if success tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // tag got successfully
                        Log.d(TAG, "no error");
                        removeRows(mContext, RouteTable.TABLE_NAME, minid);
                        boolean success = JsonParserHelper.decodeJsonRoute(mContext, jObj, minid);
                        if (success){
                            Log.d(TAG, "Routes updated");
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d(TAG, errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // show error message
                    String informString = "Json error: " + e.getMessage();
                    Log.d(TAG, informString);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Route Error: " + error.getMessage());
                String errorText = "Route Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Log.e(TAG, errorText);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to tag url
                Map<String, String> params = new HashMap<>();
                params.put("minid", String.valueOf(minid));
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(mContext).addToRequestQueue(strReq, tagRequestTag);
    }



    /**
     * sync data for places' tags
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizePlaceTag(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.PLACETAG_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success place tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "no error");
                    removeRows(context, PlaceTagTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonPlaceTag(context, jObj);
                    if (success){
                        Log.d(TAG, "Place Tags updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "place tags loading error: no response from server");
        }
    }



    /**
     * sync data for places' comments
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizeComment(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.PLACECOMMENT_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "no error");
                    removeRows(context, PlaceCommentTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonComment(context, jObj);
                    if (success){
                        Log.d(TAG, "Comments updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "place comments loading error: no response from server");
        }
    }

    /**
     * Get data from server using Google Volley
     * @param minid min id for synchronization
     */
    public void synchronizeComment(final int minid, final PlaceDetailsFragment fragment){
        // Tag used to cancel the request
        String tagRequestComment = "REQUEST_COMMENT";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.PLACECOMMENT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Comment Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if success tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // tag got successfully
                        Log.d(TAG, "no error");
                        removeRows(mContext, PlaceCommentTable.TABLE_NAME, minid);
                        boolean success = JsonParserHelper.decodeJsonComment(mContext, jObj);
                        if (success){
                            Log.d(TAG, "Comments updated");
                            if (fragment != null){
                                fragment.setListViewAdapter(true);
                            }
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d(TAG, errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // show error message
                    String informString = "Json error: " + e.getMessage();
                    Log.d(TAG, informString);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Comment Error: " + error.getMessage());
                String errorText = "Comment Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Log.e(TAG, errorText);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to tag url
                Map<String, String> params = new HashMap<>();
                params.put("minid", String.valueOf(minid));
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(mContext).addToRequestQueue(strReq, tagRequestComment);
    }



    /**
     * sync data for routes' points
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizeRoutePoint(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.ROUTEPOINT_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success place tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "no error");
                    removeRows(context, RoutePointTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonRoutePoint(context, jObj);
                    if (success){
                        Log.d(TAG, "Route Points updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "routes points loading error: no response from server");
        }
    }



    /**
     * Get data from server using Google Volley
     * @param minid min id for synchronization
     */
    public void synchronizeRoutePoint(final int minid){
        // Tag used to cancel the request
        String tagRequestRoutePoint = "REQUEST_ROUTEPOINT";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ROUTEPOINT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "RoutePoint Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if success place tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // tag got successfully
                        Log.d(TAG, "no error");
                        removeRows(mContext, RoutePointTable.TABLE_NAME, minid);
                        boolean success = JsonParserHelper.decodeJsonRoutePoint(mContext, jObj);
                        if (success){
                            Log.d(TAG, "Route Points updated");
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d(TAG, errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // show error message
                    String informString = "Json error: " + e.getMessage();
                    Log.d(TAG, informString);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "RoutePoint Error: " + error.getMessage());
                String errorText = "RoutePoint Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Log.e(TAG, errorText);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to tag url
                Map<String, String> params = new HashMap<>();
                params.put("minid", String.valueOf(minid));
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(mContext).addToRequestQueue(strReq, tagRequestRoutePoint);
    }



    /**
     * sync data for route sharing
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizeRouteSharing(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.ROUTESHARING_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success place tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "no error");
                    removeRows(context, RouteSharingTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonRouteSharing(context, jObj);
                    if (success){
                        Log.d(TAG, "RouteSharing updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "route sharing loading error: no response from server");
        }
    }

    /**
     * Get data from server using Google Volley
     * @param minid min id for synchronization
     */
    public void synchronizeRouteSharing(final int minid){
        // Tag used to cancel the request
        String tagRequestRouteSharing = "REQUEST_ROUTESHARING";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ROUTESHARING_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "RouteSharing Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if success place tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // tag got successfully
                        Log.d(TAG, "no error");
                        removeRows(mContext, RouteSharingTable.TABLE_NAME, minid);
                        boolean success = JsonParserHelper.decodeJsonRouteSharing(mContext, jObj);
                        if (success){
                            Log.d(TAG, "RouteSharing updated");
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d(TAG, errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // show error message
                    String informString = "Json error: " + e.getMessage();
                    Log.d(TAG, informString);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "RouteSharing Error: " + error.getMessage());
                String errorText = "RouteSharing Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Log.e(TAG, errorText);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to tag url
                Map<String, String> params = new HashMap<>();
                params.put("minid", String.valueOf(minid));
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(mContext).addToRequestQueue(strReq, tagRequestRouteSharing);
    }



    /**
     * sync data for users
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizeUser(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.USER_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "users: no error");
                    removeRows(context, UserTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonUser(context, jObj);
                    if (success){
                        Log.d(TAG, "Users updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, "users error: " + errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error (users): " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "user loading error: no response from server");
        }
    }


    /**
     * sync data for places' images
     *
     * @param context - app's context
     * @param minid - minimum id
     */
    public static void synchronizePlaceImage(Context context, final int minid) {

        // Posting parameters for request
        HashMap<String, String> params = new HashMap<>();
        params.put("minid", String.valueOf(minid));
        // request json
        String response = HttpHandler.performPostCall(APIConfig.PLACEIMAGE_URL,params);
        if (!(response.equals(""))){
            try {
                JSONObject jObj = new JSONObject(response);
                // check if success tag found in JSON
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // tag got successfully
                    Log.d(TAG, "no error");
                    removeRows(context, ImageTable.TABLE_NAME, minid);
                    boolean success = JsonParserHelper.decodeJsonPlaceImage(context, jObj, minid);
                    if (success){
                        Log.d(TAG, "Place Images updated");
                    }
                } else {
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Log.d(TAG, errorMsg);
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                // show error message
                String informString = "Json error: " + e.getMessage();
                Log.d(TAG, informString);
            }
        } else {
            Log.e(TAG, "places image loading error: no response from server");
        }
    }


// LOADING IMAGES

    /**
     * Download places' images to external storage
     *
     * @param startId - uid of the last downloaded place-image row
     * @return status - true if downloaded successfully
     */
    public static boolean syncPlacesImages(Context context, int startId){
        // open connection
        SQLiteDatabase dbConnection = DbHelper.getInstance(context).getWritableDatabase();
        // download images
        boolean status = ImageTable.downloadPlaceImages(context, dbConnection, startId);
        // close connection
        dbConnection.close();

        return status;

    }

    /**
     * Download route' images to internal storage
     *
     * @param startId - uid of the last downloaded route
     * @return status - true if downloaded successfully
     */
    public static boolean syncRoutesImages(Context context, int startId){
        // open connection
        SQLiteDatabase dbConnection = DbHelper.getInstance(context).getWritableDatabase();
        // download images
        boolean status = RouteTable.downloadRouteImages(context, dbConnection, startId);
        // close connection
        dbConnection.close();

        return status;
    }

}
