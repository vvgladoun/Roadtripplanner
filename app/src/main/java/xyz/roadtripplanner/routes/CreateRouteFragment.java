package xyz.roadtripplanner.routes;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.DAO.RouteDAOImplementation;
import xyz.roadtripplanner.database.ToWebDB;
import xyz.roadtripplanner.model.Route;

/**
 * Create new route or edit existing one
 *
 * @author xyz
 */
public class CreateRouteFragment extends Fragment {

    public static final String EXTRA_ROUTE_ID = "ROUTE_ID";

    private EditText mRouteName;
    private EditText mRouteDescription;
    private EditText mRouteImage;
    private Button mCreate;
    private View mProgressView;
    private View mRouteFormView;

    private int mRouteId;

    public static CreateRouteFragment newInstance(int routeId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ROUTE_ID, routeId);
        CreateRouteFragment fragment = new CreateRouteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // get fragment layout
        View fragment_view = inflater.inflate(R.layout.create_route_fragment, container, false);
        mRouteName = (EditText)fragment_view.findViewById(R.id.route_name);
        mRouteDescription = (EditText)fragment_view.findViewById(R.id.route_description);
        mRouteImage = (EditText)fragment_view.findViewById(R.id.route_image);
        mRouteFormView = fragment_view.findViewById(R.id.create_route_form);
        mProgressView = fragment_view.findViewById(R.id.create_route_progress);
        mCreate = (Button)fragment_view.findViewById(R.id.create_route_button);
        Button cancel = (Button) fragment_view.findViewById(R.id.cancel_button);

        mRouteId = getArguments().getInt(EXTRA_ROUTE_ID);

        if(mRouteId > 0){
            fillViewElements();
        }

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                if (checkFields()) {
                    showProgress(true);
                    if (mRouteId > 0) {
                        routeEditSave();
                    } else {
                        routeCreation();
                    }
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFunction();
            }
        });

        return fragment_view;
    }

    private void cancelFunction(){
        getActivity().onBackPressed();
//        if (getContext() instanceof ActivityCallback) {
//            ActivityCallback callbackActivity = (ActivityCallback) getContext();
//
//            //clear back stack (to avoid return by back button press
//            callbackActivity.clearBackStack();
//            // launch routes fragment
//            callbackActivity.changeFragmentAsFirst(new RoutesFragment());
//        }
    }

    private void fillViewElements(){
        Route route = (new RouteDAOImplementation(getContext())).findRouteById(mRouteId);
        mRouteName.setText(route.getName());
        mRouteDescription.setText(route.getDescription());
        mRouteImage.setText(route.getImagePath());
        mCreate.setText("Save");
    }

    /**
     * check route name
     * @return true if it is not empty
     */
    private boolean checkFields(){
        String routeName = mRouteName.getText().toString();
        if(routeName.equals("") || routeName.equals("Route name")){
            mRouteName.setError(getString(R.string.error_field_required));
            mRouteName.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * create new route and add it to DB
     */
    private void routeCreation(){
        String routeName = mRouteName.getText().toString();
        String routeDescription = mRouteDescription.getText().toString();
        String routeImage = mRouteImage.getText().toString();
            if (routeImage.equals("")){
                routeImage = "image/defaultplace.jpg";
            } else if (!routeImage.substring(0, 3).equals("http")){
                routeImage = "image/defaultplace.jpg";
            }
            ToWebDB.insertRouteWeb(getContext(), routeName, routeDescription, routeImage, this);
    }

    /**
     * Save changes
     */
    private void routeEditSave(){
        String routeName = mRouteName.getText().toString();
        String routeDescription = mRouteDescription.getText().toString();
        String routeImage = mRouteImage.getText().toString();
        if (routeImage.equals("")){
            routeImage = "image/defaultplace.jpg";
        } else if (!routeImage.substring(0, 3).equals("http")){
            routeImage = "image/defaultplace.jpg";
        }

        ToWebDB.editRouteWeb(getContext(), mRouteId, routeName, routeDescription, routeImage, this);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        Log.d("PROGRESS", "show: " + show);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRouteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRouteFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRouteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRouteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
