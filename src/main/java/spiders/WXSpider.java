package spiders;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import models.Article;
import utils.SpiderUtil;

@Component
public class WXSpider extends Spider {

	private String base = "https://weixin.sogou.com/pcindex/pc/";
	private int interval = 20;

	private HashMap<String, String> tags;

	@Override
	public void doWork() throws IOException {
		// 刷新首页，及初始化tags
		getFirstPages();
		// 获取其他页
		getNextPages();
	}

	private List<Article> getArticleByPageUrl(String tag, String pageUrl) throws HttpStatusException, IOException {
		Document doc = Jsoup.connect(pageUrl).get();
		Elements eles = doc.select("li");
		List<Article> articles = new ArrayList<Article>();
		for (int i = 0; i < eles.size(); i++) {
			Element ele = eles.get(i);
			String title = ele.select(".txt-box>h3>a").text();
			String preview = ele.select(".txt-box>p").text();
			String imagePath = "http:" + ele.select(".img-box>a>img").attr("src");
			String articleUrl = ele.select(".img-box>a").attr("href");
			String author = ele.select(".txt-box>div>a").text();
			Article wxArticle = new Article(title, preview, URLDecoder.decode(imagePath, "utf-8"), author, articleUrl,
					"微信");
			ArrayList<String> tags = new ArrayList<String>();
			tags.add(tag.substring(0, 2));
			wxArticle.setTags(tags);
			articles.add(wxArticle);
		}
		return articles;
	}

	// 从各分类的第一页获取数据
	private void getFirstPages() {
		if (tags == null) {
			initTags();
		}
		// 获取第一页
		tags.forEach((k, v) -> {
			String url = base + v + "/" + v + ".html";
			try {
				List<Article> lst = getArticleByPageUrl(k, url);
				saveArticles(lst);
//				if (k.equals("热门")) {
//					Jedis jedis = Manager.getResource();
//					jedis.set("hotArticle", JSON.toJSONString(lst));
//					jedis.close();
//				}
			} catch (IOException e3) {
				e3.printStackTrace();
			}
		});
	}

	// 获取其他页
	private void getNextPages() {
		if (tags == null) {
			initTags();
		}
		tags.forEach((k, v) -> {
			int curPage = 1;
			while (true) {
				String pageUrl = base + v + "/" + curPage++ + ".html";
//				System.out.println("WXSPider : " + pageUrl);
				try {
					saveArticles(getArticleByPageUrl(k, pageUrl));
				} catch (HttpStatusException e2) {
					if (e2.getStatusCode() == 404) {
						break;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void initTags() {
		tags = new HashMap<String, String>();
		try {
			Document doc = Jsoup.connect("https://weixin.sogou.com/").get();
			Elements eles = doc.select("#type_tab a[id^='pc']");
			for (int i = 0; i < eles.size(); i++) {
				Element e = eles.get(i);
				String k = e.ownText();
				String v = e.attr("id");
				tags.put(k, v);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 将数据保存到数据库
	private void saveArticles(List<Article> articles) {
		articles.forEach(x -> {
			save(x);
		});
	}

	public void startSpider() {
		SpiderUtil.getTimer().schedule(new TimerTask() {
			@Override
			public void run() {
				articleService.clearExpiredArticle();
				tagService.clear();
			}
		}, new Date(), TimeUnit.MINUTES.toMillis(interval));
		System.out.println("startSpider : WX");
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

}
