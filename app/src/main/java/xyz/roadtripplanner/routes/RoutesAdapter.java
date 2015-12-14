package xyz.roadtripplanner.routes;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.map.MapFragment;
import xyz.roadtripplanner.model.Route;
import xyz.roadtripplanner.utilities.APIConfig;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.NetworkHelper;
import xyz.roadtripplanner.utilities.Toolbox;
import xyz.roadtripplanner.utilities.VolleyHelper;

/**
 * Recycler view adapter for Routes
 *
 * @author xyz
 */
public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder> {

    private static final String TAG = RoutesFragment.class.getSimpleName();

    private ArrayList<Route> mRoutes;
    private static Context sContext;

    /**
     * Adapter's constructor class
     *
     * @param context app's context
     * @param routes - array list of routes
     */
    public RoutesAdapter(Context context, ArrayList<Route> routes) {
        mRoutes = routes;
        sContext = context;
    }

    @Override
    public RoutesAdapter.RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_list_item, parent, false);

        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder viewHolder, int position) {

        // set text fields
        viewHolder.mNameText.setText(mRoutes.get(position).getName());
        viewHolder.mDescText.setText(mRoutes.get(position).getDescription());

        // get route's id
        int routeId = mRoutes.get(position).getId();

        // add image (using Google Picasso
        String imageWebURL = mRoutes.get(position).getImagePath().trim();
        if (!(imageWebURL.equals(""))) {
            // get image path from external storage
            String imagePath = sContext.getFilesDir().getPath()
                    + "/route_" + routeId + Toolbox.getFileExtension(imageWebURL);
            File imageFile = new File(imagePath);

            if (Toolbox.DEBUG) {
                Log.d(TAG, "Loading image: " + imagePath);
            }
            if (imageFile.exists()) {
                //load and crop image to fit the image view
                Picasso.with(sContext).load(imageFile)
                        .fit()
                        .centerCrop()
                        .into(viewHolder.mRouteImage);
            } else {
                viewHolder.mRouteImage.setImageResource(R.drawable.intro_image);
            }
        } else {
            //Show default image
            viewHolder.mRouteImage.setImageResource(R.drawable.intro_image);
        }

    }


    @Override
    public int getItemCount() {
        return mRoutes.size();
    }

    /**
     *
     * @return adapter's array list
     */
    public ArrayList<Route> getRoutes() {
        return mRoutes;
    }

    /**
     *
     * @param mRoutes array list with content for adapter
     */
    public void setRoutes(ArrayList<Route> mRoutes) {
        this.mRoutes = mRoutes;
    }

    /**
     * Custom view holder for routes' adapter
     */
    public class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mNameText;
        protected TextView mDescText;
        protected ImageView mRouteImage;

        // action buttons
        protected Button mEditButton;
        protected Button mDeleteButton;
        protected Button mMapButton;

        /**
         * view holder's constructor
         *
         * @param itemView - layout view for an item
         */
        public RouteViewHolder(View itemView) {
            super(itemView);
            // init widgets
            mNameText = (TextView) itemView.findViewById(R.id.item_text_name);
            mDescText = (TextView) itemView.findViewById(R.id.item_text_description);
            mRouteImage = (ImageView) itemView.findViewById(R.id.routes_card_image);
            mEditButton = (Button) itemView.findViewById(R.id.btnRoutesEdit);
            mDeleteButton = (Button) itemView.findViewById(R.id.btnRoutesDelete);
            mMapButton = (Button) itemView.findViewById(R.id.btnRoutesMap);

            // get click on the whole card
            itemView.setOnClickListener(this);
            // catch click on action buttons
            mEditButton.setOnClickListener(this);
            mDeleteButton.setOnClickListener(this);
            mMapButton.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            //check if network is available (otherwise - no actions)
            boolean isConnected = NetworkHelper.checkConnection(v.getContext());
            int position = getAdapterPosition();

            if (position > -1) {
                Route route = mRoutes.get(position);
                switch (v.getId()) {
                    case R.id.btnRoutesEdit:
                        if (isConnected) {
                            // edit route description
                            if (v.getContext() instanceof ActivityCallback) {
                                ActivityCallback callbackActivity = (ActivityCallback) v.getContext();
                                callbackActivity.changeFragment(CreateRouteFragment.newInstance(route.getId()));
                            }
                        } else {
                            // show no connection message
                            Snackbar.make(v, "No network connection", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        break;
                    case R.id.btnRoutesDelete:
                        if (isConnected) {
                            //remove item
                            removeAt(position, v);
                        } else {
                            // show no connection message
                            Snackbar.make(v, "No network connection", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        break;
                    case R.id.btnRoutesMap:
                        if (isConnected) {
                            // show route on map
                            if (v.getContext() instanceof ActivityCallback) {
                                ActivityCallback callbackActivity = (ActivityCallback) v.getContext();
                                callbackActivity.changeFragment(MapFragment.newInstance(route.getId()));
                            }
                        } else {
                            // show no connection message
                            Snackbar.make(v, "No network connection", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        break;
                    default:
                        // on click open route details' fragment
                        Fragment routeDetailsFragment = RouteDetailsFragment.newInstance(route.getId(), route.getName());
                        if (v.getContext() instanceof ActivityCallback) {
                            ActivityCallback callbackActivity = (ActivityCallback) v.getContext();
                            callbackActivity.changeFragment(routeDetailsFragment);
                        }
                }
            }
        }

    }

    /**
     * Remove item from adapter, db and web server
     * @param position - position in recycler view
     * @param v - current view (card)
     */
    public void removeAt(int position, View v) {
        //get route's uid
        int routeId = mRoutes.get(position).getId();

        if (NetworkHelper.checkConnection(sContext)) {
            // request remove on web server
            if (Toolbox.DEBUG) {
                Log.d(TAG, "rmoving object: " + position);
            }
            makeVolleyRequestRemove(routeId, position);

        } else {
            Snackbar.make(v, "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }


    /**
     * Remove route data on server using Volley
     *
     * @param routeId - route's id
     * @param position - position in adapter
     */
    private void makeVolleyRequestRemove(final int routeId, final int position){

        // Tag used to cancel the request
        String tagRequestLogin = "REQ_REMROUTE";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.REMOVE_ROUTE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if error tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        //deleted successfully: remove from db and adapter
                        removeRoute(routeId, position);

                    } else {
                        // Error on web server. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Log.e(TAG, "Web Server Error: " + errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // log error message
                    Log.e(TAG, "Json Error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Routes Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                // add user's id
                params.put("routeid", String.valueOf(routeId));
                return params;
            }
        };
        // Adding request to request queue
        VolleyHelper.getInstance(sContext).addToRequestQueue(strReq, tagRequestLogin);
    }

    /**
     * Start async task to remove route's data
     *
     * @param routeId - route's uid
     */
    private void removeRoute(int routeId, int position) {
        RemoveRouteTask removeTask = new RemoveRouteTask(routeId, position);
        removeTask.execute();
    }

    /**
     * Async task to remove route (and sharing) from database
     */
    private class RemoveRouteTask extends AsyncTask<Void, Void, Void> {

        private int mRouteId;
        private int mPosition;

        public RemoveRouteTask(int routeId, int position){
            // set variables
            this.mRouteId = routeId;
            this.mPosition = position;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // remove from database
            RoutesManager.removeRoute(sContext, mRouteId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // remove from adapter and notify data changed
            mRoutes.remove(mPosition);
            notifyItemRemoved(mPosition);
            notifyItemRangeChanged(mPosition, mRoutes.size());
        }
    }

}
