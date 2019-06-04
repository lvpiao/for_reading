package despatch;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import models.Article;
import services.ArticleService;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.selector.Html;
import utils.JedisUtil;
import utils.Manager;
import utils.ModuleCommonUtil;

@Component
public class JSSpider implements PageProcessor {

	private static ArticleService articleService;

	private static final String SITE_PREFIX = "https://";
	private static final String SITE_DOMAIN = "www.jianshu.com";
	private static final int MAX_QUEUE_SIZE = 100;
	private static final int MIN_JSZ_NUM = 5;

	private static class JSPipeline implements Pipeline {
		@Override
		public void process(ResultItems resultItems, Task task) {
			Set<Entry<String, Object>> s = resultItems.getAll().entrySet();
			for (Entry<String, Object> item : s) {
				if (item.getValue() != null) {
					Document doc = Jsoup.parse(item.getValue().toString());
					String author = doc.select(".main-top .title").text();
					Elements eles = doc.select(".note-list li");
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
							articleService.replace(article);
						}
					}
				}
			}
		}
	}

	private Site site = new Site().setRetryTimes(4).setCharset("utf-8");

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		Html html = page.getHtml();
		// 首页的文章
		String curUrl = page.getUrl().toString();
		if (curUrl.length() >= 24 && curUrl.charAt(24) == 'u') {
			page.putField("u", html.toString());
		}
		int queSize = JedisUtil.getUrlQueueCount(SITE_DOMAIN);
		System.out.println(queSize);
		if (queSize <= MAX_QUEUE_SIZE) {
			// 发现后续用户
			List<String> uurls = html.links().regex("https://www\\.jianshu\\.com/u/.*").all();
			uurls = uurls.stream()
					.map(x -> x.substring(0, Math.min(x.length(), "https://www.jianshu.com/u/c5ff39de5c2e".length())))
					.collect(Collectors.toList());
			page.addTargetRequests(uurls);
			String baseUrl = curUrl.substring(0,
					Math.min(curUrl.length(), "https://www.jianshu.com/u/c5ff39de5c2e".length()));
			for (int i = 1; i <= ModuleCommonUtil.randInt(6); i++) {
				StringBuffer strBuf = new StringBuffer(baseUrl);
				strBuf.append("?order_by=top").append("&page=").append(i);
				page.addTargetRequest(strBuf.toString());
			}

			// 发现后续文章
			List<String> purls = html.links().regex("https://www\\.jianshu\\.com/p/.*").all();
			purls = purls.stream()
					.map(x -> x.substring(0, Math.min(x.length(), "https://www.jianshu.com/p/c5ff39de5c2e".length())))
					.collect(Collectors.toList());
			purls.removeIf(x -> x.endsWith("nts"));// 移除#comments结尾的链接
			page.addTargetRequests(purls);
		}
	}

	@Autowired(required = true)
	public void setArticleService(ArticleService articleService) {
		JSSpider.articleService = articleService;
	}

	private static Spider spider;

	// @PostConstruct
	public static void startSpider() {
		Manager.getThreadPool().submit(new Runnable() {
			@Override
			public void run() {
				try {
					spider = Spider.create(new JSSpider()).addUrl(SITE_PREFIX + SITE_DOMAIN)
							.addPipeline(new JSPipeline()).thread(5);
					spider.setScheduler(new MyRedisScheduler(Manager.getRedisPool())
							.setDuplicateRemover(new BloomFilterDuplicateRemover(1000000)));
					spider.runAsync();
				} catch (Exception e) {
					run();
					e.printStackTrace();
				}
			}
		});
	}

	@PreDestroy
	public static void destroy() {
		spider.stop();
	}

}
