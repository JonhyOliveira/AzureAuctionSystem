package scc.data.models;

import jakarta.annotation.Nonnull;

public class CookieDAO extends DAO {

    private String id;
    private String value;

    public CookieDAO()
    {

    }

    public CookieDAO(String id, String value)
    {
        this.id = id;
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
