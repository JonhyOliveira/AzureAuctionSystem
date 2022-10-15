package scc.data.layers;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisCacheLayer {

	private static final String REDIS_HOSTNAME, REDIS_KEY;
	private static final Integer REDIS_PORT;

	static // read from properties file
	{

		try {
			InputStream fis = RedisCacheLayer.class.getClassLoader().getResourceAsStream("redis.properties");
			Properties props = new Properties();

			props.load(fis);

			REDIS_HOSTNAME = props.getProperty("URI");
			REDIS_KEY = props.getProperty("PKEY");
			REDIS_PORT = Integer.parseInt(props.getProperty("PORT"));

			System.out.printf("Redis = %s:%s\n", REDIS_HOSTNAME, REDIS_PORT);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static JedisPool instance;

	public synchronized static JedisPool getCachePool() {
		if( instance != null)
			return instance;
		final JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);
		instance = new JedisPool(poolConfig, REDIS_HOSTNAME, REDIS_PORT, 1000, REDIS_KEY, true);
		return instance;

	}
}
