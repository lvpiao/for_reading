package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import models.UserCollectArticle;

@Repository
@Mapper
public interface UserCollectArticleService {
	@Select("SELECT `uid`,`tag`FROM `user_collect_article` WHERE uid = #{uid}")
	@Results(id = "userCollectArticleService", value = { @Result(column = "uid", property = "uid", id = true),
			@Result(column = "article", property = "article", id = true) })
	List<UserCollectArticle> getCollectionsByUid(String uid);

	@Select("SELECT * FROM `user_collect_article` WHERE uid = #{uid}")
	@ResultType(HashMap.class)
	List<Map<String, String>> getCollectionsByUidAsMap(String uid);

	@Insert("INSERT INTO `user_collect_article`(`uid`,`aid`) values(#{uid},#{aid})")
	Integer insert(@Param("uid") String uid, @Param("aid") int aid);

	@Select("SELECT * FROM `user_collect_article` WHERE uid = #{uid} and aid=#{aid}")
	Integer exists(@Param("uid") String uid, @Param("aid") int aid);

	@Delete("DELETE FROM `user_collect_article` WHERE uid = #{uid}")
	Integer deleteCollectionsByUid(String uid);

	@Delete("DELETE FROM `user_collect_article` WHERE uid = #{uid} and aid=#{aid}")
	Integer delete(@Param("uid") String uid, @Param("aid") int aid);

	@Select("SELECT count(*) FROM `user_collect_article` WHERE uid = #{uid}")
	Integer getCollectionsCountByUid(String uid);

	@Select("SELECT * FROM `article` WHERE id IN (SELECT aid FROM user_collect_article WHERE uid = #{uid}) LIMIT #{offset},#{limit}")
	@ResultType(HashMap.class)
	List<Map<String, String>> getUserCollectionsArticleByUid(@Param("uid") String uid, @Param("offset") int offset,
			@Param("limit") int limit);
}
