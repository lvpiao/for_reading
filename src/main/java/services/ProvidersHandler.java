package services;

import java.util.Collection;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public class ProvidersHandler {

	public String getArticleAsList(Map<String, Collection<Integer>> map) {
		Collection<Integer> list = map.get("list");
		StringBuffer sql = new StringBuffer("select * from `article` where id in(");
		for (Integer i : list) {
			sql.append(i).append(",");
		}
		sql.setCharAt(sql.length() - 1, ')');
		return sql.toString();
	}
}