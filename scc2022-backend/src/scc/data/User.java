package scc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import scc.utils.Hash;

import java.util.Objects;

/**
 * Represents a User, as returned to the clients
 */
public class User {
	private String nickname;
	private String name;
	private String pwd;
	@JsonProperty("image_id")
	private String imageId;

	@SuppressWarnings("unused")
	public User() {}

	public User(String nickname, String name, String pwd, String imageId) {
		super();
		this.nickname = nickname;
		this.name = name;
		this.pwd = pwd;
		this.imageId = imageId;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getName() {
		return name;
	}
	@SuppressWarnings("unused")
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getImageId() {
		return imageId;
	}
	@SuppressWarnings("unused")
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	/**
	 * @return a new object witch is a copy of this user with its password censored
	 */
	public User censored()
	{
		User censored = this.copy();

		censored.pwd = "?";

		return censored;
	}

	/**
	 * @return a new object witch is a copy of this user with its password hashed
	 */
	public User hashPwd() {
		User hashed = this.copy();

		hashed.pwd = Hash.of(this.pwd);

		return hashed;
	}


	public User patch(User user)
	{
		User patched = this.copy();

		if (Objects.nonNull(user))
		{
			if (Objects.nonNull(user.name))
				patched.name = user.name;
			if (Objects.nonNull(user.pwd))
				patched.pwd = user.pwd;
			if (Objects.nonNull(user.imageId))
				patched.imageId = user.imageId;
		}

		return patched;
	}

	/**
	 * @return a new object witch is a copy of this user
	 */
	public User copy() {
		return new User(this.nickname, this.name, this.pwd, this.imageId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return nickname.equals(user.nickname) && Objects.equals(name, user.name) && pwd.equals(user.pwd) && Objects.equals(imageId, user.imageId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nickname, name, pwd, imageId);
	}

	@Override
	public String toString() {
		return "User [nickname=" + nickname + ", name=" + name + ", pwd=" + pwd + ", photoId=" + imageId + " ]";
	}

}
