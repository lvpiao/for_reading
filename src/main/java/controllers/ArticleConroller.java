package controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import exceptions.CustomException;
import models.JR;
import models.ResultModel;
import models.UserFavoriteTag;
import redis.clients.jedis.Jedis;
import services.ArticleService;
import services.UserCollectArticleService;
import services.UserFavoriteTagService;
import utils.HintUtil;
import utils.Manager;
import utils.ModuleCommonUtil;
import utils.RecommendUtil;
import utils.TokenUtil;

@Controller
@RequestMapping("/article")
public class ArticleConroller {

	@Autowired
	ArticleService articleService;

	@RequestMapping(value = "/getHot")
	public @ResponseBody ResultModel getHot() {
		Jedis jedis = Manager.getResource();
		String json = jedis.get("hotArticle");
		ResultModel re = new ResultModel(JR.SUCCESS);
		re.setData(json);
		jedis.close();
		return re;
	}

	@RequestMapping(value = "/get/{token:.+}")
	public @ResponseBody ResultModel getRandom(@PathVariable("token") String token) {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		while (data.size() < 8) {
			Integer min = articleService.getMinId();
			Integer max = articleService.getMaxId();
			List<Integer> randInts = ModuleCommonUtil.randomIntegers(16, min == null ? 0 : min,
					max == null ? 0 : max + 1);
			List<Map<String, String>> item = articleService.getArticleByIds(randInts);
			data.addAll(item);
		}
		ResultModel re = new ResultModel(JR.SUCCESS);
		re.setData(data.stream().limit(8).collect(Collectors.toList()));
		return re;
	}

	@Autowired
	UserFavoriteTagService userFavoriteTagService;

	@RequestMapping(value = "/recommend/{token:.+}")
	public @ResponseBody ResultModel recommend(@PathVariable("token") String token) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		List<UserFavoriteTag> favoriteTags = userFavoriteTagService.getTagsByUid(uid);
		if (favoriteTags.size() == 0) {
			return new ResultModel(JR.FAIL, "请先选择感兴趣的文章标签");
		}
		List<Map<String, String>> lstMap;
		try {
			lstMap = Manager.getBean(RecommendUtil.class).getArticleByTags(favoriteTags);
			return new ResultModel(JR.SUCCESS, lstMap);
		} catch (CustomException e) {
			e.printStackTrace();
			return new ResultModel(JR.FAIL, e.getMessage());
		}

	}

	@Autowired
	UserCollectArticleService userCollectArticleService;

	@RequestMapping(value = "/clipArticle/{token:.+}")
	public @ResponseBody ResultModel clipArticle(@PathVariable("token") String token,
			@RequestParam("articleId") String articleId) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		return userCollectArticleService.insert(uid, Integer.parseInt(articleId)) > 0 ? new ResultModel(JR.SUCCESS)
				: new ResultModel(JR.FAIL);
	}

	@RequestMapping(value = "/clipedArticle/{token:.+}")
	public @ResponseBody ResultModel clipedArticle(@PathVariable("token") String token,
			@RequestParam("articleId") String articleId) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		return userCollectArticleService.exists(uid, Integer.parseInt(articleId)) != null
				? new ResultModel(JR.SUCCESS, "true")
				: new ResultModel(JR.SUCCESS, "false");
	}

	private int userCollectionLimit = 15;

	@RequestMapping(value = "/userCollectionArtilce/{token:.+}")
	public @ResponseBody ResultModel getUserCollectionArticle(@PathVariable("token") String token,
			@RequestParam(name = "page", defaultValue = "1") String page) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		int pageNum = Integer.parseInt(page);
		if (pageNum <= 0) {
			pageNum = 1;
		}
		int offset = userCollectionLimit * (pageNum - 1);
		List<Map<String, String>> lstMap = userCollectArticleService.getUserCollectionsArticleByUid(uid, offset,
				userCollectionLimit);
		return new ResultModel(JR.SUCCESS, lstMap);
	}

	@RequestMapping(value = "/removeUserCollectionArticle/{token:.+}")
	public @ResponseBody ResultModel removeUserCollectionArticle(@PathVariable("token") String token,
			@RequestParam("articleId") String articleId) {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		return userCollectArticleService.delete(uid, Integer.parseInt(articleId)) != null ? new ResultModel(JR.SUCCESS)
				: new ResultModel(JR.FAIL);
	}

	private int searchLimit = 15;

	@RequestMapping(value = "/search/{token:.+}")
	public @ResponseBody ResultModel search(@PathVariable("token") String token,
			@RequestParam("keyword") String keyword, @RequestParam(name = "page", defaultValue = "1") int page)
			throws UnsupportedEncodingException {
		String uid = TokenUtil.getUserIdByToken(token);
		if (uid == null) {
			return new ResultModel(JR.FAIL, HintUtil.INVALID_TOKEN.getMsg());
		}
		if (page <= 0) {
			page = 1;
		}
		int offset = searchLimit * (page - 1);
		return new ResultModel(JR.SUCCESS, articleService.search("*" + keyword + "*", offset, searchLimit));
	}
}
