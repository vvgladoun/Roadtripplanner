package xyz.roadtripplanner.places;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.DAO.AddressDAOImplementation;
import xyz.roadtripplanner.database.DAO.ImageDAOImplementation;
import xyz.roadtripplanner.database.DAO.PlaceCommentDAOImplementation;
import xyz.roadtripplanner.database.DAO.PlaceDAOImplementation;
import xyz.roadtripplanner.database.ToWebDB;
import xyz.roadtripplanner.login.LoginFragment;
import xyz.roadtripplanner.model.Address;
import xyz.roadtripplanner.model.Comment;
import xyz.roadtripplanner.model.Place;
import xyz.roadtripplanner.model.PlaceImage;
import xyz.roadtripplanner.utilities.PreferencesManagement;
import xyz.roadtripplanner.utilities.Toolbox;

/**
 * Fragment with place details
 * @author xyz
 */
public class PlaceDetailsFragment extends Fragment {

    public static final String EXTRA_PLACE_ID = "PLACE_ID";

    // UI references.
    private EditText mCommentView;
    private TextView mPlaceName;
    private TextView mPlaceAddress;
    private TextView mPlaceDescription;
    private TextView mCommentTitle;
    private ListView mCommentList;
    private ImageView mPlaceImage;

    private int mPlaceId;
    private int mUserId;

    private View mProgressView;
    private View mPlaceFormView;

    private static final String TAG = LoginFragment.class.getSimpleName();

    /**
     * Create new instance of the fragment with defined route's uid
     *
     * @param routeId route's uid
     * @return created fragment
     */
    public static PlaceDetailsFragment newInstance(int routeId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_PLACE_ID, routeId);
        PlaceDetailsFragment fragment = new PlaceDetailsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get fragment layout
        View fragment_view = inflater.inflate(R.layout.place_details_fragment, container, false);
        mPlaceId = getArguments().getInt(EXTRA_PLACE_ID);
        mUserId = PreferencesManagement.getUserId(getContext());
        //mUserId = 2;
        initViewElements(fragment_view);
        return fragment_view;
    }

    /**
     * Init view's elements
     *
     * @param container - source view
     */
    private void initViewElements(View container){
        // find widgets and views
        mCommentView = (EditText) container.findViewById(R.id.newComment);
        mPlaceName = (TextView)container.findViewById(R.id.placeDetailsName);
        mPlaceAddress = (TextView)container.findViewById(R.id.placeDetailsAddress);
        mPlaceDescription = (TextView)container.findViewById(R.id.placeDetailsDescription);
        mPlaceImage = (ImageView)container.findViewById(R.id.placeDetailsImage);
        mCommentList = (ListView)container.findViewById(R.id.lvComments);
        mCommentTitle = (TextView)container.findViewById(R.id.commentTitle);
        mProgressView = container.findViewById(R.id.placeDetailsProgress);
        mPlaceFormView = container.findViewById(R.id.place_details_container);
        final Button mAddComment = (Button) container.findViewById(R.id.btnAddComment);
        if (mUserId < 1){
            mCommentView.setVisibility(View.GONE);
            mAddComment.setVisibility(View.GONE);
        }
        fillDetails();
        // define button's action
        mAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                addComment();
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPlaceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPlaceFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPlaceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPlaceFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void fillDetails(){
        Place place = (new PlaceDAOImplementation(getContext())).findPlaceById(mPlaceId);
        AddressDAOImplementation adi = new AddressDAOImplementation(getContext());

        //Address address = adi.findAddressById(place.getAddressId());
        Address address = (new AddressDAOImplementation(getContext())).findAddressById(place.getAddressId());
        PlaceImage placeImage = (new ImageDAOImplementation(getContext())).findImageByPlaceId(mPlaceId);
        mPlaceName.setText(place.getShortDescription());
        mPlaceDescription.setText(place.getFullDescription());
        mPlaceAddress.setText(getStringAddress(address));


        // add image (using Google Picasso
        String imageWebURL = placeImage.getImagePath().trim();
        if (!(imageWebURL.equals(""))) {
            // get image path from external storage
            //String imagePath = Environment.getExternalStorageDirectory().getPath()
            String imagePath = getContext().getFilesDir().getPath()
                    + "/place_" + place.getId() + Toolbox.getFileExtension(imageWebURL);
            File imageFile = new File(imagePath);
            //Uri imageUri = Uri.parse(imagePath);

            if (Toolbox.DEBUG) {
                Log.d(TAG, "Show image (" + place.getShortDescription() + "): " + imagePath);
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

        setListViewAdapter(true);

    }

    public void setListViewAdapter(boolean reload){
        if (reload){
            mCommentList.setAdapter(null);
            GetCommentsList mGetComments = new GetCommentsList();
            mGetComments.execute();
        }
    }

    private String getStringAddress(Address address){
        String placeAddress = "";
        if(!TextUtils.isEmpty(address.getBuilding())){
            placeAddress += address.getBuilding() + ", ";
        }
        if(!TextUtils.isEmpty(address.getStreet())){
            placeAddress += address.getStreet() + ", ";
        }
        if(!TextUtils.isEmpty(address.getCity())){
            placeAddress += address.getCity();
            if(!TextUtils.isEmpty(address.getPostIndex())){
                placeAddress += " " + address.getPostIndex() + ", ";
            } else {
                placeAddress += ", ";
            }
        }
        if(!TextUtils.isEmpty(address.getCountry())){
            placeAddress += address.getCountry();
        }
        return placeAddress;
    }

    private void addComment(){
        String commentText = mCommentView.getText().toString();
        ToWebDB.insertCommentWeb(getContext(), mPlaceId, mUserId, commentText, this);
    }

    /**
     * Async task to get the list of ccomments
     */
    class GetCommentsList extends AsyncTask<Void, Void, ArrayList<Comment>> {

        ProgressDialog mProgressDialog;
        //creation and showing of progress dialog
        @Override
        protected void onPreExecute(){
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("Loading...");
            mProgressDialog.setMessage("Loading of comments, please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<Comment> doInBackground(Void... params) {
            //get all comments from DB
            ArrayList<Comment> commentList = (new PlaceCommentDAOImplementation(getContext())).findCommentByPlaceId(mPlaceId);
            return commentList;
        }

        @Override
        protected void onPostExecute(ArrayList<Comment> commentList){
            //close progress dialog
            mProgressDialog.dismiss();
            mCommentList.setAdapter(new CommentAdapter(getContext(), R.layout.place_details_fragment, commentList));
            Helper.getListViewSize(mCommentList);
            if(commentList == null){
                if (mUserId > 0){
                    mCommentTitle.setText("Be the first to comment");
                } else {
                    mCommentTitle.setVisibility(View.GONE);
                }
            } else {
                mCommentTitle.setText("Comments");
            }
        }
    }

}
