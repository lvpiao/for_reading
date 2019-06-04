package services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;

import models.Article;

@Repository
@Mapper
public interface ArticleService {
//	protected String title;
//	protected String preview;
//	protected String imagePath;
//	protected String author;
//	protected String authorExtra;
//	protected String articleUrl;
//	protected List<String> tags;
//	protected String platform;
	@Select("SELECT `id`,`title`,`preview`,`imagePath`,`author`,`authorExtra`,`articleUrl`,`platform`,`createTime` FROM  `article` WHERE id = #{aid}")
	@Results(id = "ArticleMap", value = { @Result(id = true, column = "id", property = "id"),
			@Result(column = "title", property = "title"), @Result(column = "preview", property = "preview"),
			@Result(column = "imagePath", property = "imagePath"), @Result(column = "author", property = "author"),
			@Result(column = "authorExtra", property = "authorExtra"),
			@Result(column = "articleUrl", property = "articleUrl"),
			@Result(column = "platform", property = "platform"),
			@Result(column = "id", property = "tags", javaType = List.class, many = @Many(fetchType = FetchType.LAZY, select = "services.TagService.getTagsByAid")) })
	public Article getArticleById(int aid);

	@Insert("INSERT INTO `for_reading`.`article`(`title`,`preview`,`imagePath`,`author`,`authorExtra`,`articleUrl`,`platform`)"
			+ "VALUES(#{title},#{preview},#{imagePath},#{author},#{authorExtra},#{articleUrl},#{platform})")
	public Integer insert(Article article);

	@Insert("REPLACE INTO `for_reading`.`article`(`title`,`preview`,`imagePath`,`author`,`authorExtra`,`articleUrl`,`platform`)"
			+ "VALUES(#{title},#{preview},#{imagePath},#{author},#{authorExtra},#{articleUrl},#{platform})")
	public Integer replace(Article article);

	@Delete("truncate `for_reading`.`article`")
	public Integer truncate();

	@Select("SELECT `id`,`title`,`preview`,`imagePath`,`author`,`authorExtra`,`articleUrl`,`platform` FROM `for_reading`.`article` limit 0,10")
	@ResultType(HashMap.class)
	public List<HashMap<String, String>> select();

	@SelectProvider(type = ProvidersHandler.class, method = "getArticleAsList")
	@ResultType(HashMap.class)
	public List<Map<String, String>> getArticleByIds(Collection<Integer> list);

	@Select("select min(id) from article")
	public Integer getMinId();

	@Select("select max(id) from article")
	public Integer getMaxId();

	@Delete("delete from article where platform = '微信'  and createTime <= date_sub(now(), interval 270 MINUTE)")
	public Integer clearExpiredArticle();

	@Select("select count(*) FROM for_reading.article a left join article_tag b on a.id = b.aid where b.tag = #{tag}")
	public Integer getArticleCountByTag(String tag);

	@Select("${sql}")
	@ResultType(HashMap.class)
	public List<Map<String, String>> exeSelect(Map<String, String> map);

	@Select("SELECT a.* FROM article a WHERE MATCH(a.title ,a.preview) AGAINST(#{keyword} IN BOOLEAN MODE) limit #{offset},#{limit}")
	@ResultType(HashMap.class)
	public List<Map<String, String>> search(@Param("keyword") String keyword, @Param("offset") int offset,
			@Param("limit") int limit);

}
