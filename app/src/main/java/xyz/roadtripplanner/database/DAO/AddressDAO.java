package xyz.roadtripplanner.database.DAO;

import xyz.roadtripplanner.model.Address;

/**
 * Address DAO declaration
 *
 * @author xyz
 */
public interface AddressDAO {

    /**
     * Insert address object to the database
     *
     * @param address - address object
     * @return status
     */
    boolean insertAddress(Address address);

    /**
     * find address in datastore by uid
     *
     * @param id -uid
     * @return - address object
     */
    Address findAddressById(int id);

}
