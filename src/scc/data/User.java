package scc.data;

import scc.utils.Hash;

import java.util.Objects;

/**
 * Represents a User, as returned to the clients
 */
public class User implements Cloneable {
	private String nickname;
	private String name;
	private String pwd;
	private String photoId;

	public User() {}

	public User(String nickname, String name, String pwd, String photoId) {
		super();
		this.nickname = nickname;
		this.name = name;
		this.pwd = pwd;
		this.photoId = photoId;
	}
	public String nickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public User censored()
	{
		User censored = this.clone();

		censored.pwd = "?";

		return censored;
	}

	public User hashPwd() {
		User hashed = this.clone();

		hashed.pwd = Hash.of(this.pwd);

		return hashed;
	}
	public User patch(User user)
	{
		User patched = this.clone();

		if (Objects.nonNull(user))
		{
			if (Objects.nonNull(user.name))
				patched.name = user.name;
			if (Objects.nonNull(user.pwd))
				patched.pwd = user.pwd;
			if (Objects.nonNull(user.photoId))
				patched.photoId = user.photoId;
		}

		return patched;
	}

	@Override
	protected User clone() {
		return new User(this.nickname, this.name, this.pwd, this.photoId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return nickname.equals(user.nickname) && Objects.equals(name, user.name) && pwd.equals(user.pwd) && Objects.equals(photoId, user.photoId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nickname, name, pwd, photoId);
	}

	@Override
	public String toString() {
		return "User [nickname=" + nickname + ", name=" + name + ", pwd=" + pwd + ", photoId=" + photoId + " ]";
	}

}
