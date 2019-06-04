package models;

import java.util.List;

public class Article {

	private int id;
	private String title;
	private String preview;
	private String author;
	private String authorExtra;
	private String imagePath;
	private String articleUrl;
	private List<String> tags;
	private String platform;

	public Article() {
	}

//	public Article encode() throws UnsupportedEncodingException {
//		title = URLEncoder.encode(title, "utf-8");
//		author = URLEncoder.encode(author, "utf-8");
//		preview = URLEncoder.encode(preview, "utf-8");
//		platform = URLEncoder.encode(platform, "utf-8");
//		if (StringUtils.isNotEmpty(authorExtra))
//			authorExtra = URLDecoder.decode(authorExtra, "utf-8");
//		return this;
//	}
//
//	public Article decode() throws UnsupportedEncodingException {
//		title = URLDecoder.decode(title, "utf-8");
//		author = URLDecoder.decode(author, "utf-8");
//		preview = URLDecoder.decode(preview, "utf-8");
//		platform = URLDecoder.decode(platform, "utf-8");
//		if (StringUtils.isNotEmpty(authorExtra))
//			authorExtra = URLDecoder.decode(authorExtra, "utf-8");
//		return this;
//	}

	public Article(String title, String preview, String imagePath, String author, String articleUrl, String platform) {
		super();
		this.title = title;
		this.preview = preview;
		this.imagePath = imagePath;
		this.author = author;
		this.articleUrl = articleUrl;
		this.platform = platform;
	}

	@Override
	public String toString() {
		return "Article [title=" + title + ", author=" + author + ", articleUrl=" + articleUrl + ", platform="
				+ platform + "]";
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getAuthorExtra() {
		return authorExtra;
	}

	public void setAuthorExtra(String authorExtra) {
		this.authorExtra = authorExtra;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getArticleUrl() {
		return articleUrl;
	}

	public void setArticleUrl(String articleUrl) {
		this.articleUrl = articleUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
