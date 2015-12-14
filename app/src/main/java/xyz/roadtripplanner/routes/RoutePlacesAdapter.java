package xyz.roadtripplanner.routes;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.model.Place;
import xyz.roadtripplanner.places.PlaceDetailsFragment;
import xyz.roadtripplanner.utilities.APIConfig;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.ItemTouchHelperAdapter;
import xyz.roadtripplanner.utilities.ItemTouchHelperViewHolder;
import xyz.roadtripplanner.utilities.NetworkHelper;
import xyz.roadtripplanner.utilities.Toolbox;
import xyz.roadtripplanner.utilities.VolleyHelper;

/**
 * Recycler view adapter for Routes
 *
 * @author xyz
 */
public class RoutePlacesAdapter
        extends RecyclerView.Adapter<RoutePlacesAdapter.RoutePlaceViewHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = RoutePlacesAdapter.class.getSimpleName();

    private int mRouteId;
    private ArrayList<Place> mPlaces;
    private static Context sContext;

    /**
     * Adapter's constructor class
     *
     * @param context app's context
     * @param places - array list of route's places
     */
    public RoutePlacesAdapter(Context context, ArrayList<Place> places, int routeId) {
        mRouteId = routeId;
        mPlaces = places;
        sContext = context;
    }

    @Override
    public RoutePlacesAdapter.RoutePlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_item, parent, false);

        return new RoutePlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RoutePlaceViewHolder viewHolder, int position) {

        // get current place
        Place currentPlace = mPlaces.get(position);

        // set text fields
        viewHolder.mNameText.setText(currentPlace.getShortDescription());
        viewHolder.mTagText.setText(currentPlace.getTag());

        // get route's id
        int routeId = currentPlace.getId();

        // add image (using Google Picasso
        String imageWebURL = currentPlace.getImagePath().trim();
        if (!(imageWebURL.equals(""))) {
            // get image path from external storage
            String imagePath = sContext.getFilesDir().getPath()
                    + "/place_" + routeId + Toolbox.getFileExtension(imageWebURL);
            File imageFile = new File(imagePath);

            if (Toolbox.DEBUG) {
                Log.d(TAG, "Loading image: " + imagePath);
            }
            if (imageFile.exists()) {
                //load and crop image to fit the image view
                Picasso.with(sContext).load(imageFile)
                        .fit()
                        .centerCrop()
                        .into(viewHolder.mPlaceImage);
            } else {
                viewHolder.mPlaceImage.setImageResource(R.drawable.intro_image);
            }
        } else {
            //Show default image
            viewHolder.mPlaceImage.setImageResource(R.drawable.intro_image);
        }

    }

    // ITEM TOUCH IMPLEMENTATION
    @Override
    public void onItemDismiss(int position) {
        // remove item in adapter
        // then request remove action on web server
        // and remove route's point from database
        removeAt(position);
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // change route points' places
        moveItem(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    /**
     *
     * @return adapter's array list with content
     */
    public ArrayList<Place> getRoutes() {
        return mPlaces;
    }

    /**
     *
     * @param places array list with content for adapter
     */
    public void setPlaces(ArrayList<Place> places) {
        this.mPlaces = places;
    }


    /**
     * Custom view holder for route's places
     */
    public class RoutePlaceViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, ItemTouchHelperViewHolder {

        protected TextView mNameText;
        protected TextView mTagText;
        protected ImageView mPlaceImage;


        /**
         * view holder's constructor
         *
         * @param itemView - layout view for an item
         */
        public RoutePlaceViewHolder(View itemView) {
            super(itemView);
            // init widgets
            mNameText = (TextView) itemView.findViewById(R.id.item_place_name);
            mTagText= (TextView) itemView.findViewById(R.id.item_place_tag);
            mPlaceImage = (ImageView) itemView.findViewById(R.id.item_place_image);

            // get click on the whole card
            itemView.setOnClickListener(this);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }


        @Override
        public void onClick(View v) {
            //check if network is available (otherwise - no actions)
            boolean isConnected = NetworkHelper.checkConnection(v.getContext());
            int position = getAdapterPosition();

            if (position > -1) {
                Place place = mPlaces.get(position);
                // on click open place details' fragment

                Fragment placeFragment = PlaceDetailsFragment.newInstance(place.getId());
                if (v.getContext() instanceof ActivityCallback) {
                    ActivityCallback callbackActivity = (ActivityCallback) v.getContext();
                    callbackActivity.changeFragment(placeFragment);
                }

            }
        }

    }

    /**
     * Remove item from adapter, db and web server
     *
     * @param position - position in recycler view
     */
    public void removeAt(int position) {

        //get place's uid
        int placeId = mPlaces.get(position).getId();

        // remove from adapter and notify data changed
        mPlaces.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPlaces.size());
        //notifyDataSetChanged();


        // remove from web server and database
        if (NetworkHelper.checkConnection(sContext)) {
            // request remove on web server
            if (Toolbox.DEBUG) {
                Log.d(TAG, "removing object: " + position);
            }
            makeVolleyRequestRemove(mRouteId, placeId);
        } else {
            // notify there's no connection
            Toast.makeText(sContext, "No network connection", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Move item in recycler view
     *
     * @param fromPosition - start position in adapter
     * @param toPosition - end position in adapter
     */
    public void moveItem(int fromPosition, int toPosition) {

        //get places' uid
        int sourcePlaceId = mPlaces.get(fromPosition).getId();
        int targetPlaceId = mPlaces.get(toPosition).getId();

        // remove from adapter and notify data changed
        Collections.swap(mPlaces, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        int minPosition = ((fromPosition > toPosition) ? toPosition : fromPosition);
        notifyItemRangeChanged(minPosition, mPlaces.size());

        // remove from web server and database
        if (NetworkHelper.checkConnection(sContext)) {
            // request remove on web server
            if (Toolbox.DEBUG) {
                Log.d(TAG, "moving item from " + fromPosition + " to " + toPosition);
            }
            makeVolleyRequestMove(mRouteId, sourcePlaceId, targetPlaceId);
        } else {
            // notify there's no connection
            Toast.makeText(sContext, "No network connection", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Remove place from the route on server using Volley
     *
     * @param routeId - route's id
     * @param sourcePlaceId - source route place's id
     * @param targetPlaceId - target route place's id
     */
    private void makeVolleyRequestMove(final int routeId, final int sourcePlaceId, final int targetPlaceId){

        // Tag used to cancel the request
        final String tagRequestLogin = "REQ_REMPLACE";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.MOVE_ROUTES_PLACE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if error tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        //deleted successfully: remove from db and adapter
                        movePlace(routeId, sourcePlaceId, targetPlaceId);
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
                params.put("sourceid", String.valueOf(sourcePlaceId));
                params.put("targetid", String.valueOf(targetPlaceId));
                return params;
            }
        };
        // Adding request to request queue
        VolleyHelper.getInstance(sContext).addToRequestQueue(strReq, tagRequestLogin);
    }


    /**
     * Remove place from the route on server using Volley
     *
     * @param routeId - route's id
     * @param placeId - route place's id
     */
    private void makeVolleyRequestRemove(final int routeId, final int placeId){

        // Tag used to cancel the request
        final String tagRequestLogin = "REQ_REMPLACE";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.REMOVE_ROUTES_PLACE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if error tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        //deleted successfully: remove from db and adapter
                        removePlace(routeId, placeId);
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
                params.put("placeid", String.valueOf(placeId));
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
    private void removePlace(int routeId, int placeId) {
        RemovePlaceTask removeTask = new RemovePlaceTask(routeId, placeId);
        removeTask.execute();
    }

    /**
     * Async task to remove route (and sharing) from database
     */
    private class RemovePlaceTask extends AsyncTask<Void, Void, Void> {

        private int mRouteId;
        private int mPlaceId;

        public RemovePlaceTask(int routeId, int placeId){
            // set variables
            this.mRouteId = routeId;
            this.mPlaceId = placeId;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // remove route point from database
            RoutesManager.removePlace(sContext, mRouteId, mPlaceId);
            return null;
        }
    }

    /**
     * Start async task to remove route's data
     *
     * @param routeId - route's uid
     */
    private void movePlace(int routeId, int sourcePlaceId, int targetPlaceId) {
        MovePlaceTask moveTask = new MovePlaceTask(routeId, sourcePlaceId, targetPlaceId);
        moveTask.execute();
    }

    /**
     * Async task to move place in route's order (in database)
     */
    private class MovePlaceTask extends AsyncTask<Void, Void, Void> {

        private int mRouteId;
        private int mSourcePlaceId;
        private int mTargetPlaceId;

        public MovePlaceTask(int routeId, int sourcePlaceId, int targetPlaceId){
            // set variables
            this.mRouteId = routeId;
            this.mSourcePlaceId = sourcePlaceId;
            this.mTargetPlaceId = targetPlaceId;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // remove route point from database
            RoutesManager.movePlace(sContext, mRouteId, mSourcePlaceId, mTargetPlaceId);
            return null;
        }
    }

}
