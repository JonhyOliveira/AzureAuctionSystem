package scc.data.models;

import jakarta.annotation.Nonnull;

public class CookieDAO extends DAO {

    private String id;
    private String value;
    private Integer ttl;

    public CookieDAO()
    {

    }

    public CookieDAO(String id, String value)
    {
        this(id, value, -1);
    }

    public CookieDAO(String id, String value, Integer ttl)
    {
        this.id = id;
        this.value = value;
        this.ttl = ttl;
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

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }
}
