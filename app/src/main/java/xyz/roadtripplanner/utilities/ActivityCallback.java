package xyz.roadtripplanner.utilities;

import android.support.v4.app.Fragment;

/**
 * Callback interface for activities with fragments
 *
 * @author xyz
 */
public interface ActivityCallback {

    /**
     * Change activity's fragment
     *
     * @param newFragment fragment instance
     */
    void changeFragment(Fragment newFragment);

    /**
     * Change activity's fragment (clear back stack before adding)
     *
     * @param newFragment fragment instance
     */
    void changeFragmentAsFirst(Fragment newFragment);

    /**
     * Remove all from the back stack
     */
    void clearBackStack();


}
