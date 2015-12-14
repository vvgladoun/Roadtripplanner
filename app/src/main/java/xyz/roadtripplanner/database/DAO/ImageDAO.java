package xyz.roadtripplanner.database.DAO;

import xyz.roadtripplanner.model.PlaceImage;

/**
 * DAO pattern for tag
 *
 * @author xyz
 */
public interface ImageDAO {

    /**
     * insert place-image object to the datastore
     *
     * @param placeImage place-image link object
     * @return status
     */
    boolean insertImage(PlaceImage placeImage);

    /**
     * Get image by ID
     *
     * @param id - image ID
     * @return placeImage object
     */
    PlaceImage findImageById(int id);

    /**
     * Get image by Place ID
     *
     * @param id - place ID
     * @return placeImage object
     */
    PlaceImage findImageByPlaceId(int id);
}
