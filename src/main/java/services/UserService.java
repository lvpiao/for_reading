package services;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import models.User;

@Repository
@Mapper
public interface UserService {

	@Select("select * from `user`")
	@Results(id = "userMap", value = { @Result(id = true, column = "id", property = "id"),
			@Result(column = "nickname", property = "nickname"), @Result(column = "password", property = "password"),
			@Result(column = "sex", property = "sex"), @Result(column = "avatarPath", property = "avatarPath"),
			@Result(column = "createTime", property = "createTime"),
			@Result(column = "permission", property = "permission") })
	public List<User> list();

	@Select("select * from `user` where id = #{id}")
	@ResultMap("userMap")
	public User getUserById(String id);

	@Select("select * from `user` where id = #{id}  and `password` = #{password}")
	@ResultMap("userMap")
	public User getUser(@Param("id") String id, @Param("password") String password);

	@Select("select count(*) from `user` where `id` = #{id} and `password` = #{password}")
	public Integer login(@Param("id") String id, @Param("password") String password);

	@Select("select count(*) from `user` where id = #{id}")
	public Integer exist(String id);

	@Delete("delete from `user` where `id` = #{id} and `password` = #{password}")
//	@ResultMap("userMap")
	public Integer delete(User user);

	// ,`nickname`,`name`,`permission`,`avatarPath`,`sex`
	@Insert("INSERT INTO `user` (`id`,`password`) VALUES(#{id},#{password})")
	public Integer insert(User user);

	// ,`nickname`,`name`,`permission`,`avatarPath`,`sex`
	@Update("UPDATE  `user` set password = #{newPassword} where id = #{uid}")
	public Integer changePassword(@Param("uid") String uid, @Param("newPassword") String newPassword);

	// ,`nickname`,`name`,`permission`,`avatarPath`,`sex`
	@Update("UPDATE `user` set nickname = #{nickname} where id = #{uid}")
	public Integer changeNickname(@Param("uid") String uid, @Param("nickname") String nickname);

}
