package scc.data;

public abstract class DAO {

    protected String _rid;
    protected String _ts;

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
}
