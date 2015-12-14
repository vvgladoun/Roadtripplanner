package xyz.roadtripplanner.routes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.ImageTable;
import xyz.roadtripplanner.database.PlaceTable;
import xyz.roadtripplanner.database.PlaceTagTable;
import xyz.roadtripplanner.database.RoutePointTable;
import xyz.roadtripplanner.database.SynchronizeDB;
import xyz.roadtripplanner.model.Place;
import xyz.roadtripplanner.places.PlacesFragment;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.NetworkHelper;
import xyz.roadtripplanner.utilities.OnStartDragListener;
import xyz.roadtripplanner.utilities.SimpleItemTouchHelperCallback;
import xyz.roadtripplanner.utilities.Toolbox;

/**
 * Show route details
 * (Title and list of places)
 *
 * @author xyz
 */
public class RouteDetailsFragment extends Fragment
        implements View.OnClickListener, OnStartDragListener{

    private static final String TAG = RouteDetailsFragment.class.getSimpleName();
    public static final String EXTRA_ROUTE_ID = "ID_ROUTE";
    public static final String EXTRA_ROUTE_NAME = "ROUTE_NAME";
    private Context mContext;

    // to get route point's by route
    private int mRouteId;
    private String mRouteName;
    // aray list for adapter
    private ArrayList<Place> mPlaces;
    // swipe container for the implementation of the swipe-to-refresh pattern
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ItemTouchHelper mItemTouchHelper;
    // widgets
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFAB;
    // custom adapter
    private RoutePlacesAdapter mRoutePlacesAdapter;
    // task status
    public static boolean sLoading;
    private RefreshTask mRefreshTask;


    /**
     * Create new instance of fragment with route id as an argument
     *
     * @param routeId - route's uid
     * @return new fragment
     */
    public static RouteDetailsFragment newInstance(int routeId, String routeName) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ROUTE_ID, routeId);
        args.putString(EXTRA_ROUTE_NAME, routeName);
        RouteDetailsFragment fragment = new RouteDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save current route's id
        outState.putInt(EXTRA_ROUTE_ID, mRouteId);
        outState.putString(EXTRA_ROUTE_NAME, mRouteName);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use activity's callback to:
        mContext = getContext();

        //get args from bundle
        mRouteId = getArguments().getInt(EXTRA_ROUTE_ID);
        mRouteName = getArguments().getString(EXTRA_ROUTE_NAME);

        // retain instance and show menu
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(mRouteName);
        View routesView = inflater.inflate(R.layout.routes_places_fragment, container, false);
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
        mPlaces = new ArrayList<>();
        mRoutePlacesAdapter = new RoutePlacesAdapter(getActivity(), mPlaces, mRouteId);
        mRecyclerView.setAdapter(mRoutePlacesAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // add item touch listener
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mRoutePlacesAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        // find swipe container and add new listener on swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) container.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAdapter();
            }
        });
        // get FAB
        mFAB = (FloatingActionButton) container.findViewById(R.id.route_places_fab);
        mFAB.setOnClickListener(this);
        // get progress bar
        mProgressBar = (ProgressBar) container.findViewById(R.id.route_places_progress);
        if (mRoutePlacesAdapter.getItemCount() == 0) {
            showProgressBar();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.route_places_fab) {
            // open places fragment with current route id
            PlacesFragment placesFragment = PlacesFragment.newInstance(mRouteId);
            if (getContext() instanceof ActivityCallback) {
                ActivityCallback callbackActivity = (ActivityCallback) getContext();
                callbackActivity.changeFragment(placesFragment);
            }
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
            mRefreshTask.cancel(true);
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


    /**
     * stop animation for swipe-to-refresh pattern
     */
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

        // update adapter's data
        refreshAdapter();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    /**
     * Start refresh adapter task if not running
     */
    private void refreshAdapter(){
        if (!sLoading) {
            mRefreshTask = new RefreshTask();
            mRefreshTask.execute();
        }
    }


    /**
     * Download route's places and images
     */
    private class RefreshTask extends AsyncTask<Void, Void, Integer> {

        // status codes for the background task
        private static final int STATUS_NO_CONNECTION = 0;
        private static final int STATUS_DATA_LOADED = 1;
        private static final int STATUS_NO_DATA = 2;
        private static final int STATUS_NO_CHANGES = 3;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sLoading = true;
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            if (Toolbox.DEBUG) {
                Log.d(TAG, "refresh task started");
            }
            // set default status
            int loadingstatus = STATUS_NO_CONNECTION;
            // update database from web server
            Context appscontext = mContext.getApplicationContext();
            // check connection
            boolean connected = NetworkHelper.checkConnection(appscontext);
            // if connected to the internet
            if (connected) {

                int maxPlaceId = DbHelper.getMaxTableId(appscontext, PlaceTable.TABLE_NAME);
                int currentMaxId = DbHelper.getMaxTableId(appscontext, RoutePointTable.TABLE_NAME);
                int maxPlaceTagId = DbHelper.getMaxTableId(appscontext, PlaceTagTable.TABLE_NAME);
                int maxImageID = DbHelper.getMaxTableId(appscontext, ImageTable.TABLE_NAME);
                if (Toolbox.DEBUG) {
                    Log.d(TAG, "Updating data(from): place " + maxPlaceId
                            + ", route point " + currentMaxId + ", tag " + maxPlaceTagId
                            + ", image " + maxImageID);
                }
                SynchronizeDB.synchronizePlace(appscontext, maxPlaceId);
                SynchronizeDB.synchronizeRoutePoint(appscontext, currentMaxId);
                SynchronizeDB.synchronizePlaceTag(appscontext, maxPlaceTagId);
                // load new images
                SynchronizeDB.synchronizePlaceImage(appscontext, maxImageID);
                SynchronizeDB.syncPlacesImages(appscontext, maxImageID);

                // check if local database was updated
                int newMaxId = DbHelper.getMaxTableId(appscontext, RoutePointTable.TABLE_NAME);
                if (newMaxId > currentMaxId) {
                    //update array list from database
                    mPlaces = RoutesManager.getArrayRoutePoints(appscontext, mRouteId);
                    loadingstatus = STATUS_DATA_LOADED;
                } else if (newMaxId == 0) {
                    // no places defined for the route
                    loadingstatus = STATUS_NO_DATA;
                } else {
                    //nothing changes
                    loadingstatus = STATUS_NO_CHANGES;
                    if (mPlaces.size()==0) {
                        mPlaces = RoutesManager.getArrayRoutePoints(appscontext, mRouteId);
                    }
                }
            }
            if (Toolbox.DEBUG) {
                Log.d(TAG, "Data updating status "
                        + "(0 - no internet, 1 - ok, 2 - no data, 3 - no changes): "
                        + loadingstatus);
            }
            return loadingstatus;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            // stop all running progress bars
            mSwipeRefreshLayout.setRefreshing(false);
            hideProgressBar();

            switch (result) {
                case STATUS_NO_CONNECTION:
                    Snackbar.make(mFAB, "No internet Access, Check your internet connection.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                case STATUS_NO_DATA:
                    Snackbar.make(mFAB, "No places were defined for this route",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                case STATUS_DATA_LOADED:
                    //update adapter
                    mRoutePlacesAdapter.setPlaces(mPlaces);
                    mRoutePlacesAdapter.notifyDataSetChanged();
                    break;
                case STATUS_NO_CHANGES:
                    // if initial load, set adapter
                    if (mRoutePlacesAdapter.getItemCount() == 0) {
                        mRoutePlacesAdapter.setPlaces(mPlaces);
                        mRoutePlacesAdapter.notifyDataSetChanged();
                    }
            }

            // reset status
            sLoading = false;
        }
    }

}
