package xyz.roadtripplanner.database.DAO;

import xyz.roadtripplanner.model.Tag;

/**
 * Tag DAO declaration
 *
 * @author xyz
 */
public interface TagDAO {

    /**
     * declaration of Insert tag to DB method
     * @param tag tag object
     * @return status
     */
    boolean insertTag(Tag tag);
}
