package xyz.roadtripplanner.json;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import xyz.roadtripplanner.database.DAO.AddressDAOImplementation;
import xyz.roadtripplanner.database.DAO.ImageDAOImplementation;
import xyz.roadtripplanner.database.DAO.PlaceCommentDAOImplementation;
import xyz.roadtripplanner.database.DAO.PlaceDAOImplementation;
import xyz.roadtripplanner.database.DAO.PlaceTagDAOImplementation;
import xyz.roadtripplanner.database.DAO.RouteDAOImplementation;
import xyz.roadtripplanner.database.DAO.RoutePointDAOImplementation;
import xyz.roadtripplanner.database.DAO.RouteSharingDAOImplementation;
import xyz.roadtripplanner.database.DAO.TagDAOImplementation;
import xyz.roadtripplanner.database.DAO.UserDAOImplementation;
import xyz.roadtripplanner.database.SynchronizeDB;
import xyz.roadtripplanner.model.Address;
import xyz.roadtripplanner.model.Comment;
import xyz.roadtripplanner.model.Place;
import xyz.roadtripplanner.model.PlaceImage;
import xyz.roadtripplanner.model.Route;
import xyz.roadtripplanner.model.Tag;
import xyz.roadtripplanner.model.User;

/**
 * Methods to parse JSON
 *
 * @author xyz
 */
public class JsonParserHelper {

    public static final String TAG = JsonParserHelper.class.getSimpleName();


