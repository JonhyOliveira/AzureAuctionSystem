package scc.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import scc.data.User;

/**
 * Represents a User, as stored in the database
 */
public final class UserDAO extends DAO {

	private String nickname;
	private String name;
	private String pwd;
	private String photoId;

	@SuppressWarnings("unused")
	public UserDAO() {
	}
	public UserDAO( User u) {
		this(u.getNickname(), u.getName(), u.getPwd(), u.getPhotoId());
	}
	public UserDAO(String nickname, String name, String pwd, String photoId) {
		super();
		this.nickname = nickname;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
	}

	public String getId() {
		return nickname;
	}
	@SuppressWarnings("unused")
    public void setId(String nickname) {
		this.nickname = nickname;
	}
	@SuppressWarnings("unused")
    public String getName() {
		return name;
	}
	@SuppressWarnings("unused")
    public void setName(String name) {
		this.name = name;
	}
	@SuppressWarnings("unused")
    public String getPwd() {
		return pwd;
	}
	@SuppressWarnings("unused")
    public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	@SuppressWarnings("unused")
	@JsonProperty("photo_id")
    public String getPhotoId() {
		return photoId;
	}
	@SuppressWarnings("unused")
    public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public User toUser() {
		return new User(nickname, name, pwd, photoId);
	}

	@Override
	public String toString() {
		return "UserDAO [_rid=" + _rid + ", _ts=" + _ts + ", id=" + nickname + ", name=" + name + ", pwd=" + pwd
				+ ", photoId=" + photoId + " ]";
	}

}
