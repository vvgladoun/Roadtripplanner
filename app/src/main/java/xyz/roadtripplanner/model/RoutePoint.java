package xyz.roadtripplanner.model;

/**
 * Route-point link objects
 *
 * @author xyz
 */
public class RoutePoint {
    private int mId;
    private int mPlaceId;
    private int mRouteId;
    private int mOrderNumber;

    public RoutePoint() {
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(int placeId) {
        mPlaceId = placeId;
    }

    public int getRouteId() {
        return mRouteId;
    }

    public void setRouteId(int routeId) {
        mRouteId = routeId;
    }

    public int getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        mOrderNumber = orderNumber;
    }
}
