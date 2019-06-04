package models;

public class ArticleTag {

	int aid;
	String tag;

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public ArticleTag(int aid, String tag) {
		super();
		this.aid = aid;
		this.tag = tag;
	}

}
