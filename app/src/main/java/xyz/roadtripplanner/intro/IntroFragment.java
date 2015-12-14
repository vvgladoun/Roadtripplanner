package xyz.roadtripplanner.intro;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.DBCreation;
import xyz.roadtripplanner.login.LoginFragment;
import xyz.roadtripplanner.login.RegisterFragment;
import xyz.roadtripplanner.places.PlacesFragment;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.NetworkHelper;
import xyz.roadtripplanner.utilities.PreferencesManagement;

/**
 *  Intro page and checking user credentials
 * and internet connection
 *
 *  If user was logged in before, re-direct to routes
 * else show intro page
 *
 * @author xyz
 */
public class IntroFragment extends Fragment
        implements View.OnClickListener {

    // use class name as TAG
    private static final String TAG = IntroFragment.class.getSimpleName();

    // layout's widgets
    private Button mSignInBtn;
    private Button mRegisterBtn;
    private Button mSkipBtn;
    private ScrollView mIntroScrollView;
    // status text and bar container
    private RelativeLayout mStatusContainer;

    // task status
    public static boolean sLoading;

    private DownloadTask mLoadTask;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get current context
        mContext = getContext();
        // clear call back
        if (mContext instanceof ActivityCallback) {
            ActivityCallback callbackActivity = (ActivityCallback)mContext;
            callbackActivity.clearBackStack();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get fragment layout
        View fragment_view = inflater.inflate(R.layout.intro_fragment, container, false);
        //return super.onCreateView(inflater, container, savedInstanceState);
        initViewElements(fragment_view);

        return fragment_view;
    }

    /**
     * Init view's elements
     *
     * @param container - source view
     */
    private void initViewElements(View container) {
        // find widgets and views
        mSignInBtn = (Button) container.findViewById(R.id.btnIntroSignIn);
        mRegisterBtn = (Button) container.findViewById(R.id.btnIntroRegister);
        mSkipBtn = (Button) container.findViewById(R.id.btnIntroSkip);
        // progress bar and layout container
        mIntroScrollView = (ScrollView) container.findViewById(R.id.intro_container);
        mStatusContainer = (RelativeLayout) container.findViewById(R.id.intro_status_block);

        // add on click listeners
        mSignInBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        mSkipBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Context context = getContext();
        // check if internet connection is on
        if (NetworkHelper.checkConnection(context)) {
            // check if initial loading needed
            boolean dataloaded = PreferencesManagement.getDataLoaded(context);
            if (!dataloaded) {
                // show progress bar
                showProgressBar();

                // start initial load task
                startLoading();
            }
            startLoading();
        }
    }

    @Override
    public void onStop() {
        // stop task if running
        if (mLoadTask != null){
            mLoadTask.cancel(true);
            sLoading = false;
        }
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        //redirect to the page depending on button's id
        switch (v.getId()) {
            // sign in button
            case R.id.btnIntroSignIn:
                if (v.getContext() instanceof ActivityCallback) {
                    ActivityCallback callbackActivity = (ActivityCallback) v.getContext();
                    callbackActivity.changeFragment(new LoginFragment());
                }
                break;
            //register button
            case R.id.btnIntroRegister:
                if (v.getContext() instanceof ActivityCallback) {
                    ActivityCallback callbackActivity = (ActivityCallback) v.getContext();
                    callbackActivity.changeFragment(new RegisterFragment());
                }
                break;
            //skip and show places button
            case R.id.btnIntroSkip:
                if (v.getContext() instanceof ActivityCallback) {
                    ActivityCallback callbackActivity = (ActivityCallback) v.getContext();
                    callbackActivity.changeFragment(PlacesFragment.newInstance(0));
                }
                break;
        }
    }


    /**
     * Show only progress bar (while initial loading)
     */
    private void showProgressBar() {
        mIntroScrollView.setVisibility(View.GONE);
        //mProgressBar.setVisibility(View.VISIBLE);
        mStatusContainer.setVisibility(View.VISIBLE);
    }

    /**
     * Hide progress bar and show layout
     */
    private void hideProgressBar(){
        //mProgressBar.setVisibility(View.GONE);
        mStatusContainer.setVisibility(View.GONE);
        mIntroScrollView.setVisibility(View.VISIBLE);
    }

    /**
     * start loading task (if running - restart)
     */
    private void startLoading() {
        if (sLoading && (mLoadTask != null)){
            mLoadTask.cancel(true);
        }

        // start new download task
        mLoadTask = new DownloadTask();
        mLoadTask.execute();
    }


    /**
     * Download all data and images to local data stores
     */
    private class DownloadTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sLoading = true;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            // load data from web server
            DBCreation.fillDatabaseSequence(mContext.getApplicationContext());
            PreferencesManagement.setDataLoaded(mContext, true);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            //update data loaded status

            hideProgressBar();
            // reset status
            sLoading = false;
        }
    }


}
