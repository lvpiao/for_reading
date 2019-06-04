package spiders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import models.Article;
import utils.EmailUtil;
import utils.DataUtil;
import utils.SpiderUtil;

@Component
public class JSSpider extends Spider {

	private float MIN_JSZ_NUM = 0.1f;
	private String SITE_DOMAIN = "www.jianshu.com";
	private String SITE_PREFIX = "https://";

	public void doWork() throws IOException {
		for (String tag : DataUtil.getAllTags()) {
			// 所有的集合的json
			String jsonText = getJsonCollections(tag, 1);
			JSONObject jsonObj = JSONObject.parseObject(jsonText);
			// 获取总页数
			int pageCount = jsonObj.getIntValue("total_pages");
			for (int curPage = 2; curPage <= pageCount; curPage++) {
				jsonText = getJsonCollections(tag, curPage);
				jsonObj = JSONObject.parseObject(jsonText);
				JSONArray jsonArr = (JSONArray) jsonObj.get("entries");
				for (Object o : jsonArr) {
					try {
						JSONObject obj = (JSONObject) o;
						// 集合链接
						String collectionUrl = "https://www.jianshu.com/c/" + obj.getString("slug");
//						System.out.println("JSSPider : " + collectionUrl);
						// 从集合链接中获取文章
						for (int i = 1; i < (Math.random() * pageCount) / curPage + 1; i++) {
							StringBuffer strBuf = new StringBuffer(collectionUrl);
							strBuf.append("?order_by=top&page=").append(i);
							// 处理html中的文章列表
							Response res = Jsoup.connect(strBuf.toString()).method(Method.GET).execute();
							if (res.statusCode() != 200) {
								continue;
							}
							if (!getArticles(tag, res.body()))
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param html   从html中解析出文章信息,
	 * @param string
	 * @return 返回文章列表对象
	 * @throws IOException
	 */
	private boolean getArticles(String tag, String html) throws IOException {
		Document doc = Jsoup.parse(html);
		String author = doc.select(".main-top .title").text();
		Elements eles = doc.select(".note-list li");
		boolean fetch = false;
		for (int i = 0; i < eles.size(); i++) {
			Element ele = eles.get(i);
			String jszNum = ele.select(".meta .jsd-meta").text();
			if (StringUtils.isEmpty(jszNum))
				continue;
			if (Float.parseFloat(jszNum) >= MIN_JSZ_NUM) {
				String title = ele.select(".content .title").text();
				String preview = ele.select(".content .abstract").text();
				String imagePath = "https:" + ele.select(".wrap-img img").attr("src");
				String articleUrl = SITE_PREFIX + SITE_DOMAIN + ele.select(".content .title").attr("href");
				Article article = new Article(title, preview, imagePath, author, articleUrl, "简书");
				article.setTags(new ArrayList<String>(Arrays.asList(tag)));
				save(article);
				fetch = true;
			}
		}
		return fetch;
	}

	/**
	 * 
	 * @param response Jsoup的Response
	 * @param cssQuery css选择器
	 * @param attr     属性
	 * @return 从response的html中选择css选择的标签的属性attr
	 */
	private String getAttributeFromResponse(Response response, String cssQuery, String attr) {
		Elements eles = Jsoup.parse(response.body()).select(cssQuery);
		return eles.get(0).attr(attr);
	}

	/**
	 * @param keyword 关键词
	 * @param pageNum 当前页
	 * @return 当前关键词的当前页的json格式的集合
	 * @throws IOException
	 */

	private String getJsonCollections(String keyword, int pageNum) throws IOException {
		// 试探
		Response response = Jsoup.connect("https://www.jianshu.com/search").method(Method.GET).execute();
		String csfrToken = getAttributeFromResponse(response, "meta[name='csrf-token']", "content");
		// 设置请求头
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-CSRF-Token", csfrToken);
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0");
		headers.put("Accept", "application/json");
		return Jsoup
				.connect("https://www.jianshu.com/search/do?q=" + keyword + "&type=collection&page=" + pageNum
						+ "&&order_by=top")
				.method(Method.POST).headers(headers).cookies(response.cookies()).ignoreContentType(true).execute()
				.body();
	}

	public void startSpider() {
		System.out.println("startSpider : JS");
		SpiderUtil.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					long s = System.currentTimeMillis();
					doWork();
					long t = System.currentTimeMillis();
					EmailUtil.sendEmail("简书爬取一次用时:" + String.valueOf((t - s) / 1000) + "秒", "简书完成爬取通知",
							"2033465789@qq.com");
				} catch (EmailException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, new Date(), TimeUnit.HOURS.toMillis(2));
	}
}
