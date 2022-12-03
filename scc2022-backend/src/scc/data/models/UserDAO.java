package scc.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import scc.data.User;

/**
 * Represents a User, as stored in the database
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UserDAO extends BaseDAO {

	private String name;
	@JsonProperty("password")
	private String pwd;
	@JsonProperty("photo_id")
	private String photoId;
	@JsonProperty(value = "to_delete",defaultValue = "false")
	private boolean to_delete;

	@SuppressWarnings("unused")
	public UserDAO() {
	}
	public UserDAO( User u) {
		this(u.getNickname(), u.getName(), u.getPwd(), u.getImageId());
	}
	public UserDAO(String nickname, String name, String pwd, String photoId) {
		super(nickname);
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
	}

	public String getNickname() {
		return id;
	}
	@SuppressWarnings("unused")
    public void setNickname(String nickname) {
		this.id = nickname;
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
    public String getPhotoId() {
		return photoId;
	}
	@SuppressWarnings("unused")
    public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public boolean isToDelete() {
		return to_delete;
	}

	public void setToDelete(boolean toDelete) {
		this.to_delete = toDelete;
	}

	public User toUser() {
		return new User(id, name, pwd, photoId);
	}

	@Override
	public String toString() {
		return "UserDAO [nickname=" + id + ", name=" + name + ", pwd=" + pwd
				+ ", photoId=" + photoId + " ]";
	}

}
