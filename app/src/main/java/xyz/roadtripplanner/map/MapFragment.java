package xyz.roadtripplanner.map;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.DAO.AddressDAOImplementation;
import xyz.roadtripplanner.database.DAO.ImageDAOImplementation;
import xyz.roadtripplanner.database.DAO.PlaceDAOImplementation;
import xyz.roadtripplanner.database.DAO.RoutePointDAOImplementation;
import xyz.roadtripplanner.model.Place;
import xyz.roadtripplanner.model.PlaceImage;
import xyz.roadtripplanner.model.RoutePoint;
import xyz.roadtripplanner.utilities.Toolbox;

/**
 * Fragment with google ma
 *
 * @author xyz
 */
public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{

    public static final String EXTRA_ROUTE_ID = "ROUTE_ID";
    private static final String TAG = "MAP";
    private int mRouteId;

    private GoogleMap mMap;
    private Button goButton, showRouteButton, starNavigationButton;
    private EditText searchBox;
    private Marker searched = null;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    private int opened = -1;
    private TextView mPlaceTitle;
    private ImageView mPlaceImage;

    private ArrayList<Place> mPlaceList;
    private ArrayList<xyz.roadtripplanner.model.Address> mAddressList;
    private ArrayList<LatLng> markerPoints;
    private ArrayList<LatLng> routePoints;

    private SlidingDrawer mSlidingDrawer;

    /**
     * Create new instance of the fragment with defined route's uid
     *
     * @param routeId route's uid
     * @return created fragment
     */
    public static MapFragment newInstance(int routeId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ROUTE_ID, routeId);
        Log.d(TAG, "routeId = " + routeId);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //get args from bundle
        mRouteId = getArguments().getInt(EXTRA_ROUTE_ID);

        View fragment_view = inflater.inflate(R.layout.map_fragment_layout, container, false);
        initViewElements(fragment_view);
        Log.d(TAG, "create view");
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }catch (Exception e){
            Log.d("MAP: ", e.toString());
        }

        return fragment_view;
    }


    private void initViewElements(View container){

        Log.d(TAG, "init view elements");
        // find widgets and views
        mSlidingDrawer = (SlidingDrawer)container.findViewById(R.id.drawer);
        searchBox = (EditText) container.findViewById(R.id.EditTextSearchPlace);
        mPlaceTitle = (TextView) container.findViewById(R.id.placeTitle);
        mPlaceImage = (ImageView) container.findViewById(R.id.placePicture);
        //search
        goButton = (Button)container.findViewById(R.id.ButtonGo);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
                findPlace();
            }
        });

        //show route on the map
        showRouteButton = (Button)container.findViewById(R.id.ButtonShowRoute);
        showRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRouteOnMap();
            }
        });

        //open google maps navigation
        starNavigationButton = (Button)container.findViewById(R.id.ButtonStartNavigation);
        starNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNavigation(opened);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setLists();
        //Toast.makeText(getContext(), "points: " + mRoutePointsList.size(), Toast.LENGTH_LONG).show();

        mMap.setOnMarkerClickListener(this);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        markerPoints = new ArrayList<>();
        Log.d(TAG, "placeList size = " + mPlaceList.size());
        for(int i = 0; i < mPlaceList.size(); i++){
            xyz.roadtripplanner.model.Address a = mAddressList.get(i);
            Place p = mPlaceList.get(i);
            LatLng point = new LatLng(a.getLatitude(), a.getLongitude());
            builder.include(point);
            Marker mark = mMap.addMarker(new MarkerOptions().position(point).title(p.getShortDescription()));
            mHashMap.put(mark, i);
            markerPoints.add(point);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 800, 800, 10);
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(cu);

        //routePoints = new ArrayList<LatLng>();
        //routePoints = getRoutePoints(0, markerPoints);
    }

    private void setLists(){
        RoutePointDAOImplementation rpdi = new RoutePointDAOImplementation(getContext());
        PlaceDAOImplementation pdi = new PlaceDAOImplementation(getContext());
        AddressDAOImplementation adi = new AddressDAOImplementation(getContext());
        ArrayList<RoutePoint> routePointsList = new ArrayList<>();
        mPlaceList = new ArrayList<>();
        mAddressList = new ArrayList<>();
        routePointsList = rpdi.findRoutePointsByRouteId(mRouteId);
        for (int i = 0; i < routePointsList.size(); i++){
            Place place = pdi.findPlaceById(routePointsList.get(i).getPlaceId());
            xyz.roadtripplanner.model.Address address = adi.findAddressById(place.getAddressId());
            mPlaceList.add(place);
            mAddressList.add(address);
        }
        adi.close();
        pdi.close();
        rpdi.close();
    }

    private void findPlace(){
        //EditText searchBox = (EditText) findViewById(R.id.EditText1);
        String location = searchBox.getText().toString();

        Geocoder mGeocoder = new Geocoder(getContext());
        if (searched != null) {
            searched.remove();
            searched = null;
        }
        try {
            List<Address> addressList = mGeocoder.getFromLocationName(location, 1);
            Address mAddress = addressList.get(0);
            LatLng point = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
            searched = mMap.addMarker(new MarkerOptions().position(point).title(mAddress.getFeatureName()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 8));
        } catch (IOException e) {
            Toast.makeText(getContext(), "Not Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRouteOnMap(){
        routePoints = new ArrayList<>();
        routePoints = getRoutePoints(0, markerPoints);

        if(routePoints.size() >= 2){
            LatLng origin = routePoints.get(0);
            LatLng dest = routePoints.get(routePoints.size() - 1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private void openNavigation(int pos){
        String request = "http://maps.google.com/maps?daddr=" +
                mAddressList.get(pos).getLatitude() + "," +
                mAddressList.get(pos).getLongitude();

        String uri = String.format(Locale.ENGLISH, request);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException ex)
        {
            try
            {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            }
            catch(ActivityNotFoundException innerEx)
            {
                Toast.makeText(getContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    private ArrayList<LatLng> getRoutePoints(int start, ArrayList<LatLng> points){
        ArrayList<LatLng> routePoints = new ArrayList<>();
        int last, k;
        if (points.size() - start > 7){
            last = start + 7;
        } else {
            last = points.size();
        }
        k = 0;
        for(int i = start; i < last; i++){
            routePoints.add(k, points.get(i));
            k++;
        }
        return routePoints;
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i = 1;i < markerPoints.size() - 1; i++){
            LatLng point  = markerPoints.get(i);
            if(i == 1)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
    }


    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb  = new StringBuilder();

            String line;
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("URL: ", e.toString());
        }
        //close stream and connection
        if (iStream != null) {
            iStream.close();
        }
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        return data;
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(), "Route Calculation", "Internet Request...", true);
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getContext(), "Route Calculation", "Calculation in progress...", true);
        }
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            progressDialog.dismiss();
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private void hideSoftKeyboard(View v){
        InputMethodManager mIMManager = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mIMManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        int position = mHashMap.get(marker);
        if(!mSlidingDrawer.isOpened() || opened == position) {
            mSlidingDrawer.animateOpen();

        }
        opened = position;

        mPlaceTitle.setText(mPlaceList.get(position).getShortDescription());
        PlaceImage placeImage = (new ImageDAOImplementation(getContext()))
                .findImageByPlaceId(mPlaceList.get(position).getId());


        // add image (using Google Picasso
        String imageWebURL = placeImage.getImagePath().trim();
        if (!(imageWebURL.equals(""))) {
            // get image path from external storage
            //String imagePath = Environment.getExternalStorageDirectory().getPath()
            String imagePath = getContext().getFilesDir().getPath()
                    + "/place_" + mPlaceList.get(position).getId() + Toolbox.getFileExtension(imageWebURL);
            File imageFile = new File(imagePath);
            //Uri imageUri = Uri.parse(imagePath);

            if (Toolbox.DEBUG) {
                Log.d(TAG, "Show image (" + mPlaceList.get(position).getShortDescription() + "): " + imagePath);
            }
            if (imageFile.exists()) {
                //if (!(imageWebURL.equals(""))){
                //load and crop image to fit the image view
                //Picasso.with(sContext).load(imageWebURL)
                Picasso.with(getContext()).load(imageFile)
                        .fit()
                        .centerCrop()
                        .into(mPlaceImage);
            } else {
                mPlaceImage.setImageResource(R.drawable.intro_image);
            }
        } else {
            //Show default image
            mPlaceImage.setImageResource(R.drawable.intro_image);
        }

        return false;
    }
}