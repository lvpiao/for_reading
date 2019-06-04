package utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import services.ArticleService;

@Component
public class DataUtil {

	// 用户权限
	public static final int USER = 0;
	public static final int ADMIN = 1;
	public static final int SUPER_ADMIN = 2;
	public static String SERVER = "https://liuhuaqiang.cn/siyue";

	private static final String CLIENT_APK_PATH = System.getProperty("user.home") + File.separator + "apk"
			+ File.separator;

//	public static final String SERVER = "http://localhost:8080/siyue";

	public static final int MIN_PASSWRD_LENGTH = 6;

	public static int curClientVersion = 1;

	/**
	 * 刷新Article要用到的信息
	 */
	public static void refreshArticleData() {
		// 设置最小值
		Integer min = articleService.getMinId();
		int articleMinId = min == null ? 0 : min;
		JedisUtil.setHashData("SD", "articleMinId", String.valueOf(articleMinId));
		// 设置最大值
		Integer max = articleService.getMaxId();
		int articleMaxId = max == null ? 0 : max;
		JedisUtil.setHashData("SD", "articleMaxId", String.valueOf(articleMaxId));
	}

	private static ArticleService articleService;

	@Autowired(required = true)
	public void setArticleService(ArticleService articleService) {
		DataUtil.articleService = articleService;
	}

	static {
		File f = new File(CLIENT_APK_PATH);
		if (!f.exists())
			f.mkdirs();
		HashSet<String> allTags = new HashSet<String>(Arrays.asList("热门", "热点", "搞笑", "私房", "八卦", "科技", "财经", "汽车",
				"生活", "时尚", "育儿", "旅游", "职场", "美食", "历史", "教育", "星座", "军事", "游戏", "萌宠", "时事", "财经", "美文", "娱乐", "时尚",
				"美食", "体育", "教育", "情感", "生活", "健康", "母婴", "游戏", "家居", "影视", "旅行", "职场", "国际", "IT"));
		JedisUtil.addSetDate("allTags", allTags);
	}

	public static Set<String> getAllTags() {
		return JedisUtil.getSetData("allTags");
	}

	public static void addTags(Set<String> tags) {
		JedisUtil.addSetDate("allTags", tags);
	}

	public static void removeTags(Set<String> tags) {
		JedisUtil.removeSetData("allTags", tags);
	}

	public static String getNewClientPath() {
		return CLIENT_APK_PATH + curClientVersion + ".apk";
	}
}
