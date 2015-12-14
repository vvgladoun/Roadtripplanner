package xyz.roadtripplanner.model;

import com.google.gson.annotations.SerializedName;

/**
 * Route sharing object
 *
 * @author xyz
 */
public class RouteSharing {

    @SerializedName("id")
    private int mId;
    @SerializedName("route_id")
    private int mRouteId;
    @SerializedName("user_id")
    private int mUserId;

    /**
     * Route Sharing constructor
     *
     * @param id - uid from db
     * @param routeid route's uid
     * @param userid user's uid
     */
    public RouteSharing(int id, int routeid, int userid){

        this.mId = id;
        this.mRouteId = routeid;
        this.mUserId = userid;
    }

    /**
     *
     * @return route sharing row uid
     */
    public int getId() {
        return mId;
    }

    /**
     *
     * @param id route sharing row uid
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     *
     * @return route's uid
     */
    public int getRouteid() {
        return mRouteId;
    }

    /**
     *
     * @param routeId route's uid
     */
    public void setRouteId(int routeId) {
        this.mRouteId = routeId;
    }

    /**
     *
     * @return user's uid
     */
    public int getUserid() {
        return mUserId;
    }

    /**
     *
     * @param userId user'd uid
     */
    public void setUserId(int userId) {
        this.mUserId = userId;
    }
}
