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

import models.UserFavoriteTag;

@Repository
@Mapper
public interface UserFavoriteTagService {

	@Select("SELECT `uid`,`tag`FROM `user_favorite_tag` WHERE uid = #{uid}")
	@Results(id = "uftsMap", value = { @Result(column = "id", id = true, property = "id"),
			@Result(column = "uid", property = "uid"), @Result(column = "tag", property = "tag") })
	List<UserFavoriteTag> getTagsByUid(String uid);

	@Select("SELECT * FROM `user_favorite_tag` WHERE uid = #{uid}")
	@ResultType(HashMap.class)
	List<Map<String, String>> getTagsByUidAsMap(String uid);

	@Insert("INSERT INTO `user_favorite_tag`(`uid`,`tag`) values(#{uid},#{tag})")
	Integer insert(@Param("uid") String uid, @Param("tag") String tag);

	@Delete("DELETE FROM `user_favorite_tag` WHERE uid = #{uid}")
	Integer deleteTagsByUid(String uid);

	@Select("SELECT count(*) FROM `user_favorite_tag` WHERE uid = #{uid}")
	Integer getTagsCountByUid(String uid);

}
