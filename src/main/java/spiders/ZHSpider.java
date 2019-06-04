package spiders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import models.Article;
import utils.DataUtil;
import utils.SpiderUtil;

@Component
public class ZHSpider extends Spider {

	private int interval = 60;

	private int minVoteCount = 1;
	private int minCommentCount = 1;

	@Override
	public void startSpider() {
		System.out.println("startSpider : ZH");
		SpiderUtil.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					doWork();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, new Date(), TimeUnit.MINUTES.toMillis(interval));
	}

	@Override
	protected void doWork() throws IOException {
		for (String tag : DataUtil.getAllTags()) {
			getTopicIdsByTag(tag);
		}
	}

	/**
	 * 
	 * @param tag
	 * @param page
	 * @return json文本数据
	 * @throws IOException
	 */
	private String getJsonTopicsCollections(String tag, int page) throws IOException {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0");
		Response response = Jsoup
				.connect("https://www.zhihu.com/api/v4/search_v3?t=topic&correction=1&offset=" + page * 20
						+ "&limit=20&lc_idx=80&show_all_topics=1&q=" + tag)
				.method(Method.GET).ignoreContentType(true).headers(headers).execute();
		if (response.statusCode() != 200) {
			return null;
		}
		return response.body();
	}

	/**
	 * @param nextPageUrl 下一页topics的链接
	 * @return json文本数据
	 * @throws IOException
	 */
	private String getJsonTextFromUrl(String nextPageUrl) throws IOException {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0");
		Response response = Jsoup.connect(nextPageUrl).method(Method.GET).headers(headers).ignoreContentType(true)
				.execute();
		return response.body();
	}

	public void getTopicIdsByTag(String topic) throws IOException {
		String jsonText = getJsonTopicsCollections(topic, 1);
		while (true) {
			JSONObject paging = null;
			try {
				if (jsonText == null || jsonText.length() <= 10) {
					break;
				}
				JSONObject jsonObject = JSON.parseObject(jsonText);
				paging = jsonObject.getJSONObject("paging");
				JSONArray data = jsonObject.getJSONArray("data");
				for (int i = 0; i < data.size(); i++) {
					JSONObject item = data.getJSONObject(i).getJSONObject("object");
					int top_answer_count = item.getIntValue("top_answer_count");
					int questions_count = item.getIntValue("questions_count");
					if (top_answer_count <= 10 || questions_count <= 10)
						continue;
					String topicId = item.getString("id");
//					topicIds.add(topicId);
					getArticleByTopicId(topic, topicId);
				}
				if (paging.getBooleanValue("is_end") || data.size() == 0)
					break;
				jsonText = getJsonTextFromUrl(paging.getString("next"));
			} catch (Exception e) {
				if (paging != null)
					jsonText = getJsonTextFromUrl(paging.getString("next"));
				else
					break;
				e.printStackTrace();
			}
		}
	}

	private void getArticleByTopicId(String topic, String topicId) throws IOException {
		String beginUrl = "https://www.zhihu.com/api/v4/topics/" + topicId
				+ "/feeds/essence?include=data[?(target.type=topic_sticky_module)].target.data[?(target.type=answer)].target.content,relationship.is_authorized,is_author,voting,is_thanked,is_nothelp;data[?(target.type=topic_sticky_module)].target.data[?(target.type=answer)].target.is_normal,comment_count,voteup_count,content,relevant_info,excerpt.author.badge[?(type=best_answerer)].topics;data[?(target.type=topic_sticky_module)].target.data[?(target.type=article)].target.content,voteup_count,comment_count,voting,author.badge[?(type=best_answerer)].topics;data[?(target.type=topic_sticky_module)].target.data[?(target.type=people)].target.answer_count,articles_count,gender,follower_count,is_followed,is_following,badge[?(type=best_answerer)].topics;data[?(target.type=answer)].target.annotation_detail,content,hermes_label,is_labeled,relationship.is_authorized,is_author,voting,is_thanked,is_nothelp;data[?(target.type=answer)].target.author.badge[?(type=best_answerer)].topics;data[?(target.type=article)].target.annotation_detail,content,hermes_label,is_labeled,author.badge[?(type=best_answerer)].topics;data[?(target.type=question)].target.annotation_detail,comment_count;&limit=10&offset=0";
		String jsonText = getJsonTextFromUrl(beginUrl);
		while (true) {
			JSONObject paging = null;
			try {
				if (jsonText == null || jsonText.length() <= 10) {
					break;
				}
				JSONObject jsonObject = JSON.parseObject(jsonText);
				paging = jsonObject.getJSONObject("paging");
				JSONArray data = jsonObject.getJSONArray("data");
				for (int i = 0; i < data.size(); i++) {
					JSONObject item = data.getJSONObject(i).getJSONObject("target");
					int voteup_count = item.getIntValue("voteup_count");
					int comment_count = item.getIntValue("comment_count");
					if (voteup_count > minVoteCount || comment_count > minCommentCount) {
						String title, articleUrl = null;
						if (item.getString("type").equals("answer")) {
							String answerId = item.getString("id");
							JSONObject question = item.getJSONObject("question");
							String questionId = question.getString("id");
							title = question.getString("title");
							articleUrl = "https://www.zhihu.com/question/" + questionId + "/answer/" + answerId;
						} else {
							title = item.getString("title");
							articleUrl = item.getString("url");
						}
						String imagePath = item.getString("thumbnail");
						String preview = item.getString("excerpt");
						String author = item.getJSONObject("author").getString("name");
						String authorExtra = item.getJSONObject("author").getString("headline");
						Article article = new Article(title, preview, imagePath, author, articleUrl, "知乎");
						article.setAuthorExtra(authorExtra);
						article.setTags(new ArrayList<String>(Arrays.asList(topic)));
//						System.out.println(article);
						save(article);
					}
				}
				if (paging.getBooleanValue("is_end") || data.size() == 0)
					break;
				String next = paging.getString("next");
//				System.out.println("ZHSpider:" + next);
				jsonText = getJsonTextFromUrl(next);
			} catch (Exception e) {
				if (paging != null)
					jsonText = getJsonTextFromUrl(paging.getString("next"));
				else
					break;
				e.printStackTrace();
			}
		}
	}

}
