package scc.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public abstract class MongoBaseDAO {

    public static final String ID = "_id";
    public static final String TTL = "expire_at";

    @JsonProperty(ID)
    protected String id;

    @JsonProperty(TTL)
    protected Long expiry;

    protected MongoBaseDAO() {
        this(null);
    }

    protected MongoBaseDAO(String id) {
        this(id, null);
    }

    protected MongoBaseDAO(String id, Long ttl) {
        this.id = id;
        this.expiry = ttl != null ? System.currentTimeMillis() +  ttl * 1000 : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }
}
