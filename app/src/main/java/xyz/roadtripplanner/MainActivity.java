package xyz.roadtripplanner;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import xyz.roadtripplanner.about.AboutDialogFragment;
import xyz.roadtripplanner.intro.IntroFragment;
import xyz.roadtripplanner.login.LoginFragment;
import xyz.roadtripplanner.places.PlacesFragment;
import xyz.roadtripplanner.routes.RoutesFragment;
import xyz.roadtripplanner.utilities.ActivityCallback;
import xyz.roadtripplanner.utilities.PreferencesManagement;


/**
 * Main activity contains only one fragment
 *
 * @author vvgladoun@gmail.com
 */
public class MainActivity extends BasicActivity implements ActivityCallback,
        NavigationView.OnNavigationItemSelectedListener {

    String mUsernameValue;
    String mPasswordValue;
    int mUid;
    String mEmail;
    boolean mLogged;
    Menu mMenu;

    // widgets
    Toolbar mToolbar;
    // navigation drawer
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawer;
    NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // call next steps from super class
        super.onCreate(savedInstanceState);


    }





    protected Fragment createFragment() {
        // get first fragment to show
        Fragment defaultFragment;
        if (!mLogged) {
            //show intro if not logged in
            defaultFragment = new IntroFragment();
        } else {
            //show routes page
            defaultFragment = new RoutesFragment();
            mNavigationView = (NavigationView) findViewById(R.id.nav_view);
            mNavigationView.getMenu().getItem(0).setChecked(true);
        }
        return defaultFragment;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_options, menu);
        if (mLogged) {
            hideOption(R.id.action_signin);
            showOption(R.id.action_signout);
        } else {
            hideOption(R.id.action_signout);
            showOption(R.id.action_signin);
        }
        return true;
    }

    /**
     * Hide option menu item
     *
     * @param id - menu item id
     */
    public void hideOption(int id) {
        MenuItem item = mMenu.findItem(id);
        item.setVisible(false);
    }

    /**
     * Show option menu item
     *
     * @param id - menu item id
     */
    public void showOption(int id) {
        MenuItem item = mMenu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_about:
                // Show about dialog
                (new AboutDialogFragment()).show(getFragmentManager(), "ABOUT");
                return true;
            case R.id.action_signin:
                // open sign in fragment
                if (!mLogged) {
                    changeFragmentAsFirst(new LoginFragment());
                }
                return true;
            case R.id.action_signout:
                // sign out (clear credentials)
                PreferencesManagement.clearPreferences(this.getApplicationContext());
                // clear back stack and open intro page
                changeFragmentAsFirst(new IntroFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void changeFragment(Fragment newFragment) {
        // update logged in status
        initVariables();
        // add new fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void changeFragmentAsFirst(Fragment newFragment) {
        //clear backstack
        int stackHeight = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < stackHeight; i++) {
            clearBackStack();
        }

        changeFragment(newFragment);
    }

    @Override
    public void clearBackStack() {
        getSupportFragmentManager()
                .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_places) {
            // show places fragment with no defined route
            changeFragmentAsFirst(PlacesFragment.newInstance(0));
        } else if (id == R.id.nav_routes) {
            // show routes
            changeFragmentAsFirst(new RoutesFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void initVariables(){
        // get user credentials
        loadPreferences();

        // define widgets and actions for drawer navigation
        // define navigation drawer
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        showNavigationDrawer(mLogged);

    }

    void defineDrawerNavigation(){
        if (mToolbar == null) {
            //toolbar
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        if (mNavigationView == null) {
            // get navigation view
            mNavigationView = (NavigationView) findViewById(R.id.nav_view);
            mNavigationView.setNavigationItemSelectedListener(this);
            // find header and define text in it
            View headerLayout = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
            TextView headerNavUser = (TextView) headerLayout.findViewById(R.id.tvNavigationUsername);
            headerNavUser.setText(mUsernameValue);
        }
    }

    /**
     * Hides navigation drawer if needed
     *
     * @param visible flag: if true navigation drawer can be used
     */
    void showNavigationDrawer(boolean visible){
        if (visible) {
            // get navigation drawer
            defineDrawerNavigation();
            // set drawer's actions
            mDrawerToggle = new ActionBarDrawerToggle(
                    this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawer.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        if (mDrawerToggle != null) {
            mDrawerToggle.setDrawerIndicatorEnabled(visible);
        }
    }

    /**
     * Get credentials from preferences
     */
    public void loadPreferences() {
        // Get credentials
        mUsernameValue = PreferencesManagement.getUsername(this.getApplicationContext());
        mPasswordValue = PreferencesManagement.getPassword(this.getApplicationContext());
        mEmail = PreferencesManagement.getEmail(this.getApplicationContext());
        mUid = PreferencesManagement.getUserId(this.getApplicationContext());
        mLogged = PreferencesManagement.isLogged(this.getApplicationContext());
    }



}
