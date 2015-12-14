package xyz.roadtripplanner.places;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import xyz.roadtripplanner.database.DbHelper;
import xyz.roadtripplanner.database.RoutePointTable;
import xyz.roadtripplanner.database.SynchronizeDB;
import xyz.roadtripplanner.model.Place;
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
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.RoutePlaceViewHolder> {

    private static final String TAG = PlacesAdapter.class.getSimpleName();
    // adapter content's array list
    private ArrayList<Place> mPlaces;
    // uid of the route to add place to
    private int mRouteId;
    private static Context sContext;

    /**
     * Adapter's constructor class
     *
     * @param context app's context
     * @param places - array list of route's places
     */
    public PlacesAdapter(Context context, ArrayList<Place> places, int routeId) {
        mPlaces = places;
        mRouteId = routeId;
        sContext = context;
    }

    @Override
    public PlacesAdapter.RoutePlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            //String imagePath = Environment.getExternalStorageDirectory().getPath()
            String imagePath = sContext.getFilesDir().getPath()
                    + "/place_" + routeId + Toolbox.getFileExtension(imageWebURL);
            File imageFile = new File(imagePath);
            //Uri imageUri = Uri.parse(imagePath);

            if (Toolbox.DEBUG) {
                Log.d(TAG, "Show image (" + currentPlace.getShortDescription() + "): " + imagePath);
            }
            if (imageFile.exists()) {
                //if (!(imageWebURL.equals(""))){
                //load and crop image to fit the image view
                //Picasso.with(sContext).load(imageWebURL)
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
    public class RoutePlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // item layout's widgets
        protected TextView mNameText;
        protected TextView mTagText;
        protected ImageView mPlaceImage;
        // button to add to the route
        protected ImageButton mAddButton;


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
            // if route is defined show add button
            if (mRouteId > 0) {
                mAddButton = (ImageButton) itemView.findViewById(R.id.item_place_add_btn);
                mAddButton.setVisibility(View.VISIBLE);
                mAddButton.setOnClickListener(this);
            }

            // get click on the whole card
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            //check if network is available (otherwise - no actions)
            int position = getAdapterPosition();

            if (position > -1) {
                if (v.getId() == R.id.item_place_add_btn) {
                    // add place to the route
                    removeAt(position, v);
                } else {
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

    }

    /**
     * Remove item from adapter
     * and add place to route in db and web server
     *
     * @param position - position in recycler view
     * @param v - current view (card)
     */
    public void removeAt(int position, View v) {

        //get route's uid
        int placeId = mPlaces.get(position).getId();

        // remove from web server and database
        if (NetworkHelper.checkConnection(sContext)) {
            // remove from adapter and notify data changed
            mPlaces.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mPlaces.size());

            // request remove on web server
            if (Toolbox.DEBUG) {
                Log.d(TAG, "removing object: " + position);
            }
            makeVolleyRequestAddPlace(mRouteId, placeId);

        } else {
            Snackbar.make(v, "No network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    /**
     * Add place to the route on server using Volley
     *
     * @param routeId - route's id
     * @param placeId - route place's id
     */
    private void makeVolleyRequestAddPlace(final int routeId, final int placeId){

        // Tag used to cancel the request
        final String tagRequestLogin = "REQ_ADDPLACE";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.ADD_ROUTES_PLACE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if error tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        //added successfully: update data in the database
                        addPlace();

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
     * Start async task to update route's points
     */
    private void addPlace() {
        AddPlaceTask addTask = new AddPlaceTask();
        addTask.execute();
    }

    /**
     * Async task to update route points from database
     */
    private class AddPlaceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // update route points from database
            SynchronizeDB.synchronizeRoutePoint(sContext
                    , DbHelper.getMaxTableId(sContext, RoutePointTable.TABLE_NAME));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

}
