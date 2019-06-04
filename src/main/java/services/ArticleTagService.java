package services;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import models.ArticleTag;

@Repository
@Mapper
public interface ArticleTagService {

	@Select("select * from article_tag where aid = #{aid}")
	@Results(id = "TagMap", value = { @Result(column = "aid", id = true, property = "aid"),
			@Result(column = "tag", id = true, property = "tag") })
	List<ArticleTag> getTagsByAid(int aid);

	@Insert("INSERT INTO article_tag VALUES((select id from article where articleUrl = #{url} ),#{tag})")
	Integer insert(@Param("url") String articleUrl, @Param("tag") String tag);

	@Insert("REPLACE INTO article_tag VALUES((select id from article where articleUrl = #{url} ),#{tag})")
	Integer replace(@Param("url") String articleUrl, @Param("tag") String tag);

	@Delete("delete from article_tag where aid not in (select id from article)")
	Integer clear();
}
