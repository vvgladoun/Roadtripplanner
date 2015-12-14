package xyz.roadtripplanner.routes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.RouteSharingTable;
import xyz.roadtripplanner.database.RouteTable;
import xyz.roadtripplanner.model.Route;
import xyz.roadtripplanner.model.RouteSharing;
import xyz.roadtripplanner.utilities.APIConfig;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.NetworkHelper;
import xyz.roadtripplanner.utilities.PreferencesManagement;
import xyz.roadtripplanner.utilities.Toolbox;
import xyz.roadtripplanner.utilities.VolleyHelper;

/**
 * Fragment with the list of user's routes
 *
 * @author xyz
 */
public class RoutesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = RoutesFragment.class.getSimpleName();

    private Context mContext;

    // variables for incremental data load
    private int mUserId;

    // swipe container for the implementation of the swipe-to-refresh pattern
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // widgets
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFAB;

    // custom adapter
    RoutesAdapter mRoutesAdapter;
    // task status
    public static boolean sLoading;
    private DownloadTask mDownloadTask;

    // array list for adapter
    ArrayList<Route> mRoutes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use activity's callback to:
        mContext = getContext();
//        if (getContext() instanceof ActivityCallback) {
//            ActivityCallback callbackActivity = (ActivityCallback) mContext;
//
//            //clear back stack (to avoid return by back button press)
//            callbackActivity.clearBackStack();
//        }

        // get stored user id
        mUserId = PreferencesManagement.getUserId(mContext);

        // retain instance and show menu
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.app_name);
        View routesView = inflater.inflate(R.layout.routes_list_fragment, container, false);
        initViewElements(routesView);

        return routesView;
    }

    /**
     * Define widgets and actions on the layout
     *
     * @param container layout's view
     */
    private void initViewElements(View container){

        // init recycler view
        mRecyclerView = (RecyclerView) container.findViewById(R.id.recyclerView);
        mRoutes = new ArrayList<>();
        mRoutesAdapter = new RoutesAdapter(getActivity(), mRoutes);
        mRecyclerView.setAdapter(mRoutesAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // find swipe container and add new listener on swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) container.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeVolleyRequest();
            }
        });
        // get FAB
        mFAB = (FloatingActionButton) container.findViewById(R.id.routes_fab);
        mFAB.setOnClickListener(this);
        // get progress bar
        mProgressBar = (ProgressBar) container.findViewById(R.id.progressBar);
        if (mRoutesAdapter.getItemCount() == 0) {
            showProgressBar();
        }
    }


    /**
     * Show only progress bar (while initial loading)
     */
    private void showProgressBar() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mFAB.setVisibility(View.GONE);
    }

    /**
     * Hide progress bar and show layout
     */
    private void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mFAB.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        // stop task if running
        if (sLoading) {
            mDownloadTask.cancel(true);
        }
        sLoading = false;
        super.onStop();
    }

    @Override
    public void onPause() {
        // clear swipe-to-refresh animation
        if (mSwipeRefreshLayout != null) {
            clearSwipeToRefresh();
        }
        super.onPause();
    }

    private void clearSwipeToRefresh(){
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.destroyDrawingCache();
        mSwipeRefreshLayout.clearAnimation();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //if on top - start swipe to refresh pattern
                boolean enable = false;
                if (mRecyclerView != null && mRecyclerView.getChildCount() > 0) {
                    int topPosition = mRecyclerView.getChildAt(0).getTop();
                    enable = topPosition >= 0;
                }
                mSwipeRefreshLayout.setEnabled(enable);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        makeVolleyRequest();
        // load pre-cashed data
        refreshAdapter();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.routes_fab) {
            // Start create route fragment
            if (NetworkHelper.checkConnection(mContext)) {
                CreateRouteFragment createRouteFragment = CreateRouteFragment.newInstance(0);
                if (getContext() instanceof ActivityCallback) {
                    ActivityCallback callbackActivity = (ActivityCallback) getContext();
                    callbackActivity.changeFragment(createRouteFragment);
                }
            } else {
                Snackbar.make(mFAB, "No internet Access, Check your internet connection."
                        , Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    /**
     * Request routes' and route sharing data from web
     */
    private void makeVolleyRequest(){

        // Tag used to cancel the request
        String tagRequestLogin = "REQ_ROUTES";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.SHAREDROUTES_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Routes Response: " + response);


                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if error tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // response with no errors from web. Parse json

                        // get new route sharings
                        JSONArray jsonRouteSharing = jObj.getJSONArray("routesharings");
                        Gson gson = new Gson();
                        RouteSharing[] routeSharings = gson.fromJson(jsonRouteSharing.toString(), RouteSharing[].class);

                        ArrayList<RouteSharing> newRouteSharings = new ArrayList<>(Arrays.asList(routeSharings));

                        // get new routes from json
                        JSONArray jsonRoutes = jObj.getJSONArray("routes");

                        Route[] routes = gson.fromJson(jsonRoutes.toString(), Route[].class);
                        ArrayList<Route> newRoutes = new ArrayList<>(Arrays.asList(routes));

                        if (Toolbox.DEBUG) {
                            Log.d(TAG, "JSON route: " + jsonRoutes.toString());
                            Log.d(TAG, "JSON route sharing: " + jsonRoutes.toString());
                        }

                        // save data to app's storage
                        saveNewData(newRouteSharings, newRoutes);

                    } else {
                        // Error on web server. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Snackbar.make(mFAB, errorMsg, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // show error message
                    String informString = "Json error: " + e.getMessage();
                    Snackbar.make(mFAB, informString, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Routes Error: " + error.getMessage());
                String errorText = "Routes Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (error.getMessage() != null) {
                    errorText += error.getMessage();
                } else {
                    errorText += "unknown error";
                }
                Snackbar.make(mFAB, errorText, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                // add current MAX route and sharing ids (0 - update all)
                params.put("maxrouteid", "0");
                params.put("maxshareid", "0");
                return params;
            }
        };
        // Adding request to request queue
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(strReq, tagRequestLogin);
    }


    /**
     * Start async task to download new data
     *
     * @param routeSharings - array list of route sharing objects
     * @param routes - array list of new routes
     */
    private void saveNewData(ArrayList<RouteSharing> routeSharings, ArrayList<Route> routes) {
        // if running - restart
        if (sLoading && (mDownloadTask != null)) {
            mDownloadTask.cancel(true);
        }
        mDownloadTask = new DownloadTask(routeSharings, routes);
        mDownloadTask.execute();
    }

    /**
     * Download data and images to local data stores
     */
    private class DownloadTask extends AsyncTask<Void, Void, Boolean> {

        private ArrayList<RouteSharing> mRouteSharings;
        private ArrayList<Route> mNewRoutes;
        private SQLiteDatabase mConnection;

        public DownloadTask(ArrayList<RouteSharing> routeSharings, ArrayList<Route> routes){
            this.mRouteSharings = routeSharings;
            this.mNewRoutes = routes;
            this.mConnection = DbHelper.getInstance(mContext).getWritableDatabase();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            sLoading = true;

            // download route sharing
            boolean routesSharingLoaded = RouteSharingTable
                    .batchLoadRouteSharing(mContext.getApplicationContext(), mRouteSharings, true);
            // download routes and images
            boolean routesLoaded = RouteTable
                    .loadRoutes(mContext.getApplicationContext(), mNewRoutes, true);
            RouteTable.downloadRouteImages(mContext.getApplicationContext(), mNewRoutes);
            // close current connection
            mConnection.close();
            return (routesSharingLoaded || routesLoaded);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            //update data
            if (result) {
                refreshAdapter();
            }
            mSwipeRefreshLayout.setRefreshing(false);
            sLoading = false;
        }
    }

    /**
     * update recycler's adapter
     */
    private void refreshAdapter() {
        mRoutes = RoutesManager.getAllRoutes(mContext.getApplicationContext(), mUserId);
        // if data has been changed - update adapter
        mRoutesAdapter.setRoutes(mRoutes);
        mRoutesAdapter.notifyDataSetChanged();

        // hide progress bars
        mSwipeRefreshLayout.setRefreshing(false);
        hideProgressBar();
    }

}
