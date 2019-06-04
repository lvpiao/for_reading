package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import exceptions.CustomException;
import models.UserFavoriteTag;
import services.ArticleService;

@Component
@Scope("prototype")
public class RecommendUtil {

	private int recommendArticleNum = 6;

	@Autowired
	ArticleService articleService;

	public List<Map<String, String>> getArticleByTags(List<UserFavoriteTag> tags) throws CustomException {
		float r = -1;
		if (tags.size() > recommendArticleNum) {
			List<Integer> idxs = ModuleCommonUtil.randomIntegers(recommendArticleNum, 0, tags.size());
			ArrayList<UserFavoriteTag> tmp = new ArrayList<UserFavoriteTag>();
			for (Integer i : idxs) {
				tmp.add(tags.get(i));
			}
			tags.removeAll(tmp);
		} else if (tags.size() < recommendArticleNum) {
			r = (float) recommendArticleNum / tags.size();
		}
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < tags.size(); i++) {
			int num = (int) Math.ceil((r > 0 ? r : 1));
			String curTag = tags.get(i).getTag();
			for (int j = 0; j < num; j++) {
				strBuilder.append("(").append(oneSelect(curTag, (int) (Math.random() * 100))).append(")");
				if (j != num - 1) {
					strBuilder.append(" union ");
				}
			}
			if (i != tags.size() - 1) {
				strBuilder.append(" union ");
			}
		}
		if (strBuilder.toString().isEmpty()) {
			throw new CustomException("暂无推荐");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("sql", strBuilder.toString());
		return articleService.exeSelect(map);
	}

	private StringBuilder oneSelect(String tag, int offset) {
		StringBuilder builder = new StringBuilder();
		builder.append("select a.*,b.tag FROM for_reading.article a left join article_tag b on a.id = b.aid "
				+ " where a.id >= ").append(Math.random() * articleService.getArticleCountByTag(tag))
				.append(" and b.tag = '").append(tag).append("' ").append(" limit ").append(offset).append(" , ")
				.append(1);
		return builder;
	}

	public RecommendUtil articleNum(int recommendArticleNum) {
		this.recommendArticleNum = recommendArticleNum;
		return this;
	}
}
