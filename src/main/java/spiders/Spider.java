package spiders;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import models.Article;
import services.ArticleService;
import services.ArticleTagService;

public abstract class Spider {

	@Autowired
	protected ArticleService articleService;
	@Autowired
	protected ArticleTagService tagService;

	/**
	 * 启动爬虫
	 * 
	 * @throws IOException
	 */

	abstract public void startSpider();

	/**
	 * 保存文章
	 */
	public void save(Article article) {
		try {
			// 保存文章
			if (articleService.replace(article) > 0) {
				// 保存文章标签
				article.getTags().forEach(t -> {
					tagService.replace(article.getArticleUrl(), t);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 爬取工作主体类容,爬取一次
	 * 
	 * @throws IOException
	 */
	abstract protected void doWork() throws IOException;

}
