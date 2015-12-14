package xyz.roadtripplanner.utilities;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton for volley
 *
 * @author xyz
 */
public class VolleyHelper {

    //default TAG to cancel request
    public static final String TAG = VolleyHelper.class.getSimpleName();

    private static VolleyHelper mInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    /**
     * singleton constructor
     *
     * @param context current context
     */
    private VolleyHelper(Context context){
        this.mContext = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * return existing instance
     * (or create new if not created)
     *
     * @param context
     * @return helper's instance
     */
    public static synchronized VolleyHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new VolleyHelper(context);
        }
        return mInstance;
    }

    /**
     * return existing queue
     * (or create new one if not created)
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    /**
     * Add request to queue
     * @param req request
     * @param <T> generic type
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * cancel request
     *
     * @param tag request's tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