    /**
     * decode json array and write places to DB
     * @param jsonRootObject - response JSON
     * @return status flag
     */
    public static boolean decodeJsonPlaces(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("places");
        PlaceDAOImplementation mPlaceDAOImplementation = new PlaceDAOImplementation(context);
        try {
            mPlaceDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Place place = decodeJsonObjectPlace(jsonObject);
                //check if place created correctly
                if(place != null){
                    //Log.d("DecodeJson", place.getId() + " " + place.getShortDescription() + "address: " +place.getAddressId() + " " + place.getUserId());
                    mPlaceDAOImplementation.insertPlace(place);
                }else{
                    Log.d("DecodeJson", "place is null");
                    return false;
                }
            }
            mPlaceDAOImplementation.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Place object
     * @param jsonObject - JSON with places
     * @return place if place created or null
     */
    public static Place decodeJsonObjectPlace(JSONObject jsonObject){
        try {
            //get jobject attributes
            int pId = jsonObject.getInt("id");
            String pName = jsonObject.getString("short_description");
            String pDesc = jsonObject.getString("full_description");
            int adId = jsonObject.getInt("address_id");
            int usId = jsonObject.getInt("user_id");
            //create new place and set its attributes
            Place place = new Place();
            place.setId(pId);
            place.setAddressId(adId);
            place.setUserId(usId);
            place.setShortDescription(pName);
            place.setFullDescription(pDesc);
            return place;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * decode json array and write places to DB
     * @param jsonRootObject json object to parse
     * @return status flag
     */
    public static boolean decodeJsonAddress(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("address");
        AddressDAOImplementation mAddressDAOImplementation = new AddressDAOImplementation(context);
        try {
            mAddressDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Address address = decodeJsonObjectAddress(jsonObject);
                //check if place created correctly
                if(address != null){
                    mAddressDAOImplementation.insertAddress(address);
                }else{
                    Log.d("DecodeJson", "address is null");
                    return false;
                }
            }
            mAddressDAOImplementation.close();
            return true;
        } catch (JSONException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * decode json object to Address object
     * @param jsonObject json object to parse
     * @return place if place created or null
     */
    public static Address decodeJsonObjectAddress(JSONObject jsonObject){
        try {
            //create new place and set its attributes
            Address address = new Address();
            address.setId(jsonObject.getInt("id"));
            address.setBuilding(jsonObject.getString("building"));
            address.setStreet(jsonObject.getString("street"));
            address.setSuburb(jsonObject.getString("suburb"));
            address.setCity(jsonObject.getString("city"));
            address.setCountry(jsonObject.getString("country"));
            address.setPostIndex(jsonObject.getString("postindex"));
            address.setLatitude(jsonObject.getDouble("latitude"));
            address.setLongitude(jsonObject.getDouble("longitude"));
            return address;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * decode json array and write places to DB
     * @param jsonRootObject json object
     * @return true if updated
     */
    public static boolean decodeJsonTag(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("tag");
        TagDAOImplementation tagDAOImplementation = new TagDAOImplementation(context);
        try {
            tagDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Tag tag = decodeJsonObjectTag(jsonObject);
                //check if place created correctly
                if(tag != null){
                    tagDAOImplementation.insertTag(tag);
                }else{
                    Log.d("DecodeJson", "address is null");
                    return false;
                }
            }
            tagDAOImplementation.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Tag object
     * @param jsonObject jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static Tag decodeJsonObjectTag(JSONObject jsonObject){
        try {
            //create new place and set its attributes
            Tag tag = new Tag();
            tag.setId(jsonObject.getInt("id"));
            tag.setName(jsonObject.getString("tag_name"));
            return tag;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * decode json array and write places to DB
     *
     * @param context - app's context
     * @param jsonRootObject json object to parse
     * @param minid last loaded id
     * @return status flag
     */
    public static boolean decodeJsonRoute(Context context, JSONObject jsonRootObject, int minid){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("route");
        RouteDAOImplementation routeDAOImplementation = new RouteDAOImplementation(context);
        try {
            routeDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Route route = decodeJsonObjectRoute(jsonObject);
                //check if route created correctly
                if(route != null){
                    routeDAOImplementation.insertRoute(route);
                }else{
                    Log.d("DecodeJson", "address is null");
                    return false;
                }
            }
            routeDAOImplementation.close();
            //load routes' images
            SynchronizeDB.syncRoutesImages(context, minid);
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Route object
     * @param jsonObject jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static Route decodeJsonObjectRoute(JSONObject jsonObject){
        try {
            //create new place and set its attributes
            Route route = new Route();

            route.setId(jsonObject.getInt("id"));
            route.setName(jsonObject.getString("route_name"));
            route.setDescription(jsonObject.getString("description"));
            route.setImagePath(jsonObject.getString("image_path"));
            return route;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * decode json array and write places to DB
     * @param jsonRootObject json object to parse
     * @return status flag
     */
    public static boolean decodeJsonPlaceTag(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("place_tag");
        PlaceTagDAOImplementation placeTagDAOImplementation = new PlaceTagDAOImplementation(context);
        try {
            placeTagDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                decodeJsonObjectPlaceTag(jsonObject, placeTagDAOImplementation);
            }
            placeTagDAOImplementation.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Address object
     * @param jsonObject jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static boolean decodeJsonObjectPlaceTag(JSONObject jsonObject, PlaceTagDAOImplementation plt){
        try {
            //create new place and set its attributes
            int plId = jsonObject.getInt("marker_id");
            int tagId = jsonObject.getInt("tag_id");
            int id = jsonObject.getInt("id");
            int isPrimary = jsonObject.getInt("is_primary");
            plt.insertPlaceTag(id, plId, tagId, isPrimary);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * decode json array and write places to DB
     * @param jsonRootObject json object to parse
     * @return status flag
     */
    public static boolean decodeJsonComment(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("place_comment");
        PlaceCommentDAOImplementation placeCommentDAOImplementation = new PlaceCommentDAOImplementation(context);
        try {
            placeCommentDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Comment comment = decodeJsonObjectComment(jsonObject);
                //check if route created correctly
                if(comment != null){
                    placeCommentDAOImplementation.insertPlaceComment(comment);
                }else{
                    Log.d("DecodeJson", "address is null");
                    return false;
                }
            }
            placeCommentDAOImplementation.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Address object
     * @param jsonObject jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static Comment decodeJsonObjectComment(JSONObject jsonObject){
        try {
            //create new place and set its attributes
            Comment comment = new Comment();
            comment.setId(jsonObject.getInt("id"));
            comment.setCommentText(jsonObject.getString("comment_text"));
            comment.setDate(jsonObject.getString("edit_date"));
            comment.setPlaceId(jsonObject.getInt("marker_id"));
            comment.setUserId(jsonObject.getInt("user_id"));
            return comment;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * decode json array and write route points to DB
     *
     * @param context apps context
     * @param jsonRootObject json object to parse
     * @return status flag
     */
    public static boolean decodeJsonRoutePoint(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("route_point");
        RoutePointDAOImplementation routePointDAOImplementation = new RoutePointDAOImplementation(context);
        try {
            routePointDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                decodeJsonObjectRoutePoint(jsonObject, routePointDAOImplementation);
            }
            routePointDAOImplementation.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Address object
     *
     * @param jsonObject jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static boolean decodeJsonObjectRoutePoint(JSONObject jsonObject, RoutePointDAOImplementation rp){
        try {
            //create new place and set its attributes
            int plId = jsonObject.getInt("marker_id");
            int routeId = jsonObject.getInt("route_id");
            int id = jsonObject.getInt("id");
            int order = jsonObject.getInt("order_number");
            rp.insertRoutePoint(id, plId, routeId, order);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * decode json array and write places to DB
     *
     * @param context apps context
     * @param jsonRootObject json object to parse
     * @return status flag
     */
    public static boolean decodeJsonRouteSharing(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("route_sharing");
        RouteSharingDAOImplementation routeSharingDAOImplementation = new RouteSharingDAOImplementation(context);
        try {
            routeSharingDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                decodeJsonObjectRouteSharing(jsonObject, routeSharingDAOImplementation);
            }
            routeSharingDAOImplementation.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Address object
     * @param jsonObject jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static boolean decodeJsonObjectRouteSharing(JSONObject jsonObject
            , RouteSharingDAOImplementation rsh){
        try {
            //create new place and set its attributes
            int userId = jsonObject.getInt("user_id");
            int routeId = jsonObject.getInt("route_id");
            int id = jsonObject.getInt("id");
            rsh.insertRouteSharing(id, userId, routeId);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * decode json array and write places to DB
     *
     * @param context app's context
     * @param jsonRootObject json object to parse
     * @return status flag
     */
    public static boolean decodeJsonUser(Context context, JSONObject jsonRootObject){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("user");
        UserDAOImplementation userDAOImplementation = new UserDAOImplementation(context);
        try {
            userDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                User user = decodeJsonObjectUser(jsonObject);
                //check if route created correctly
                if(user != null){
                    userDAOImplementation.insertUser(user);
                }else{
                    Log.d("DecodeJson", "address is null");
                    return false;
                }
            }
            userDAOImplementation.close();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Address object
     *
     * @param jsonObject jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static User decodeJsonObjectUser(JSONObject jsonObject){
        try {
            //create new place and set its attributes
            User user = new User();
            user.setId(jsonObject.getInt("id"));
            user.setEmail(jsonObject.getString("email"));
            user.setFullName(jsonObject.getString("full_name"));
            user.setLogin(jsonObject.getString("username"));
            user.setIsAdmin(jsonObject.getInt("isadmin"));

            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * decode json array and write places to DB
     *
     * @param context app's context
     * @param jsonRootObject json object to parse
     * @param minId - last loaded id
     * @return status flag
     */
    public static boolean decodeJsonPlaceImage(Context context, JSONObject jsonRootObject, int minId){
        //decode json string to array of json objects
        JSONArray jsonArray = jsonRootObject.optJSONArray("place_image");
        ImageDAOImplementation imageDAOImplementation = new ImageDAOImplementation(context);
        try {
            imageDAOImplementation.open();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PlaceImage pImage = decodeJsonObjectPlaceImage(jsonObject);
                //check if route created correctly
                if(pImage != null){
                    imageDAOImplementation.insertImage(pImage);
                }else{
                    Log.d("DecodeJson", "address is null");
                    return false;
                }
            }
            imageDAOImplementation.close();

            // load images
            SynchronizeDB.syncPlacesImages(context, minId);

            return true;
        } catch (SQLException e) {
            Log.e(TAG, "SQL error: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
        return false;
    }

    /**
     * decode json object to Address object
     * @param jsonObject JSON object to parse
     * @return place if place created or null
     */
    public static PlaceImage decodeJsonObjectPlaceImage(JSONObject jsonObject){
        try {
            //create new place and set its attributes
            PlaceImage placeImage = new PlaceImage();
            placeImage.setId(jsonObject.getInt("id"));
            placeImage.setPlaceId(jsonObject.getInt("marker_id"));
            placeImage.setImagePath(jsonObject.getString("image_path"));
            placeImage.setIsDefault(jsonObject.getInt("is_default"));

            return placeImage;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
