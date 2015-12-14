package xyz.roadtripplanner.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import xyz.roadtripplanner.database.DAO.PlaceCommentDAOImplementation;
import xyz.roadtripplanner.database.DAO.RouteDAOImplementation;
import xyz.roadtripplanner.database.DAO.RoutePointDAOImplementation;
import xyz.roadtripplanner.database.DAO.RouteSharingDAOImplementation;
import xyz.roadtripplanner.database.DAO.UserDAOImplementation;
import xyz.roadtripplanner.login.RegisterFragment;
import xyz.roadtripplanner.places.PlaceDetailsFragment;
import xyz.roadtripplanner.routes.CreateRouteFragment;
import xyz.roadtripplanner.routes.RoutesFragment;
import xyz.roadtripplanner.utilities.APIConfig;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.PreferencesManagement;
import xyz.roadtripplanner.utilities.VolleyHelper;

/**
 * Methods for work with web DB
 *
 * @author xyz
 */
public class ToWebDB {

    private static final String TAG = "INCERTTOWEB";
//    private Context context;

//    public ToWebDB(Context context){
//        this.context = context;
//    }

    /**
     * Incert routepoint to DB on server
     *
     * @param routeId route ID
     * @param markerId place ID
     * @param context app's context
     */
    public static void insertRoutePointWeb(final Context context, final int routeId,
                                           final int markerId){
        // Tag used to cancel the request
        String tagRequestPlace = "REQUEST_ROUTEPOINT";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ROUTEPOINTADD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "ROUTEPOINT Response: " + response);
                int resp = Integer.valueOf(response);
                // Check for error node in json
                if (resp > 0) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    RoutePointDAOImplementation rdi = new RoutePointDAOImplementation(context);
                    SynchronizeDB sdb = new SynchronizeDB(context);
                    sdb.synchronizeRoutePoint(rdi.getMaxId());
                } else {
                    // Error in login. Get the error message
                    Log.d(TAG, "routepoint not inserted");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ROUTEPOINT Error: " + error.getMessage());
                String errorText = "ROUTEPOINT Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to place url
                Map<String, String> params = new HashMap<>();
                params.put("route_id", String.valueOf(routeId));
                params.put("marker_id", String.valueOf(markerId));
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(context).addToRequestQueue(strReq, tagRequestPlace);
    }

    /**
     * Insert user to server DB
     * @param context app's context
     * @param username new user username
     * @param fullname new user full name
     * @param email new user email
     * @param password new user password
     * @param fragment register fragment
     */
    public static void insertUserWeb(final Context context, final String username, final String fullname,
                              final String email, final String password, final RegisterFragment fragment){
        // Tag used to cancel the request
        String tagRequestPlace = "REQUEST_USER";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.USERADD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "INSERTUSER Response: " + response);
                int resp = Integer.valueOf(response);
                // Check for error node in json
                if (resp > 0) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    UserDAOImplementation udi = new UserDAOImplementation(context);
                    SynchronizeDB sdb = new SynchronizeDB(context);
                    sdb.synchronizeRoutePoint(udi.getMaxId());

                    if (context instanceof ActivityCallback) {
                        ActivityCallback callbackActivity = (ActivityCallback) context;
                        // save credential to preferences
                        PreferencesManagement.savePreferences(context, resp, username, password, email);

                        //clear back stack (to avoid return by back button press
                        callbackActivity.clearBackStack();
                        fragment.showProgress(false);
                        // launch routes fragment
                        callbackActivity.changeFragmentAsFirst(new RoutesFragment());
                    }
                    fragment.showProgress(false);
                } else {
                    // Error in user cteation. Get the error message
                    Log.d(TAG, "user not created");
                    fragment.showProgress(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "INSERTUSER Error: " + error.getMessage());
                String errorText = "INSERTUSER Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to place url

                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("fullname", fullname);
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(context).addToRequestQueue(strReq, tagRequestPlace);
    }

    /**
     * Insert route to server DB
     * @param routeName new route name
     * @param description new route description
     * @param imagePath  new route image path
     * @param fragment create route fragment
     */
    public static void insertRouteWeb(final Context context, final String routeName,
                                      final String description, final String imagePath,
                                      final CreateRouteFragment fragment){
        // Tag used to cancel the request
        String tagRequestPlace = "REQUEST_ROUTE";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ROUTEADD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "INSERTROUTE Response: " + response);
                int resp = Integer.valueOf(response);
                // Check for error node in json
                if (resp > 0) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    RouteDAOImplementation rdi = new RouteDAOImplementation(context);
                    SynchronizeDB sdb = new SynchronizeDB(context);
                    sdb.synchronizeRoute(rdi.getMaxId());
                    insertRouteSharingWeb(context, resp, PreferencesManagement.getUserId(context));
                    fragment.showProgress(false);

                    if (context instanceof ActivityCallback) {
                        ActivityCallback callbackActivity = (ActivityCallback) context;

                        //clear back stack (to avoid return by back button press
                        callbackActivity.clearBackStack();
                        // launch routes fragment
                        callbackActivity.changeFragmentAsFirst(new RoutesFragment());
                    }

                } else {
                    // Error in user cteation. Get the error message
                    Log.d(TAG, "route not created");
                    fragment.showProgress(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "INSERTROUTE Error: " + error.getMessage());
                String errorText = "INSERTROUTE Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show();
                fragment.showProgress(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to place url

                Map<String, String> params = new HashMap<>();
                params.put("routeName", routeName);
                params.put("description", description);
                params.put("imagePath", imagePath);
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(context).addToRequestQueue(strReq, tagRequestPlace);
    }


    /**
     * edit route with id = routeId on the server
     *
     * @param routeId route ID
     * @param routeName new name
     * @param description new description
     * @param imagePath new image path
     * @param fragment create route fragment
     */
    public static void editRouteWeb(final Context context, final int routeId,
                                    final String routeName, final String description,
                                    final String imagePath, final CreateRouteFragment fragment){
        // Tag used to cancel the request
        String tagRequestPlace = "REQUEST_ROUTE";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ROUTEEDIT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "INSERTROUTE Response: " + response);
                int resp = Integer.valueOf(response);
                // Check for error node in json
                if (resp >= 0) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    SynchronizeDB sdb = new SynchronizeDB(context);
                    sdb.synchronizeRoute(routeId - 1);
                    fragment.showProgress(false);

                    if (context instanceof ActivityCallback) {
                        ActivityCallback callbackActivity = (ActivityCallback) context;

                        //clear back stack (to avoid return by back button press
                        callbackActivity.clearBackStack();
                        // launch routes fragment
                        callbackActivity.changeFragmentAsFirst(new RoutesFragment());
                    }

                } else {
                    // Error in user cteation. Get the error message
                    Log.d(TAG, "route not created");
                    fragment.showProgress(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "INSERTROUTE Error: " + error.getMessage());
                String errorText = "INSERTROUTE Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show();
                fragment.showProgress(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to place url

                Map<String, String> params = new HashMap<>();
                params.put("routeName", routeName);
                params.put("description", description);
                params.put("imagePath", imagePath);
                params.put("routeId", String.valueOf(routeId));
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(context).addToRequestQueue(strReq, tagRequestPlace);
    }


    /**
     * Insert route sharing to the server DB
     *
     * @param routeId route ID
     * @param userId user ID
     */
    public static void insertRouteSharingWeb(final Context context, final int routeId,
                                             final int userId){
        // Tag used to cancel the request
        String tagRequestPlace = "REQUEST_ROUTESHARING";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ROUTESHARINGADD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "INSERTROUTE Response: " + response);
                int resp = Integer.valueOf(response);
                // Check for error node in json
                if (resp > 0) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    RouteSharingDAOImplementation rsdi = new RouteSharingDAOImplementation(context);
                    SynchronizeDB sdb = new SynchronizeDB(context);
                    sdb.synchronizeRouteSharing(rsdi.getMaxId());
                } else {
                    // Error in user cteation. Get the error message
                    Log.d(TAG, "routesharing not created");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "INSERTROUTE Error: " + error.getMessage());
                String errorText = "INSERTROUTE Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to place url

                Map<String, String> params = new HashMap<>();
                params.put("routeId", String.valueOf(routeId));
                params.put("userId", String.valueOf(userId));
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(context).addToRequestQueue(strReq, tagRequestPlace);
    }

    /**
     * Insert comment to the server DB
     * @param placeId place ID
     * @param userId user ID who wrote the comment
     * @param commentText new comment text
     * @param fragment Place Details fragment
     */
    public static void insertCommentWeb(final Context context, final int placeId, final int userId,
                                 final String commentText, final PlaceDetailsFragment fragment){
        // Tag used to cancel the request
        String tagRequestPlace = "REQUEST_COMMENT";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.COMMENTADD_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "INSERTCOMMENT Response: " + response);
                int resp = Integer.valueOf(response);
                // Check for error node in json
                if (resp > 0) {
                    // user successfully logged in
                    Log.d(TAG, "no error");
                    PlaceCommentDAOImplementation pcdi = new PlaceCommentDAOImplementation(context);
                    SynchronizeDB sdb = new SynchronizeDB(context);
                    sdb.synchronizeComment(pcdi.getMaxId(), fragment);
                    fragment.showProgress(false);
                } else {
                    // Error in user cteation. Get the error message
                    Log.d(TAG, "comment not created");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "INSERTCOMMENT Error: " + error.getMessage());
                String errorText = "INSERTCOMMENT Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show();
                fragment.showProgress(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to place url

                Map<String, String> params = new HashMap<>();
                params.put("placeId", String.valueOf(placeId));
                params.put("userId", String.valueOf(userId));
                params.put("commentText", commentText);
                return params;
            }
        };

        // Adding request to request queue
        VolleyHelper.getInstance(context).addToRequestQueue(strReq, tagRequestPlace);
    }
}
