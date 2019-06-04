package models;

public class UserFavoriteTag {

	private int id;
	private String uid;
	private String tag;

	public UserFavoriteTag(String uid, String tag) {
		super();
		this.uid = uid;
		this.tag = tag;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
