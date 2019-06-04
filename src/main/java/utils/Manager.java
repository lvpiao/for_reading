package utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class Manager implements ApplicationContextAware {

	private static ThreadPoolExecutor threadPool;
	private static JedisPool jedisPool;

	static {
		initialPool();
	}

	public static String getThreadPoolInfo() {
		return "activeThread: " + threadPool.getActiveCount() + ",QueCount:" + threadPool.getQueue().size()
				+ ",curThreadInPool" + threadPool.getPoolSize();
	}

	@PreDestroy
	public static void destroy() {
		threadPool.shutdown();
		jedisPool.close();
		System.out.println("成功关闭threadPool和jedisPool！！！");
	}

	public static ThreadPoolExecutor getThreadPool() {
		return threadPool;
	}

	public static JedisPool getRedisPool() {
		return jedisPool;
	}
	
	private static final String host = "47.94.131.93";
	private static final int port = 6379;
	private static final String PASSWORD = "962464";

	private static void initialPool() {
		// 池基本配置
		JedisPoolConfig config = new JedisPoolConfig();
		// 是否启用后进先出, 默认true
		config.setLifo(true);
		// 最大空闲连接数, 默认8个
		config.setMaxIdle(8);
		// 最大连接数, 默认8个
		config.setMaxTotal(8);
		// 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间, 默认-1
		config.setMaxWaitMillis(-1);
		// 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		config.setMinEvictableIdleTimeMillis(1800000);
		// 最小空闲连接数, 默认0
		config.setMinIdle(0);
		// 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
		config.setNumTestsPerEvictionRun(3);
		// 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断
		// (默认逐出策略)
		config.setSoftMinEvictableIdleTimeMillis(1800000);
		// 在获取连接的时候检查有效性, 默认false
		config.setTestOnBorrow(false);
		// 在空闲时检查有效性, 默认false
		config.setTestWhileIdle(false);
		jedisPool = new JedisPool(config, host, port);
		int coreCount = Runtime.getRuntime().availableProcessors();
		System.out.println("cpu 核心为：" + coreCount);
		threadPool = new ThreadPoolExecutor(coreCount * 8, coreCount * 20, 5, TimeUnit.MINUTES,
				new ArrayBlockingQueue<Runnable>(128));
	}

	public static Jedis getResource() {
		Jedis jedis = jedisPool.getResource();
    	jedis.auth(PASSWORD);
		return jedis;
	}

	private static ApplicationContext applicationContext;

	public static ApplicationContext getApp() {
		return applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Manager.applicationContext = applicationContext;
	}
}
