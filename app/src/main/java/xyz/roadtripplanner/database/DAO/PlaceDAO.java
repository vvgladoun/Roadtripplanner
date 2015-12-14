package xyz.roadtripplanner.database.DAO;

import xyz.roadtripplanner.model.Place;

/**
 * DAO pattern for places
 *
 * @author xyz
 */
public interface PlaceDAO {

    /**
     * insert place object to the datastore
     *
     * @param place - place object
     * @return status flag
     */
    boolean insertPlace(Place place);

    /**
     * Declaration of Find place by ID method
     *
     * @param id - place's uid
     * @return place object
     */
    Place findPlaceById(int id);
}
