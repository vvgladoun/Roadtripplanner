package xyz.roadtripplanner.database;

import android.content.Context;

/**
 * Full initial data load methods
 *
 * @author XYZ
 */
public class DBCreation {

    /**
     * Run sync methods in sequence
     *
     * @param context - app's context
     */
    public static void fillDatabaseSequence(Context context){

        SynchronizeDB.synchronizeUser(context, 0);
        SynchronizeDB.synchronizeRouteSharing(context, 0);
        SynchronizeDB.synchronizeRoute(context, 0);
        SynchronizeDB.synchronizePlaceImage(context, 0);
        SynchronizeDB.synchronizePlace(context, 0);
        SynchronizeDB.synchronizeAddress(context, 0);
        SynchronizeDB.synchronizeTag(context, 0);
        SynchronizeDB.synchronizePlaceTag(context, 0);
        SynchronizeDB.synchronizeComment(context, 0);
        SynchronizeDB.synchronizeRoutePoint(context, 0);

        loadAllImages(context);
    }

    /**
     * Load images to external storage (sequentially)
     *
     * @param context - app's context to get db instance
     */
    public static boolean loadAllImages(Context context){
        // load places' images
        boolean placesLoaded = SynchronizeDB.syncPlacesImages(context, 0);
        // load routes' images
        boolean routesLoaded = SynchronizeDB.syncRoutesImages(context, 0);

        return placesLoaded;
    }
}
