package scc.data.models;

import scc.data.User;

/**
 * Represents a User, as stored in the database
 */
public final class UserDAO extends DAO {

	private String id;
	private String name;
	private String pwd;
	private String photoId;

	public UserDAO() {
	}
	public UserDAO( User u) {
		this(u.nickname(), u.name(), u.pwd(), u.photoId());
	}
	public UserDAO(String id	, String name, String pwd, String photoId) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public String get_rid() {
		return _rid;
	}

	public String get_ts() {
		return _ts;
	}

	public void set_rid(String _rid) {
		this._rid = _rid;
	}

	public void set_ts(String _ts) {
		this._ts = _ts;
	}
	public User toUser() {
		return new User(id, name, pwd, photoId);
	}

	@Override
	public String toString() {
		return "UserDAO [_rid=" + _rid + ", _ts=" + _ts + ", id=" + id + ", name=" + name + ", pwd=" + pwd
				+ ", photoId=" + photoId + " ]";
	}

}
