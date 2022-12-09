package scc.data.models;

/**
 * This class defines the model used
 */
public abstract class BaseDAO extends MongoBaseDAO {

    protected BaseDAO() {}

    protected BaseDAO(String id) {
        super(id);
    }

    protected BaseDAO(String id, Long ttl) {
        super(id, ttl);
    }

}
