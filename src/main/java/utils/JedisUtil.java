package utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class JedisUtil {

	public static void setHashData(String key, Map<String, String> hash) {
		Jedis jedis = Manager.getResource();
		try {
			hash.forEach((k, v) -> {
				jedis.hset(key, k, v);
			});
		} finally {
			jedis.close();
		}
	}

	public static void setHashData(String key, String field, String val) {
		Jedis jedis = Manager.getResource();
		try {
			jedis.hset(key, field, val);
		} finally {
			jedis.close();
		}
	}

	public static int getArtilceMinId() {
		Jedis jedis = Manager.getResource();
		try {
			String articleMinId = jedis.hget("SD", "articleMinId");
			return Integer.parseInt(articleMinId);
		} finally {
			jedis.close();
		}
	}

	public static int getArtilceMaxId() {
		Jedis jedis = Manager.getResource();
		try {
			String articleMaxId = jedis.hget("SD", "articleMaxId");
			return Integer.parseInt(articleMaxId);
		} finally {
			jedis.close();
		}
	}

	/**
	 * @param key 队列的key
	 * @return 队列大小
	 */
	public static int getUrlQueueCount(String domain) {
		Jedis jedis = Manager.getResource();
		try {
			Set<String> keys = jedis.keys("*" + domain);
			Iterator<String> it = keys.iterator();
			if (it.hasNext()) {
				String key = it.next();
				return jedis.llen(key).intValue();
			} else
				return 0;
		} finally {
			jedis.close();
		}
	}

	public static void addSetDate(String key, Set<String> allTags) {
		Jedis jedis = Manager.getResource();
		try {
			allTags.forEach(t -> {
				jedis.sadd(key, t);
			});
		} finally {
			jedis.close();
		}
	}

	public static Set<String> getSetData(String key) {
		Jedis jedis = Manager.getResource();
		try {
			return jedis.smembers(key);
		} finally {
			jedis.close();
		}
	}

	public static void removeSetData(String key, Set<String> tags) {
		Jedis jedis = Manager.getResource();
		try {
			tags.forEach(t -> {
				jedis.srem(key, t);
			});
		} finally {
			jedis.close();
		}
	}
}
