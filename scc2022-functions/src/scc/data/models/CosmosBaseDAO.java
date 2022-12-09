package scc.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public abstract class CosmosBaseDAO {

    public static final String ID = "id";

    public static final String TTL = "ttl";

    @JsonProperty(ID)
    protected String id;
    @JsonProperty(TTL)
    protected Long ttl;
    protected String _rid;
    protected String _ts;

    protected CosmosBaseDAO() {
        this(null);
    }

    protected CosmosBaseDAO(String id) {
        this(id, null);
    }

    public CosmosBaseDAO(String id, Long ttl) {
        this.id = id;
        this.ttl = ttl != null ? ttl : -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String _rid() {
        return _rid;
    }

    @SuppressWarnings("unused")
    public String _ts() {
        return _ts;
    }

    @SuppressWarnings("unused")
    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    @SuppressWarnings("unused")
    public void set_ts(String _ts) {
        this._ts = _ts;
    }

    @SuppressWarnings("unused")
    public Long getTtl() {
        return ttl;
    }

    @SuppressWarnings("unused")
    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
