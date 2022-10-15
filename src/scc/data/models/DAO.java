package scc.data.models;

public abstract class DAO {

    protected String _rid;
    protected String _ts;

    public String _rid() {
        return _rid;
    }

    public String _ts() {
        return _ts;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
    }
}
