package scc.data.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CookieDAO extends BaseDAO {
    private String value;

    public CookieDAO()
    {
    }

    public CookieDAO(String id, String value)
    {
        this(id, value, null);
    }

    public CookieDAO(String id, String value, Long ttl)
    {
        super(id, ttl);
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
