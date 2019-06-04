package models;

import java.io.Serializable;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import utils.DataUtil;

@Component
@Scope("prototype")
public class User implements Serializable {

	private static final long serialVersionUID = 9111946291943890959L;

	private String id;
	private String password;
	private String nickname;
	private int permission;
	private String sex;
	private String avatarPath;
	private Date createTime;

	public User(String id, String password) {
		super();
		this.id = id;
		this.password = password;
	}

	public User() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isAdmin() {
		return getPermission() > DataUtil.USER;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", password=" + password + ", nickname=" + nickname + "]";
	}

}
