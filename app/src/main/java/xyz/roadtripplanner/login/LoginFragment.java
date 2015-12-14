package xyz.roadtripplanner.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.routes.RoutesFragment;
import xyz.roadtripplanner.utilities.APIConfig;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.PreferencesManagement;
import xyz.roadtripplanner.utilities.VolleyHelper;


/**
 * Sign in to the web server
 *
 * @author xyz
 */
public class LoginFragment extends Fragment {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private static final String TAG = LoginFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // get fragment layout
        View fragment_view = inflater.inflate(R.layout.login_fragment, container, false);
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
        mUsernameView = (EditText) container.findViewById(R.id.username);
        mPasswordView = (EditText) container.findViewById(R.id.password);
        mProgressView = container.findViewById(R.id.login_progress);
        mLoginFormView = container.findViewById(R.id.login_form);
        // define button's action
        Button login = (Button) container.findViewById(R.id.sign_in_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Attempts to sign in to the account specified by the login form.
     * If there are form errors (invalid, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // check login
            checkLogin(username, password);
        }
    }


    /**
     * Check input username
     *
     * @param username login
     * @return true if valid
     */
    private boolean isUsernameValid(String username) {
        //test logic
        return username.length() > 2;
    }

    /**
     * Check input password
     *
     * @param password user's password
     * @return true if valid
     */
    private boolean isPasswordValid(String password) {
        //test logic
        return password.length() > 2;
    }


    /**
     * verify login and password using Google Volley
     *
     * @param login - user's login
     * @param password - user's password
     */
    private void checkLogin(final String login, final String password) {
        // Tag used to cancel the request
        String tagRequestLogin = "REQ_LOGIN";

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIConfig.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                showProgress(false);

                try {
                    JSONObject jObj = new JSONObject(response);
                    // check if success tag found in JSON
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        int userId = jObj.getInt("uid");
                        //String email = jObj.getString("email");
                        String email = "";

                        // use activity's callback to:
                        if (getContext() instanceof ActivityCallback) {
                            ActivityCallback callbackActivity = (ActivityCallback) getContext();
                            // save credential to preferences
                            PreferencesManagement.savePreferences((Context) getContext(), userId, login, password, email);

                            //clear back stack (to avoid return by back button press
                            callbackActivity.clearBackStack();
                            showProgress(false);
                            // launch routes fragment (as a first in backstack)
                            callbackActivity.changeFragmentAsFirst(new RoutesFragment());
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Snackbar.make(mLoginFormView, errorMsg, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    // show error message
                    String informString = "Json error: " + e.getMessage();
                    Snackbar.make(mLoginFormView, informString, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                String errorText = "Login Error: ";
                if(error instanceof NoConnectionError) {
                    errorText += "No internet Access, Check your internet connection.";
                } else if (!(error.getMessage()==null)) {
                    errorText += error.getMessage();
                } else {
                    errorText += "Something went wrong.";
                }
                Snackbar.make(mLoginFormView, errorText, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                showProgress(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("username", login);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(strReq, tagRequestLogin);
    }

}