package xyz.roadtripplanner.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.ToWebDB;

/**
 * Register for new user
 *
 * @author xyz
 */
public class RegisterFragment extends Fragment {

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mConfPasswordView;
    private EditText mEmailView;
    private EditText mFullNameView;

    private View mProgressView;
    private View mRegisterFormView;

    //private static final String TAG = LoginFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get fragment layout
        View fragment_view = inflater.inflate(R.layout.register_fragment, container, false);
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
        mConfPasswordView = (EditText) container.findViewById(R.id.password_confirm);
        mEmailView = (EditText) container.findViewById(R.id.email);
        mFullNameView = (EditText) container.findViewById(R.id.fullname);

        mProgressView = container.findViewById(R.id.register_progress);
        mRegisterFormView = container.findViewById(R.id.login_form);
        // define button's action
        Button login = (Button) container.findViewById(R.id.register_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
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

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Attempts to sign in to the account specified by the login form.
     * If there are form errors (invalid, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the register attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();
        String confPassword = mConfPasswordView.getText().toString();
        String fullName = mFullNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if(!isPasswordValid(password, confPassword)){
            mPasswordView.setError(getString(R.string.error_different_password));
            focusView = mPasswordView;
            cancel = true;
        }


        //Check for valid email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check for valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // check login
            showProgress(true);
            ToWebDB.insertUserWeb(getContext(),username, fullName, email, password, this);
        }
    }


    /**
     * Check input username
     *
     * @param email login
     * @return true if valid
     */
    private boolean isEmailValid(String email) {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(EMAIL_PATTERN);
    }

    /**
     * Check input password
     *
     * @param password user's password
     * @return true if valid
     */
    private boolean isPasswordValid(String password, String confPassword) {
        //test logic
        return password.equals(confPassword);
    }

    private boolean isPasswordValid(String password) {
        //test logic
        return password.length() > 2;
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

}
