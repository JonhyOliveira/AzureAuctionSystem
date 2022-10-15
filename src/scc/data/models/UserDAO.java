package scc.data.models;

import scc.data.User;
import scc.utils.Hash;

import java.util.Objects;

/**
 * Represents a User, as stored in the database
 */
public class UserDAO extends DAO {
	private String id;
	private String name;
	private String pwd;
	private String photoId;

	public UserDAO() {
	}
	public UserDAO( User u) {
		this(u.getNickname(), u.getName(), u.getPwd(), u.getPhotoId());
	}
	public UserDAO(String nickname, String name, String pwd, String photoId) {
		super();
		this.id = nickname;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
	}
	public String _rid() {
		return _rid;
	}
	public void set_rid(String _rid) {
		this._rid = _rid;
	}
	public String _ts() {
		return _ts;
	}
	public void set_ts(String _ts) {
		this._ts = _ts;
	}
	public String id() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String name() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String pwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String photoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public User toUser() {
		return new User(id, name, pwd, photoId);
	}

	public UserDAO patch(User user)
	{
		if (Objects.nonNull(user))
		{
			if (Objects.nonNull(user.getName()))
				this.setName(user.getName());
			if (Objects.nonNull(user.getPwd()))
				this.setPwd(Hash.of(user.getPwd()));
			if (Objects.nonNull(user.getPhotoId()))
				this.setPhotoId(user.getPhotoId());
		}

		return this;
	}

	@Override
	public String toString() {
		return "UserDAO [_rid=" + _rid + ", _ts=" + _ts + ", id=" + id + ", name=" + name + ", pwd=" + pwd
				+ ", photoId=" + photoId + " ]";
	}

}
